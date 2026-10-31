package com.mechita.mechita_web.repository;

import com.mechita.mechita_web.model.Cliente;
import com.mechita.mechita_web.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsuario(Usuario usuario);
}