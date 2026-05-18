package com.farmacia.api_orcamento_manipulado.service;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.repository.OrcamentoRepository;

import org.junit.jupiter.api.DisplayName;
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
        byte[] imagemFake = new byte[] { 1, 2, 3 };
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

    @Test
    @DisplayName("Deve criar um orçamento preliminar com taxa fixa e status pendente")
    void deveCriarOrcamentoPreliminarComSucesso() {
        // Arranjo (Given)
        String clienteWhatsapp = "whatsapp:+5511999999999";
        ItemOrcamento item = new ItemOrcamento();
        item.setNome("Amoxicilina 500mg");
        item.setPreco(BigDecimal.valueOf(25.00));
        List<ItemOrcamento> itensIA = List.of(item);

        Orcamento orcamentoSalvoMock = new Orcamento();
        orcamentoSalvoMock.setId(1L);
        orcamentoSalvoMock.setClienteWhatsapp(clienteWhatsapp);
        orcamentoSalvoMock.setItens(itensIA);

        // Configura o mock do repositório
        when(repository.save(any(Orcamento.class))).thenReturn(orcamentoSalvoMock);

        // Ação (When)
        Orcamento resultado = orcamentoService.criarOrcamentoPreliminar(clienteWhatsapp, itensIA);

        // Asserções (Then)
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getClienteWhatsapp()).isEqualTo(clienteWhatsapp);
        // Verifica se a regra de negócio do cálculo dinâmico foi aplicada (25.00 do
        // item + 10.00 de taxa)
        assertThat(resultado.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(35.00));

        verify(repository).save(any(Orcamento.class));
    }

}
