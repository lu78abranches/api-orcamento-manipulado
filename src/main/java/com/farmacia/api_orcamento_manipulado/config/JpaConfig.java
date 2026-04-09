package com.farmacia.api_orcamento_manipulado.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Configuration
@EnableJpaRepositories("com.farmacia.api_orcamento_manipulado.repository")
@EntityScan("com.farmacia.api_orcamento_manipulado.model")
public class JpaConfig {
}

