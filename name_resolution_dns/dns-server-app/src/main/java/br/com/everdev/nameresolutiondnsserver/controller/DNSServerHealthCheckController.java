package br.com.everdev.nameresolutiondnsserver.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
public class DNSServerHealthCheckController {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${dns.server.url}")
    private String dnsServerUrl;

    private WebClient webClient;
    private final WebClient.Builder webClientBuilder;

    public DNSServerHealthCheckController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public void init() {
        this.webClient = this.webClientBuilder.baseUrl(dnsServerUrl).build();
    }

    @GetMapping("/health")
    public String healthy() {
        return "Sou o DNS Server e estou online: " + LocalDateTime.now();
    }

    @GetMapping("/call-eureka")
    public Mono<String> callEureka() {
        return webClient.get()
                .uri("/listApplications")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> "Response from Eureka Server: " + response);
    }

}
