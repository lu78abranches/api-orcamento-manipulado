package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.model.OrcamentoStatus;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrcamentoService {

    private final PriceCalculationEngine priceCalculationEngine;

    public OrcamentoService(PriceCalculationEngine priceCalculationEngine) {
        this.priceCalculationEngine = priceCalculationEngine;
    }

    public Orcamento processarOrcamento(List<ItemOrcamento> itens) {

        BigDecimal total = priceCalculationEngine.calcular(itens);

        Orcamento orcamento = new Orcamento();
        orcamento.setItens(itens);
        orcamento.setValorTotal(total);
        orcamento.setStatus(OrcamentoStatus.PENDENTE_REVISAO);

        return orcamento;
    }
}
