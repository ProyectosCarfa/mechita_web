package com.mechita.mechita_web.service;

import com.mechita.mechita_web.model.Categoria;
import com.mechita.mechita_web.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repo;

    public List<Categoria> listar() {
        return repo.findAll();
    }

    public List<Categoria> listarActivas() {
        return repo.findByActivo(true);
    }

    public List<Categoria> listarInactivas() {
        return repo.findByActivo(false);
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Optional<Categoria> buscarPorNombre(String nombre) {
        return repo.findByNombre(nombre);
    }

    public Categoria guardar(Categoria c) {
        return repo.save(c);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    public List<Categoria> buscarPorNombreParcial(String texto) {
        return repo.findByNombreContainingIgnoreCase(texto);
    }

    public boolean existeNombre(String nombre) {
        return repo.existsByNombre(nombre);
    }

    public void cambiarEstado(Long id) {
        Categoria c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        c.setActivo(!c.getActivo());
        repo.save(c);
    }

    public long contarTotal() {
        return repo.count();
    }

    public long contarActivas() {
        return repo.countByActivo(true);
    }
}