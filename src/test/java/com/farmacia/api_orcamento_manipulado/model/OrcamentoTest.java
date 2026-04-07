package com.farmacia.api_orcamento_manipulado.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;


// Local: src/test/java/com/farmacia/model/OrcamentoTest.java
public class OrcamentoTest {

    @Test
    void deveIniciarOrcamentoComStatusPendente() {
        Orcamento orcamento = new Orcamento();
        // O teste vai falhar porque a classe Orcamento nem existe ainda! (Fase RED)
        assertEquals("PENDENTE_REVISAO", orcamento.getStatus());
    }
}
