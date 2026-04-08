package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.repository.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok gera o construtor para injeção de dependência,
// padrão Dependency Injection (Injeção de Dependência) Em vez de o Service dar um new OrcamentoRepository(), o Spring "injeta" a
// instância pronta. Isso é o que permite o Mocking nos seus testes: você injeta um "dublê" no teste e a instância real na produção.

public class OrcamentoService {

    private final OrcamentoRepository repository;
    private final IAReceitaService iaService;

    public Orcamento processarNovaReceita(byte[] imagem) {
        List<ItemOrcamento> itens = iaService.extrairItens(imagem);

        Orcamento orcamento = new Orcamento();
        itens.forEach(orcamento::adicionarItem);

        return repository.save(orcamento);
    }
}

