package br.com.everdev.nameresolutiondnsserver.controller;


import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DNSServerHealthCheckController {

    @Autowired
    private EurekaClient eurekaClient;

    @Value("${spring.service-discovery-app}")
    private String applicationDiscoveryURI;

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/health")
    public String healthy() {
        return "Sou o DNS Server e estou online: " + LocalDateTime.now();
    }

    @GetMapping("/getRegisteredApplications")
    public List<String> getRegisteredApplications() {
        List<String> homePageUrls= new ArrayList<>();
        Applications app = eurekaClient.getApplications();
        var applications = app.getRegisteredApplications();

        for(var application : applications){
            homePageUrls.add(application.getInstances().get(0).getHomePageUrl());
        }

        return homePageUrls;
    }

}

