package br.com.everdev.nameresolutiondnsserver.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DNSServerHealthCheckController {

    @Value("${spring.service-discovery-app}")
    private String applicationDiscoveryURI;

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/health")
    public String healthy() {
        return "Sou o DNS Server e estou online: " + LocalDateTime.now();
    }

    // Método para obter aplicativos registrados no servidor
    @GetMapping("/getRegisteredApplications")
    public List<String> getRegisteredApplications() {
        try {
            // Constrói uma solicitação HTTP GET para a URI do servidor de descoberta
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(applicationDiscoveryURI))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            // Envia a solicitação e aguarda a resposta
            HttpResponse<String> response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());

            // Lista temporária para armazenar URLs de aplicativos
            List<String> urls = new ArrayList<>();
            urls.add("http://192.168.0.11:8181/perfil");
            urls.add("http://192.168.0.11/validar");

            // Retorna a lista de URLs dos aplicativos registrados
            return urls;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}

