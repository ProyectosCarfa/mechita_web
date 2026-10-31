package com.mechita.mechita_web.repository;

import com.mechita.mechita_web.model.Cliente;
import com.mechita.mechita_web.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteOrderByFechaPedidoDesc(Cliente cliente);

    List<Pedido> findAllByOrderByFechaPedidoDesc();

    List<Pedido> findByEstado_NombreOrderByFechaPedidoDesc(String nombre);
}
