package com.github.s0phs.ms.pedidos.services;

import com.github.s0phs.ms.pedidos.dto.ItemDoPedidoDTO;
import com.github.s0phs.ms.pedidos.dto.PedidoDTO;
import com.github.s0phs.ms.pedidos.entities.ItemDoPedido;
import com.github.s0phs.ms.pedidos.entities.Pedido;
import com.github.s0phs.ms.pedidos.entities.Status;
import com.github.s0phs.ms.pedidos.exceptions.PedidoPagoException;
import com.github.s0phs.ms.pedidos.exceptions.ResourceNotFoundException;
import com.github.s0phs.ms.pedidos.repositories.ItemDoPedidoRepository;
import com.github.s0phs.ms.pedidos.repositories.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemDoPedidoRepository itemDoPedidoRepository;

    @Transactional(readOnly = true)
    public List<PedidoDTO> findAllPedidos(){

        return pedidoRepository.findAll().stream().map(PedidoDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public PedidoDTO findPedidoById(Long id){

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );
        return new PedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO savePedido(PedidoDTO pedidoDTO){

        Pedido pedido = new Pedido();
        pedido.setData(LocalDate.now());
        pedido.setStatus(Status.CRIADO);
        mapDtoToPedido(pedidoDTO, pedido);
        pedido.calcularValorTotalDoProduto();
        pedido = pedidoRepository.save(pedido);

        return new PedidoDTO(pedido);
    }

    private void mapDtoToPedido(PedidoDTO pedidoDTO, Pedido pedido) {

        pedido.setNome(pedidoDTO.getNome());
        pedido.setCpf(pedidoDTO.getCpf());

        for(ItemDoPedidoDTO itemDTO : pedidoDTO.getItens()){
            ItemDoPedido itemPedido = new ItemDoPedido();
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setDescricao(itemDTO.getDescricao());
            itemPedido.setPrecoUnitario(itemDTO.getPrecoUnitario());
            itemPedido.setPedido(pedido);
            pedido.getItens().add(itemPedido);
        }
    }

    @Transactional
    public PedidoDTO updatePedido(Long id, PedidoDTO pedidoDTO){

        try{
            Pedido pedido = pedidoRepository.getReferenceById(id);

            ////////////////
            if(pedido.getStatus().equals(Status.PAGO)){
                throw new PedidoPagoException(
                        String.format("Pedido id: %d já está PAGO e não pod ser alterado", id)
                );
            }
            ////////////////

            pedido.getItens().clear();
            pedido.setData(LocalDate.now());
            //pedido.setStatus(Status.CRIADO);
            mapDtoToPedido(pedidoDTO,pedido);
            pedido.calcularValorTotalDoProduto();
            pedido = pedidoRepository.save(pedido);

            return new PedidoDTO(pedido);
        }catch(EntityNotFoundException e){
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }
    }

    @Transactional
    public void deletePedidoById(Long id){

        if(!pedidoRepository.existsById(id)){
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }

        pedidoRepository.deleteById(id);
    }
    /////////////////////////////////////
    @Transactional
    public void confirmarPagamento(Long id){

        Optional<Pedido> pedido = pedidoRepository.findById(id);

        if(pedido.isEmpty()){
            throw new ResourceNotFoundException("Pedido não encontrado. ID: " + id);
        }

        pedido.get().setStatus(Status.PAGO);
        pedidoRepository.save(pedido.get());
    }
}
