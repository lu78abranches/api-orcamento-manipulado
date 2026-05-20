package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.dto.ItemExtraidoDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoPendenteDTO;
import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.model.OrcamentoStatus;
import com.farmacia.api_orcamento_manipulado.repository.OrcamentoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrcamentoService {

    private final OrcamentoRepository repository;
    private final IAReceitaService iaReceitaService;
    private final PriceCalculationEngine priceCalculationEngine;

    public OrcamentoService(OrcamentoRepository repository,
            IAReceitaService iaReceitaService,
            PriceCalculationEngine priceCalculationEngine) {
        this.repository = repository;
        this.iaReceitaService = iaReceitaService;
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

    public Orcamento processarNovaReceita(byte[] imagem) {
        List<ItemOrcamento> itens = iaReceitaService.extrairItens(imagem);
        return criarOrcamentoPreliminar(null, itens);
    }

    public Orcamento criarOrcamentoPreliminar(String clienteWhatsapp, List<ItemOrcamento> itens) {
        if (itens == null) {
            itens = List.of();
        }

        Orcamento orcamento = new Orcamento();
        orcamento.setClienteWhatsapp(clienteWhatsapp);
        orcamento.setItens(itens);
        orcamento.setValorTotal(priceCalculationEngine.calcular(itens));
        orcamento.setStatus(OrcamentoStatus.PENDENTE_REVISAO);

        return repository.save(orcamento);
    }

    public Orcamento aprovarOrcamento(Long id) {
        Orcamento orcamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado: " + id));

        orcamento.setStatus(OrcamentoStatus.APROVADO);
        if (orcamento.getValorTotal() == null) {
            orcamento.setValorTotal(priceCalculationEngine.calcular(orcamento.getItens()));
        }

        return repository.save(orcamento);
    }

    public Orcamento recusarOrcamento(Long id) {
        Orcamento orcamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado: " + id));

        orcamento.setStatus(OrcamentoStatus.RECUSADO);
        return repository.save(orcamento);
    }

    public List<OrcamentoPendenteDTO> listarPendentes() {
        return repository.findByStatus(OrcamentoStatus.PENDENTE_REVISAO).stream()
                .map(orcamento -> {
                    String status = orcamento.getStatus() == OrcamentoStatus.PENDENTE_REVISAO
                            ? "PENDENTE"
                            : orcamento.getStatus().name();

                    return new OrcamentoPendenteDTO(
                            orcamento.getId(),
                            orcamento.getClienteWhatsapp(),
                            status,
                            orcamento.getValorTotal(),
                            orcamento.getItens().stream()
                                    .map(item -> new ItemExtraidoDTO(item.getNome(), item.getPreco()))
                                    .collect(Collectors.toList()));
                })
                .collect(Collectors.toList());
    }
}
