package com.github.s0phs.ms.pedidos.dto;

import com.github.s0phs.ms.pedidos.entities.ItemDoPedido;
import com.github.s0phs.ms.pedidos.entities.Pedido;
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
public class PedidoRequestDTO {

    @NotBlank(message = "Nome requerido")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 carateres")
    private String nome;

    //@CPF - valida o cpf
    @NotBlank(message = "CPF requerido")
    @Size(min = 11, max = 11, message = "O CPF deve ter 11 caracteres")
    private String cpf;

    private LocalDate data;

    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    private List<@Valid ItemDoPedidoRequestDTO> itens = new ArrayList<>();

//    public PedidoRequestDTO(Pedido pedido){
//        nome = pedido.getNome();
//        cpf = pedido.getCpf();
//        data = pedido.getData();
//
//        //para os itens do pedido
//        for(ItemDoPedido item : pedido.getItens()){
//            ItemDoPedidoRequestDTO itemDTO = new ItemDoPedidoRequestDTO(item);
//            itens.add(itemDTO);
//        }
//    }
}
