package br.com.everdev.nameresolutiondnsserver.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class DNSServerHealthCheckController {

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/health")
    public String healthy() {
        return "Sou o DNS Server e estou online: " + LocalDateTime.now();
    }
}
