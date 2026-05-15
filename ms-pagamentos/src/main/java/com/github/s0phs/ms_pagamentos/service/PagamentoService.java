package com.github.s0phs.ms_pagamentos.service;

import com.github.s0phs.ms_pagamentos.client.PedidoClient;
import com.github.s0phs.ms_pagamentos.dto.PagamentoDTO;
import com.github.s0phs.ms_pagamentos.entities.Pagamento;
import com.github.s0phs.ms_pagamentos.entities.Status;
import com.github.s0phs.ms_pagamentos.exceptions.PagamentoAprovadoException;
import com.github.s0phs.ms_pagamentos.exceptions.ResourceNotFoundException;
import com.github.s0phs.ms_pagamentos.repositories.PagamentoRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;


    @Autowired
    private PedidoClient pedidoClient;

    @Transactional(readOnly = true)
    public List<PagamentoDTO> findAllPagamentos(){
        List<Pagamento> pagamentos = pagamentoRepository.findAll();

        return pagamentos.stream().map(PagamentoDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public PagamentoDTO findPagamentoById(Long id){

        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO savePagamento(PagamentoDTO pagamentoDTO){
        Pagamento pagamento = new Pagamento();

        copyDtoToPagamento(pagamentoDTO, pagamento);

        pagamento.setStatus(Status.CRIADO);
        pagamento = pagamentoRepository.save(pagamento);

        return new PagamentoDTO(pagamento);
    }

    private void copyDtoToPagamento(PagamentoDTO pagamentoDTO, Pagamento pagamento) {

        pagamento.setValor(pagamentoDTO.getValor());
        pagamento.setNome(pagamentoDTO.getNome());
        pagamento.setNumeroCartao(pagamentoDTO.getNumeroCartao());
        pagamento.setValidade(pagamentoDTO.getValidade());
        pagamento.setCodigoSeguranca(pagamentoDTO.getCodigoSeguranca());
        pagamento.setPedidoId(pagamentoDTO.getPedidoId());
    }

    @Transactional
    public PagamentoDTO updatePagamento( Long id, PagamentoDTO pagamentoDTO){
         try {

             if (pagamentoDTO.getStatus().equals(Status.APROVADO)){
                 throw new PagamentoAprovadoException(
                         String.format("Pagamento id %d já está ARPOVADO e não pode ser alterado", id)
                 );
             }
             Pagamento pagamento = pagamentoRepository.getReferenceById(id);
             copyDtoToPagamento(pagamentoDTO, pagamento);
             pagamento.setStatus(pagamentoDTO.getStatus());
             pagamento = pagamentoRepository.save(pagamento);

             return new PagamentoDTO(pagamento);

         }catch (EntityNotFoundException e) {
             throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
         }
    }


    @Transactional
    public void deletePagamentoById(Long id){

        if(!pagamentoRepository.existsById(id)){
            throw new ResourceNotFoundException("Recurso não encontrado. ID: " + id);
        }
        pagamentoRepository.deleteById(id);
    }

    ////////////////////////////////////////
    @Transactional
    public PagamentoDTO confirmarPagamentoDoPedido(Long id){
        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        pagamento.setStatus(Status.APROVADO);
        pagamentoRepository.save(pagamento);

        try {
            pedidoClient.confirmarPagamento(pagamento.getPedidoId());
        }catch (FeignException.NotFound e) {// 404 do ms-pedidos
            //não existe pedido para receber a confirmação
            throw new ResourceNotFoundException("Pedido não encontrado. ID: " + pagamento.getPedidoId());
        }catch (FeignException e) {
            //outros erros (400/500/timeout etc.)
            throw new RuntimeException("Falha ao comunicar com ms-pedidos", e);
        }
        return new PagamentoDTO(pagamento);
    }
}
