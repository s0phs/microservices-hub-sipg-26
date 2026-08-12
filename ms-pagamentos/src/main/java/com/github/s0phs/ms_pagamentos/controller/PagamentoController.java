package com.github.s0phs.ms_pagamentos.controller;

import com.github.s0phs.ms_pagamentos.dto.PagamentoDTO;
import com.github.s0phs.ms_pagamentos.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j //do lombok, para logs
@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    public ResponseEntity<List<PagamentoDTO>> getAllPagamentos(){

        List<PagamentoDTO> pagamentoDTOS = pagamentoService.findAllPagamentos();

        return ResponseEntity.ok(pagamentoDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDTO> getPagamentoById(@PathVariable Long id){

        PagamentoDTO pagamentoDTO = pagamentoService.findPagamentoById(id);

        return ResponseEntity.ok(pagamentoDTO);
    }

    @PostMapping
    private ResponseEntity<PagamentoDTO> createPagamento(@RequestBody @Valid PagamentoDTO pagamentoDTO){

        pagamentoDTO = pagamentoService.savePagamento(pagamentoDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(pagamentoDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(pagamentoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDTO> updateProduto(@PathVariable Long id, @Valid @RequestBody PagamentoDTO pagamentoDTO){

        pagamentoDTO = pagamentoService.updatePagamento(id, pagamentoDTO);

        return ResponseEntity.ok(pagamentoDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePagamento(@PathVariable Long id){

        pagamentoService.deletePagamentoById(id);

        return ResponseEntity.noContent().build();
    }

    //Patch faz uma modificação parcial, diferente do Put que faz uma substituição total
    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizarPedido", fallbackMethod = "fallbackConfirmarPagamentoPendente")
    public ResponseEntity<PagamentoDTO> confirmarPagamentoDoPedido(@PathVariable @NotNull Long id) {

        PagamentoDTO dto = pagamentoService.confirmarPagamentoDoPedido(id);

        return ResponseEntity.ok(dto);
    }

    //metodo com a mesam assinatura e tipo de retorno de confirmarPagamentoDoPedido
    public ResponseEntity<PagamentoDTO> fallbackConfirmarPagamentoPendente(Long id, Throwable e){

        //registra o erro para fins de log/observalidade
        log.error("Falha ao confirmar pedido {}. Ativando fallback. Erro: {}", id, e.getMessage());
        PagamentoDTO dto = pagamentoService.alterarStatusDoPagamento(id);

        //503 - explicitar que o serviço destino falhou, mas ainda assim enviando o corpo.
        return ResponseEntity.status(503).body(dto);
    }

}
