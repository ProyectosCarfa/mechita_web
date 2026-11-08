package com.mechita.mechita_web.service;

import com.mechita.mechita_web.model.Producto;
import com.mechita.mechita_web.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ProductoService {
    @Autowired
    private ProductoRepository repo;
    public List<Producto> listar() {
        return repo.findAll();
    }
    public Optional<Producto> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Producto guardar(Producto p) {
        return repo.save(p);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    public List<Producto> listarActivos() {
        return repo.findByActivo(true);
    }

    public List<Producto> listarInactivos() {
        return repo.findByActivo(false);
    }

    public List<Producto> buscarPorNombre(String texto) {
        return repo.findByNombreContainingIgnoreCase(texto);
    }

    public List<Producto> listarPorCategoria(Long idCategoria) {
        return repo.findByCategoria_IdCategoria(idCategoria);
    }

    public List<Producto> listarStockBajo() {
        return repo.findByStockLessThan(10);
    }

    public void cambiarEstado(Long id) {
        Producto p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        p.setActivo(!p.getActivo());
        repo.save(p);
    }

    public long contarTotal() {
        return repo.count();
    }

    public long contarActivos() {
        return repo.countByActivo(true);
    }

    public long contarPorCategoria(Long idCategoria) {
        return repo.countByCategoria_IdCategoriaAndActivo(idCategoria, true);
    }
    public Page<Producto> listarActivosPaginado(Pageable pageable) {
        return repo.findByActivo(true, pageable);
    }

    public Page<Producto> listarPorCategoriaPaginado(Long idCategoria, Pageable pageable) {
        return repo.findByCategoria_IdCategoriaAndActivo(idCategoria, true, pageable);
    }

    public Page<Producto> buscarPorNombrePaginado(String nombre, Pageable pageable) {
        return repo.findByNombreContainingIgnoreCaseAndActivo(nombre, true, pageable);
    }
}