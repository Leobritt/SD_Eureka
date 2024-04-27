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

    @PostMapping("/call-dns")
    public Mono<String> callDns(@RequestBody String email) {
        return webClient.get()
                .uri("/getRegisteredApplications")
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    String response1 = "Response from DNS Server: " ;
    
                    return webClient.post()
                            .uri("http://192.168.0.11:8182/validar-email")
                            .bodyValue(email)
                            .retrieve()
                            .bodyToMono(String.class)
                            .flatMap(response2 -> {
                                String response3 = "Response from DNS Server: " + response2;
    
                                return webClient.post()
                                        .uri("http://192.168.0.11:8181/perfil")
                                        .bodyValue(email)
                                        .retrieve()
                                        .bodyToMono(String.class)
                                        .map(response4 -> response1 + ", " + response3 + ", Response from DNS Server: " + response4);
                            });
                });
            }}
