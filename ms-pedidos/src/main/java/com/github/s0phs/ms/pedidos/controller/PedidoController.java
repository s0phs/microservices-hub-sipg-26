package com.github.s0phs.ms.pedidos.controller;

import com.github.s0phs.ms.pedidos.dto.PedidoRequestDTO;
import com.github.s0phs.ms.pedidos.dto.PedidoResponseDTO;
import com.github.s0phs.ms.pedidos.services.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // Testando load balancing: devolve a porta da instância
    @GetMapping("/port")
    public String port(@Value("${local.server.port}")String porta){
        return "Instância respondeu na porta " + porta;
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> getAllPedidos(){

        List<PedidoResponseDTO> pedidos = pedidoService.findAllPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> getPedido(@PathVariable Long id){

        PedidoResponseDTO pedidoDTO = pedidoService.findPedidoById(id);
        return ResponseEntity.ok(pedidoDTO);
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> createPedido(@RequestBody @Valid PedidoRequestDTO inputDTO){

        PedidoResponseDTO pedidoDTO = pedidoService.savePedido(inputDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(pedidoDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(pedidoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> updatePedido(@PathVariable Long id,
                                                          @RequestBody @Valid PedidoRequestDTO inputDTO){

        PedidoResponseDTO pedidoDTO = pedidoService.updatePedido(id, inputDTO);

        return ResponseEntity.ok(pedidoDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id){

        pedidoService.deletePedidoById(id);

        return ResponseEntity.noContent().build();
    }

    ///////////////////////
    @PutMapping("/{pedidoId}/pagamento/confirmado")
    public void confirmarPagamento(@PathVariable Long pedidoId){

        pedidoService.confirmarPagamento(pedidoId);
    }
}
