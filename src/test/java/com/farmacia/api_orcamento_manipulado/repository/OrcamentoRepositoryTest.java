package com.farmacia.api_orcamento_manipulado.repository;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.farmacia.api_orcamento_manipulado.repository.OrcamentoRepository;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usa o MySQL real, não o H2
public class OrcamentoRepositoryTest {

    // Dependency Injection (Injeção de Dependência)
    @Autowired
    private OrcamentoRepository repository;

    @Test
    void deveSalvarERecuperarUmOrcamento() {
        long countAntes = repository.count();
        Orcamento orcamento = new Orcamento();
        // Adicione um item para testar o relacionamento
        orcamento.adicionarItem(new ItemOrcamento("Vitamina D", new BigDecimal("30.00")));

        Orcamento salvo = repository.save(orcamento);

        assertNotNull(salvo.getId());
        assertEquals(countAntes + 1, repository.count());
    }

    @Test
    @DisplayName("Deve buscar apenas orçamentos com status PENDENTE")
    void deveBuscarApenasOrcamentosPendentes() {
        Orcamento p1 = new Orcamento();
        p1.setStatus("PENDENTE");
        p1.setClienteWhatsapp("123");

        Orcamento p2 = new Orcamento();
        p2.setStatus("APROVADO");
        p2.setClienteWhatsapp("456");

        repository.save(p1);
        repository.save(p2);

        List<Orcamento> pendentes = repository.findByStatus("PENDENTE");

        assertEquals(1, pendentes.size());
        assertEquals("123", pendentes.get(0).getClienteWhatsapp());
    }

}
