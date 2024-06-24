package br.com.everdev.dfsb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DFSBServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DFSBServerApplication.class, args);
	}

}
