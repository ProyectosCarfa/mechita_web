package com.mechita.mechita_web.repository;

import com.mechita.mechita_web.model.Carrito;
import com.mechita.mechita_web.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByCliente(Cliente cliente);
}
