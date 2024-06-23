package br.com.everdev.dfsa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DFSAServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DFSAServerApplication.class, args);
	}

}
