package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.repository.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok gera o construtor para injeção de dependência,
// padrão Dependency Injection (Injeção de Dependência) Em vez de o Service dar
// um new OrcamentoRepository(), o Spring "injeta" a
// instância pronta. Isso é o que permite o Mocking nos seus testes: você injeta
// um "dublê" no teste e a instância real na produção.

public class OrcamentoService {

    private final OrcamentoRepository repository;
    private final IAReceitaService iaService;

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

    public Orcamento aprovarOrcamento(Long id) {
        // 1. Busca o orçamento na base de dados ou lança exceção caso não encontre
        Orcamento orcamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado para o ID: " + id));

        // 2. Modifica o status para APROVADO (se usar String ou Enum, ajustar aqui)
        // Caso use Enum, mude para: orcamento.setStatus(StatusOrcamento.APROVADO);
        orcamento.setStatus("APROVADO");

        // 3. Persiste a alteração no banco de dados (MySQL local / PostgreSQL do
        // Render)
        return repository.save(orcamento);
    }

    public Orcamento recusarOrcamento(Long id) {
        // 1. Busca o orçamento na base ou lança exceção
        Orcamento orcamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado para o ID: " + id));

        // 2. Modifica o status para RECUSADO (se usar Enum, mude para:
        // orcamento.setStatus(StatusOrcamento.RECUSADO);)
        orcamento.setStatus("RECUSADO");

        // 3. Salva a modificação na base de dados
        return repository.save(orcamento);
    }

}
