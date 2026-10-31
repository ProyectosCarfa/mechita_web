package com.mechita.mechita_web.service;

import com.mechita.mechita_web.model.Rol;
import com.mechita.mechita_web.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository repo;

    public Optional<Rol> buscarPorNombre(String nombre) {
        return repo.findByNombre(nombre);
    }
}