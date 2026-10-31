package com.mechita.mechita_web.repository;

import com.mechita.mechita_web.model.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Long> {
    Optional<EstadoPedido> findByNombre(String nombre);

    List<EstadoPedido> findAllByOrderByIdEstadoAsc();
}
