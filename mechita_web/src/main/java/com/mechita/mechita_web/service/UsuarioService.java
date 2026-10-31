package com.mechita.mechita_web.service;

import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return repo.findByCorreo(correo);
    }

    public Usuario guardar(Usuario u) {
        return repo.save(u);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    public boolean existeCorreo(String correo) {
        return repo.existsByCorreo(correo);
    }

    public List<Usuario> listarActivos() {
        return repo.findByActivo(true);
    }

    public List<Usuario> listarInactivos() {
        return repo.findByActivo(false);
    }

    public List<Usuario> listarPorRol(String nombreRol) {
        return repo.findByRol_Nombre(nombreRol);
    }

    public List<Usuario> buscarPorCorreoParcial(String texto) {
        return repo.findByCorreoContainingIgnoreCase(texto);
    }

    public void cambiarEstado(Long id) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setActivo(!u.getActivo());
        repo.save(u);
    }

    public long contarTotal() {
        return repo.count();
    }

    public long contarActivos() {
        return repo.findByActivo(true).size();
    }
}