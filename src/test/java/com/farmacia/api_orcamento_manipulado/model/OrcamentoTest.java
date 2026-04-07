package com.farmacia.api_orcamento_manipulado.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;

import java.math.BigDecimal;


// Local: src/test/java/com/farmacia/model/OrcamentoTest.java
public class OrcamentoTest {

    @Test
    void deveIniciarOrcamentoComStatusPendente() {
        Orcamento orcamento = new Orcamento();
        // O teste vai falhar porque a classe Orcamento nem existe ainda! (Fase RED)
        assertEquals("PENDENTE_REVISAO", orcamento.getStatus());
    }

    //método adicionado ao OrcamentoTest.java. O objetivo é: adicionar dois itens de
    // R\( 20,00 cada e verificar se o total é R\) 50,00 (considerando uma taxa fixa de R$ 10,00).

    @Test
    void deveCalcularValorTotalComTaxaFixa() {
        Orcamento orcamento = new Orcamento();

        // Imagine que temos um método para adicionar itens
        orcamento.adicionarItem("Vitamina C", new BigDecimal("20.00"));
        orcamento.adicionarItem("Zinco", new BigDecimal("20.00"));

        // O total esperado é 20 + 20 + 10 (taxa) = 50
        assertEquals(new BigDecimal("50.00"), orcamento.getValorTotal());
    }
}
