package com.github.s0phs.ms_pagamentos.dto;

import com.github.s0phs.ms_pagamentos.entities.Pagamento;
import com.github.s0phs.ms_pagamentos.entities.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PagamentoResponseDTO {

    private Long id;

    private BigDecimal valor;

    private String nome;

    //private String numeroCartao;

    //private String validade;

    //private String codigoSeguranca;

    private Status status;

    private Long pedidoId;

    public PagamentoResponseDTO(Pagamento pagamento) {
        id = pagamento.getId();
        valor = pagamento.getValor();
        nome = pagamento.getNome();
        //numeroCartao = pagamento.getNumeroCartao();
        //validade = pagamento.getValidade();
        //codigoSeguranca = pagamento.getCodigoSeguranca();
        status = pagamento.getStatus();
        pedidoId = pagamento.getPedidoId();
    }
}
