package com.farmacia.api_orcamento_manipulado.service;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.repository.OrcamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;

// src/test/java/com/farmacia/api_orcamento_manipulado/service/OrcamentoServiceTest.java
@ExtendWith(MockitoExtension.class)
public class OrcamentoServiceTest {

    @Mock
    private OrcamentoRepository repository;

    @Mock
    private IAReceitaService iaService;

    @InjectMocks
    private OrcamentoService orcamentoService;

    @Test
    void deveProcessarReceitaESalvarOrcamento() {
        // GIVEN (Dado que a IA encontre um item)
        byte[] imagemFake = new byte[]{1, 2, 3};
        List<ItemOrcamento> itensExtraidos = List.of(new ItemOrcamento("Vitamina C", new BigDecimal("20.00")));

        when(iaService.extrairItens(imagemFake)).thenReturn(itensExtraidos);
        when(repository.save(any(Orcamento.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN (Quando o serviço processar)
        Orcamento resultado = orcamentoService.processarNovaReceita(imagemFake);

        // THEN (Então o orçamento deve estar correto)
        assertNotNull(resultado);
        assertEquals(new BigDecimal("30.00"), resultado.getValorTotal()); // 20 + 10 taxa
        verify(repository).save(any(Orcamento.class));
    }
}

