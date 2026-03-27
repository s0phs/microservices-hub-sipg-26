package com.github.s0phs.ms.pedidos.dto;

import com.github.s0phs.ms.pedidos.entities.ItemDoPedido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ItemDoPedidoDTO {

    private Long id;

    @NotNull(message = "Quantidade requerido")
    @Positive(message = "Quantidade deve ser um número positivo")
    private Integer quantidade;

    @NotBlank(message = "Descrição requerido")
    private String descricao;

    @NotNull(message = "Preço unitário é requerido")
    @Positive(message = "O preço unitário deve ser um valor positivo e maior que zero")
    private BigDecimal precoUnitario;

    public ItemDoPedidoDTO(ItemDoPedido itemDoPedido){
        id = itemDoPedido.getId();
        quantidade = itemDoPedido.getQuantidade();
        descricao = itemDoPedido.getDescricao();
        precoUnitario = itemDoPedido.getPrecoUnitario();
    }
}
