package br.com.everdev.nameresolutionispserver.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@RestController
public class ISPServerHealthCheckController {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${dns.server.url}")
    private String dnsServerUrl;

    private WebClient webClient;
    private final WebClient.Builder webClientBuilder;

    public ISPServerHealthCheckController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public void init() {
        this.webClient = this.webClientBuilder.baseUrl(dnsServerUrl).build();
    }

    @GetMapping("/health")
    public String healthy() {
        return "Sou o ISP Server e estou online!" + LocalDateTime.now();
    }

    @PostMapping("/validacao")
public Mono<String> callDns(@RequestBody String email) {
    // Utiliza o WebClient para enviar uma requisição GET para obter a lista de aplicativos registrados
    return webClient.get()
            .uri("/getRegisteredApplications")
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(response -> {
                // Concatena uma resposta inicial indicando o retorno do servidor DNS
                String response1 = "Response from DNS Server autentication: \n";
    
                // Após receber a resposta, envia duas requisições POST para diferentes endpoints
                return webClient.post()
                        .uri("http://192.168.0.11:8182/validar-email")
                        .bodyValue(email)
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(response2 -> {
                            // Concatena a resposta da segunda requisição ao servidor DNS
                            String response3 =  response2;
    
                            // Envia uma terceira requisição POST para outro endpoint
                            return webClient.post()
                                    .uri("http://192.168.0.11:8181/perfil")
                                    .bodyValue(email)
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    // Retorna uma Mono com a resposta combinada de todas as requisições
                                    .map(response4 -> response1 +  response3 + "\nPerfil do email solicitado:\n  " + response4);
                        });
            });
}}
