package com.farmacia.api_orcamento_manipulado.repository;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.farmacia.api_orcamento_manipulado.repository.OrcamentoRepository;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.math.BigDecimal;


    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usa o MySQL real, não o H2
    public class OrcamentoRepositoryTest {

        @Autowired
        private OrcamentoRepository repository;

        @Test
        void deveSalvarERecuperarUmOrcamento() {
            Orcamento orcamento = new Orcamento();
            // Adicione um item para testar o relacionamento
            orcamento.adicionarItem(new ItemOrcamento("Vitamina D", new BigDecimal("30.00")));

            Orcamento salvo = repository.save(orcamento);

            assertNotNull(salvo.getId());
            assertEquals(1, repository.count());
        }
    }


