package br.com.everdev.dfsa.controller;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DFSAHealthCheckController {

    @Autowired
    private EurekaClient eurekaClient;

    private WebClient webClient;

    @Autowired
    public DFSAHealthCheckController(WebClient webClient) {
        this.webClient = webClient;
    }

    @Value("${spring.service-discovery-app}")
    private String applicationDiscoveryURI;

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/health")
    public String healthy() {
        return "Sou o DNS Server e estou online: " + LocalDateTime.now();
    }

    @GetMapping("/getRegisteredApplications")
    public List<Application> getRegisteredApplications() {
        Applications app = eurekaClient.getApplications();
        return app.getRegisteredApplications();
    }

    @GetMapping("/verificarArquivo/{nomeArquivo}")
    public Mono<ResponseEntity<String>> verificarArquivoNoDFS(@PathVariable String nomeArquivo) {
        String urlDFSB = "http://localhost:8051/verificar/" + nomeArquivo; // URL do DFSB
        String urlDFSC = "http://localhost:8053/verificar/" + nomeArquivo; // URL do DFSC

        return webClient.get()
                .uri(urlDFSB)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    if ("Presente".equals(response)) {
                        return Mono.just(ResponseEntity.ok("Arquivo encontrado no DFSB."));
                    } else {
                        // Se não encontrado no DFSB, tenta no DFSC
                        return webClient.get()
                                .uri(urlDFSC)
                                .retrieve()
                                .bodyToMono(String.class)
                                .map(respDFSC -> "Presente".equals(respDFSC)
                                        ? ResponseEntity.ok("Arquivo encontrado no DFSC.")
                                        : ResponseEntity.ok("Arquivo não encontrado em nenhum DFS."));
                    }
                })
                .defaultIfEmpty(ResponseEntity.badRequest().body("Erro ao verificar o arquivo."));
    }

}
