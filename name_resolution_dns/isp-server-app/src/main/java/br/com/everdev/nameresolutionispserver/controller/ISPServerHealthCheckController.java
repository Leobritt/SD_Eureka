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

    @GetMapping("/call-dns")
    public Mono<String> callDns() {
        return webClient.get()
                .uri("/health")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> "Response from DNS Server: " + response);
    }

    @GetMapping("/get-dns-ip")
    public String getDnsIp() {
        try {
            InetAddress address = InetAddress.getByName("localhost");
            return "IP address of DNS Server: " + address.getHostAddress();
        } catch (UnknownHostException e) {
            return "Error: " + e.getMessage();
        }
    }
}
