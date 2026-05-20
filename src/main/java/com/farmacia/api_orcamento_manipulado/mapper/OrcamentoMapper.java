package com.farmacia.api_orcamento_manipulado.mapper;

import com.farmacia.api_orcamento_manipulado.dto.ItemOrcamentoDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoResponseDTO;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;

import java.util.stream.Collectors;

public class OrcamentoMapper {

    public static OrcamentoResponseDTO toResponse(Orcamento orcamento) {

        OrcamentoResponseDTO dto = new OrcamentoResponseDTO();

        dto.setProtocolo(orcamento.getId());
        dto.setStatus(orcamento.getStatus().name());
        dto.setValorTotal(orcamento.getValorTotal());
        dto.setMensagem("Orçamento processado com sucesso");

        dto.setItens(
                orcamento.getItens().stream()
                        .map(i -> new ItemOrcamentoDTO(i.getNome(), i.getPreco()))
                        .collect(Collectors.toList()));

        return dto;
    }
}
