package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.repository.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok gera o construtor para injeção de dependência
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

