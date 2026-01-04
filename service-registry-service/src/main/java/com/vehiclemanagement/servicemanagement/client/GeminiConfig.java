package com.vehiclemanagement.servicemanagement.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import com.google.genai.Client;

@Configuration
public class GeminiConfig {

	@Value("${gemini.api.key}")
	private String apiKey;

	@Bean
	public Client geminiClient() {
		return new Client.Builder().apiKey(apiKey).build();
	}
}
