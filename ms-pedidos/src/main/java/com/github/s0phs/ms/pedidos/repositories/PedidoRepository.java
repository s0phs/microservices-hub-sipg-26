package com.github.s0phs.ms.pedidos.repositories;

import com.github.s0phs.ms.pedidos.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
