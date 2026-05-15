package com.github.s0phs.ms_pagamentos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//nome do microsserviço
@FeignClient("ms-pedidos")
public interface PedidoClient {

    @RequestMapping(method = RequestMethod.PUT,
            value = ("/pedidos/{pedidoId}/pagamento/confirmado")) //endpoint da requisição
    void confirmarPagamento(@PathVariable Long pedidoId);

}
