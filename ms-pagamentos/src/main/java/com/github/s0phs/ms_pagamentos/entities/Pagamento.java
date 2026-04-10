package com.github.s0phs.ms_pagamentos.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "tb_pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private String nome; //nome no cartão

    @Column(nullable = false, length = 16)
    private String numeroCartao; //XXX.XXX.XXX.X

    @Column(nullable = false, length = 5)
    private String validade; // MM/AA

    @Column(nullable = false, length = 3)
    private String codigoSeguranca; // xxx

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private Long pedidoId;
}
