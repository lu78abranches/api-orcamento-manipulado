package com.farmacia.api_orcamento_manipulado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })

public class ApiOrcamentoManipuladoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiOrcamentoManipuladoApplication.class, args);
	}

}
