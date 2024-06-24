package br.com.everdev.dfsa.controller;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.ok("Arquivo encontrado no DFSB."));
                    } else if (response.statusCode().is4xxClientError()) {
                        return webClient.get()
                                .uri(urlDFSC)
                                .retrieve()
                                .bodyToMono(String.class)
                                .map(respDFSC -> "Presente".equals(respDFSC)
                                        ? ResponseEntity.ok("Arquivo encontrado no DFSC.")
                                        : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Arquivo não encontrado em nenhum DFS."));
                    } else {
                        return Mono.just(ResponseEntity.status(response.statusCode())
                                .body("Erro ao verificar o arquivo."));
                    }
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Arquivo não encontrado em nenhum DFS."));
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erro ao verificar o arquivo: " + ex.getMessage()));
                });
    }
}
