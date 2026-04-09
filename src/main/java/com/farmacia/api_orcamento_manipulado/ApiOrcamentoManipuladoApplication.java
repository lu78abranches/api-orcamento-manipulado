package com.farmacia.api_orcamento_manipulado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;
@SpringBootApplication
@EnableJpaRepositories("com.farmacia.api_orcamento_manipulado.repository") // Garante que o JPA ache o Repo
@EntityScan("com.farmacia.api_orcamento_manipulado.model") // Garante que o JPA ache as Entidades




public class ApiOrcamentoManipuladoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiOrcamentoManipuladoApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
