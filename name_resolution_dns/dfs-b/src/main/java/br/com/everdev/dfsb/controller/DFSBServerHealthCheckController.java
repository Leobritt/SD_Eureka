package br.com.everdev.dfsb.controller;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class DFSBServerHealthCheckController {

    @Autowired
    private EurekaClient eurekaClient;

    @Value("${spring.service-discovery-app}")
    private String applicationDiscoveryURI;

    @Value("${spring.application.name}")
    private String appName;

    List<String> mockFilePaths = Arrays.asList(
            "mock_data/file1.txt",
            "mock_data/file2.txt",
            "mock_data/file3.txt");

    @GetMapping("/health")
    public String healthy() {
        return "Sou o DNS Server e estou online: " + LocalDateTime.now();
    }

    @GetMapping("/getRegisteredApplications")
    public List<Application> getRegisteredApplications() {
        Applications app = eurekaClient.getApplications();
        return app.getRegisteredApplications();
    }

    @GetMapping("/verificarArquivo/{nomeArquivo}")
    public String verificarArquivoNoDFS(@PathVariable String nomeArquivo) {
        return "Arquivo encontrado no DFSB.";
    }

    @GetMapping("/verificar/{nomeArquivo}")
    public ResponseEntity<String> verificarArquivoMockado(@PathVariable String nomeArquivo) {
        boolean arquivoExiste = mockFilePaths.stream()
                .anyMatch(filePath -> filePath.endsWith(nomeArquivo));
        if (arquivoExiste) {
            return ResponseEntity.ok("Presente");
        } else {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                    .body("Ausente");
        }
    }
}
