package com.farmacia.api_orcamento_manipulado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
@SpringBootApplication
public class ApiOrcamentoManipuladoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiOrcamentoManipuladoApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
