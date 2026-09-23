package com.github.s0phs.ms_pagamentos.service;

import com.github.s0phs.ms_pagamentos.client.PedidoClient;
import com.github.s0phs.ms_pagamentos.dto.PagamentoRequestDTO;
import com.github.s0phs.ms_pagamentos.dto.PagamentoResponseDTO;
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
    public List<PagamentoResponseDTO> findAllPagamentos(){
        List<Pagamento> pagamentos = pagamentoRepository.findAll();

        return pagamentos.stream().map(PagamentoResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public PagamentoResponseDTO findPagamentoById(Long id){

        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado. ID: " + id)
        );

        return new PagamentoResponseDTO(pagamento);
    }

    @Transactional
    public PagamentoResponseDTO savePagamento(PagamentoRequestDTO pagamentoDTO){
        Pagamento pagamento = new Pagamento();

        copyDtoToPagamento(pagamentoDTO, pagamento);

        pagamento.setStatus(Status.CRIADO);
        pagamento = pagamentoRepository.save(pagamento);

        return new PagamentoResponseDTO(pagamento);
    }

    private void copyDtoToPagamento(PagamentoRequestDTO pagamentoDTO, Pagamento pagamento) {

        pagamento.setValor(pagamentoDTO.getValor());
        pagamento.setNome(pagamentoDTO.getNome());
        pagamento.setNumeroCartao(pagamentoDTO.getNumeroCartao());
        pagamento.setValidade(pagamentoDTO.getValidade());
        pagamento.setCodigoSeguranca(pagamentoDTO.getCodigoSeguranca());
        pagamento.setPedidoId(pagamentoDTO.getPedidoId());
    }

    @Transactional
    public PagamentoResponseDTO updatePagamento(Long id, PagamentoRequestDTO pagamentoDTO){
         try {
             Pagamento pagamento = pagamentoRepository.getReferenceById(id);

             copyDtoToPagamento(pagamentoDTO, pagamento);
             pagamento = pagamentoRepository.save(pagamento);

             return new PagamentoResponseDTO(pagamento);

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
    public PagamentoResponseDTO confirmarPagamentoDoPedido(Long id){
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
        return new PagamentoResponseDTO(pagamento);
    }

    ///////////////////////////////////
    @Transactional
    public PagamentoResponseDTO alterarStatusDoPagamento(Long id) {

        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pagamento não encontrado. ID:" + id)
        );

        pagamento.setStatus(Status.CONFIRMACAO_PENDENTE);
        pagamento = pagamentoRepository.save(pagamento);

        return new PagamentoResponseDTO(pagamento);
    }
}
