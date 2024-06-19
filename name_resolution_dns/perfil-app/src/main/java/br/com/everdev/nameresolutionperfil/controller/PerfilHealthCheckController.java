package br.com.everdev.nameresolutionperfil.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private Map<String, String> dados = new HashMap<>();

    public PerfilHealthCheckController() {
        dados.put("leo.pardo@gmail.com", "Aluno");
        dados.put("everton@pro.ucsal.br", "Professor");
        dados.put("gustavocaste@gmail.com", "Funcionario");
    }

    @GetMapping("/profile-data")
    public ResponseEntity<byte[]> getProfileData() {
        try {
            Path filePath = Paths
                    .get("../perfil-app/src/main/java/br/com/everdev/nameresolutionperfil/files/profile-data.txt");
            File file = filePath.toFile();

            Path directoryPath = filePath.getParent();
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            FileWriter writer = new FileWriter(file);
            for (Map.Entry<String, String> entry : dados.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            writer.close();

            byte[] fileContent = Files.readAllBytes(file.toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=profile-data.txt");

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/uploadTexto")
    public ResponseEntity<String> uploadTexto(@RequestPart() MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            return new ResponseEntity<>("O arquivo está vazio", HttpStatus.BAD_REQUEST);
        }

        try {
            String conteudo = new String(arquivo.getBytes(), StandardCharsets.UTF_8);
            return new ResponseEntity<>("Arquivo recebido com sucesso!", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/perfil")
    public ResponseEntity<String> getPerfil(@RequestBody String email) {
        String perfil = dados.get(email);
        if (perfil != null) {
            return ResponseEntity.ok("E-mail: " + email + ", Perfil: " + perfil);
        } else {
            return ResponseEntity.ok("Usuário não tem perfil");
        }
    }
}