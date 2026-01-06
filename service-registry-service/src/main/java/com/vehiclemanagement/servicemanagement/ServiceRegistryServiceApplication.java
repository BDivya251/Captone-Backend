package com.vehiclemanagement.servicemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceRegistryServiceApplication {

	public static void main(String[] args) {
		loadEnvVariables();
		SpringApplication.run(ServiceRegistryServiceApplication.class, args);
	}

	private static void loadEnvVariables() {
		try {
			io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure()
					.directory("../")
					.ignoreIfMissing()
					.load();

			dotenv.entries().forEach(entry -> {
				System.setProperty(entry.getKey(), entry.getValue());
				if ("GEMINI_API_KEY".equalsIgnoreCase(entry.getKey())) {
					System.setProperty("gemini.api.key", entry.getValue());
				}
			});

			System.out.println("Loaded .env file successfully.");
		} catch (Exception e) {
			System.out.println(".env file not found or could not be loaded, using fallback.");
		}
	}

}
