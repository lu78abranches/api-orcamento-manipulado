package com.farmacia.api_orcamento_manipulado.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrcamentoResponseDTO {

    private Long protocolo;
    private String status;
    private String mensagem;
    private BigDecimal valorTotal;
    private List<ItemOrcamentoDTO> itens;
}
