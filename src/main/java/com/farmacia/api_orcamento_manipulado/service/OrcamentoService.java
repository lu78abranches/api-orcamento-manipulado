package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.dto.ItemExtraidoDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoPendenteDTO;
import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.repository.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrcamentoService {

    private final OrcamentoRepository repository;
    private final IAReceitaService iaService;
    // 1. Injetando o motor matemático mantendo o padrão do @RequiredArgsConstructor
    private final PriceCalculationEngine priceCalculationEngine;

    public Orcamento processarNovaReceita(byte[] imagem) {
        List<ItemOrcamento> itens = iaService.extrairItens(imagem);

        Orcamento orcamento = new Orcamento();
        itens.forEach(orcamento::adicionarItem);

        return repository.save(orcamento);
    }

    public Orcamento criarOrcamentoPreliminar(String clienteWhatsapp, List<ItemOrcamento> itens) {
        Orcamento orcamento = new Orcamento();
        orcamento.setClienteWhatsapp(clienteWhatsapp);
        itens.forEach(orcamento::adicionarItem);
        return repository.save(orcamento);
    }

    @Transactional // Garante consistência atômica no banco de dados
    public Orcamento aprovarOrcamento(Long id) {
        // 1. Busca o orçamento na base de dados ou lança exceção caso não encontre
        Orcamento orcamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado para o ID: " + id));

        // Validação de estado (Defesa contra bugs de duplo clique no painel)
        if ("APROVADO".equals(orcamento.getStatus()) || "RECUSADO".equals(orcamento.getStatus())) {
            throw new IllegalStateException("Apenas orçamentos pendentes de revisão podem ser aprovados.");
        }

        // 2. Integração do motor matemático: calcula e define o preço final real
        BigDecimal valorTotal = priceCalculationEngine.calcular(orcamento.getItens());
        orcamento.setValorTotal(valorTotal);

        // 3. Modifica o status para APROVADO
        orcamento.setStatus("APROVADO");

        // 4. Persiste a alteração no banco de dados
        return repository.save(orcamento);
    }

    @Transactional
    public Orcamento recusarOrcamento(Long id) {
        // 1. Busca o orçamento na base ou lança exceção
        Orcamento orcamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado para o ID: " + id));

        // Validação de estado
        if ("APROVADO".equals(orcamento.getStatus()) || "RECUSADO".equals(orcamento.getStatus())) {
            throw new IllegalStateException("Apenas orçamentos pendentes de revisão podem ser recusados.");
        }

        // 2. Modifica o status para RECUSADO
        orcamento.setStatus("RECUSADO");

        // 3. Salva a modificação na base de dados
        return repository.save(orcamento);
    }

    @Transactional(readOnly = true)
    public List<OrcamentoPendenteDTO> listarPendentes() {
        return repository.findByStatus("PENDENTE").stream()
                .map(orcamento -> new OrcamentoPendenteDTO(
                        orcamento.getId(),
                        orcamento.getClienteWhatsapp(),
                        orcamento.getStatus(),
                        orcamento.getValorTotal(), // Usa o método corrigido com fallback
                        orcamento.getItens().stream()
                                .map(item -> new ItemExtraidoDTO(item.getNome(), item.getPreco()))
                                .toList()))
                .toList();
    }

}
