package com.github.s0phs.ms.pedidos.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column - define características da coluna no DB
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    //@Column(unique = true, nullable = false, lenght = 11)
    @Column(nullable = false, length = 11)
    private String cpf;

    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private Status status;

    //valor calculado
    private BigDecimal valorTotal;
}
