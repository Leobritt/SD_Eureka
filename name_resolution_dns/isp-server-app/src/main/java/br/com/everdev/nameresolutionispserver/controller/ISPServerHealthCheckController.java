package br.com.everdev.nameresolutionispserver.controller;

import br.com.everdev.nameresolutionispserver.util.Constants;
import com.netflix.discovery.shared.Application;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

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
        return webClient.get()
                .uri("http://localhost:8081/getRegisteredApplications")
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> System.out.println("Response from getRegisteredApplications: " + response))
                .flatMap(response -> {
                    String response1 = "Response from DNS Server autentication: \n";

                    return webClient.post()
                            .uri("http://localhost:8182/validar-email")
                            .bodyValue(email)
                            .retrieve()
                            .bodyToMono(String.class)
                            .doOnNext(response2 -> System.out.println("Response from validar-email: " + response2))
                            .flatMap(response2 -> {
                                String response3 = response2;

                                return webClient.post()
                                        .uri("http://localhost:8191/perfil")
                                        .bodyValue(email)
                                        .retrieve()
                                        .bodyToMono(String.class)
                                        .doOnNext(response4 -> System.out.println("Response from perfil: " + response4))
                                        .map(response4 -> response1 + response3 + "\nPerfil do email solicitado:\n  " + response4);
                            });
                })
                .doOnError(error -> System.err.println("Error occurred: " + error.getMessage()))
                .onErrorResume(error -> Mono.just("Error occurred during the process: " + error.getMessage()));
    }

    @PostMapping("/validate")
    public Mono<String> validate(@RequestBody ISPEmailDTO dto) {
        if (dto.email().equals(null) || dto.email().equals("")) throw new IllegalArgumentException("");

        var applications = getRegisteredApplications().block();
        String validateEmailUrl = null;

        for(var application : applications) {
            if (application.getName().equals(Constants.AppName.validacaoApp)) {
                validateEmailUrl = application.getInstances().stream().findFirst().get().getHomePageUrl();
            }
        }

        return webClient.post()
                .uri(validateEmailUrl + Constants.EndpointRequests.validateEmailEndpoint)
                .bodyValue(dto.email())
                .retrieve()
                .bodyToMono(String.class);

    }

    public Mono<List<Application>> getRegisteredApplications() {
        return webClient.get()
                .uri(Constants.EndpointRequests.registeredApplicationsEndpoint)
                .retrieve()
                .bodyToFlux(Application.class)
                .collectList();
    }

    @PostMapping("/validate-email")
    public Mono<String> callEmail(@RequestBody String email) {
        return webClient.post()
                .uri("http://localhost:8182/validar-email")
                .bodyValue(email)
                .retrieve()
                .bodyToMono(String.class);
    }
}
