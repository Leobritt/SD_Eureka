package br.com.everdev.nameresolutionperfil.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
// Declaração do HashMap e inicialização
    private Map<String, String> dados = new HashMap<>(); 

// Construtor da classe, onde são inseridos dados no HashMap
public PerfilHealthCheckController() {
    dados.put("leo.pardo@gmail.com", "Aluno");
    dados.put("everton@pro.ucsal.br", "Professor");
    dados.put("gustavocaste@gmail.com", "Funcionario");
}

// Método para obter todos os dados do perfil
@GetMapping("/dados")
public Map<String, String> getDados() {
    return dados;
}

// Método para obter o perfil com base no e-mail fornecido
@PostMapping("/perfil")
public ResponseEntity<String> getPerfil(@RequestBody String email) {

// Obtém o perfil associado ao e-mail do HashMap
    String perfil = dados.get(email);
    if (perfil != null) {

// Se o perfil existe, retorna uma resposta OK com o perfil correspondente ao e-mail
        return ResponseEntity.ok("E-mail: " + email + ", Perfil: " + perfil);
    } else {

// Se o perfil não existe, retorna uma resposta OK indicando que o usuário não tem perfil
        return ResponseEntity.ok("Usuário não tem perfil");
    }
}
}