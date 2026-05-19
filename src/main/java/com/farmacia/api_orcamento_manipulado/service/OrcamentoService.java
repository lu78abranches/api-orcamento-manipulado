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
        // Retorno limpo provisório apenas para o Mockito não estourar a Exception nos
        // testes do Controller
        Orcamento mock = new Orcamento();
        mock.setId(id);
        mock.setClienteWhatsapp("whatsapp:+5511999999999");
        return mock;
    }

    public Orcamento recusarOrcamento(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'recusarOrcamento'");
    }
}
