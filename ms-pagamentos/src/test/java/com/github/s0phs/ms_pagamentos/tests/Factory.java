package com.github.s0phs.ms_pagamentos.tests;

import com.github.s0phs.ms_pagamentos.entities.Pagamento;
import com.github.s0phs.ms_pagamentos.entities.Status;

import java.math.BigDecimal;

public class Factory {

    public static Pagamento createPagamento(){
        Pagamento pagamento = new Pagamento(1L, BigDecimal.valueOf(32.25),
                "Brienne de Tarth", "3656541596582315", "07/15",
                "354", Status.CRIADO, 1L);
        return pagamento;
    }

    public static Pagamento createPagamentoSemId(){
        Pagamento pagamento = createPagamento();
        pagamento.setId(null);
        return pagamento;
    }

}
