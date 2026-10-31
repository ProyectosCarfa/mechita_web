package com.mechita.mechita_web.repository;

import com.mechita.mechita_web.model.Cliente;
import com.mechita.mechita_web.model.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByCliente(Cliente cliente);
}