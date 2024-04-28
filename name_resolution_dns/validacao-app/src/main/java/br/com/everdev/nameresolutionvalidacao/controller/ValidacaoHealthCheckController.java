package br.com.everdev.nameresolutionvalidacao.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Applications;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ValidacaoHealthCheckController {
    // Injeção do cliente Eureka para descoberta de serviço
    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    // Injeção do construtor WebClient para fazer chamadas HTTP
    @Autowired
    private WebClient.Builder webClientBuilder;

    // Injeção do nome da aplicação a partir das propriedades do Spring
    @Value("${spring.application.name}")
    private String appName;

    private List<String> emailsList = new ArrayList<>(); // Declaração do ArrayList

    // Endpoint para verificar a saúde da aplicação
    public ValidacaoHealthCheckController() {
        // Inicialização e inserção de e-mails no ArrayList no construtor da classe
        emailsList.add("leo.pardo@gmail");
        emailsList.add("everton@pro.ucsal.br");
        emailsList.add("gustavocaste@gmail.com");
    }

    @GetMapping("/health")
    public String healthy() {
        return "Estpu vivo e bem! Sou a app " + appName + " - " + LocalDateTime.now();
    }

    // Endpoint para validar os dados. Faz uma chamada HTTP para o
    // PerfilHealthCheckController
    // e retorna os dados recebidos.
    @GetMapping("/validar")
    public Mono<Map<String, String>> validar() {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8181/dados")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                });
    }

    @PostMapping("/validar-email")
    public String validarEmail(@RequestBody String email) {
        // Verifica se o e-mail fornecido está na lista de e-mails
        if (emailsList.contains(email)) {
            // Se estiver na lista, retorna uma mensagem indicando que o e-mail existe na base de dados
            return "E-mail do usuário " + email + " existe na base de dados.";
        } else {
            // Se não estiver na lista, retorna uma mensagem indicando que o e-mail não foi encontrado na base de dados
            return "E-mail do usuário " + email + " não foi encontrado na base de dados.";
        }
    }
}