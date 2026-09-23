package com.github.s0phs.ms.pedidos.dto;

import com.github.s0phs.ms.pedidos.entities.ItemDoPedido;
import com.github.s0phs.ms.pedidos.entities.Pedido;
import com.github.s0phs.ms.pedidos.entities.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PedidoResponseDTO {

    private Long id;

    private String nome;

    private String cpf;

    private LocalDate data;

    private Status status;

    private BigDecimal valorTotal;

    private List<@Valid ItemDoPedidoResponseDTO> itens = new ArrayList<>();

    public PedidoResponseDTO(Pedido pedido){
        id = pedido.getId();
        nome = pedido.getNome();
        cpf = pedido.getCpf();
        data = pedido.getData();
        status = pedido.getStatus();
        valorTotal = pedido.getValorTotal();

        //para os itens do pedido
        for(ItemDoPedido item : pedido.getItens()){
            ItemDoPedidoResponseDTO itemDTO = new ItemDoPedidoResponseDTO(item);
            itens.add(itemDTO);
        }
    }
}
