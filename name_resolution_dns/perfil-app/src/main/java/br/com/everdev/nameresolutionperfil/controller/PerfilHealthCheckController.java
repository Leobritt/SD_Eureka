package br.com.everdev.nameresolutionperfil.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

@RestController
public class PerfilHealthCheckController {
    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/health")
    public String healthy() {
        return "Estpu vivo e bem! Sou a app " + appName + " - " + LocalDateTime.now();
    }
    private Map<String, String> dados = new HashMap<>(); // Declaração do HashMap

    public PerfilHealthCheckController() {
        // Inicialização e inserção de dados no HashMap no construtor da classe
        dados.put("leo.pardo@gmail", "Aluno");
        dados.put("everton@pro.ucsal.br", "Professor");
        dados.put("gustavocaste@gmail.com", "Funcionario");
      
    }
}
