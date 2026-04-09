package com.farmacia.api_orcamento_manipulado;

import com.farmacia.api_orcamento_manipulado.repository.OrcamentoRepository;
import com.farmacia.api_orcamento_manipulado.service.IAReceitaService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"DB_USERNAME=sa",
		"DB_PASSWORD=",
		"OPENAI_API_KEY=teste",
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class ApiOrcamentoManipuladoApplicationTests {
	// Mocks para evitar chamadas reais
	@MockitoBean private IAReceitaService iaService;
	@MockitoBean private OrcamentoRepository repository;

	@Test
	void contextLoads() {}
}


