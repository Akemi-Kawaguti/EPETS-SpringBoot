package com.site.epets.repository;

import com.site.epets.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, String> {

    List<Pedido> findAllByOrderByCriadoEmDesc();
}
