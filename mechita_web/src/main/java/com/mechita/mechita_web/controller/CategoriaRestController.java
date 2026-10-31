package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.exception.RecursoNoEncontradoException;
import com.mechita.mechita_web.exception.ReglaNegocioException;
import com.mechita.mechita_web.model.Categoria;
import com.mechita.mechita_web.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaRestController {

    private final CategoriaService service;

    @GetMapping
    public List<Categoria> listar(@RequestParam(required = false) Boolean activo) {
        if (activo == null) {
            return service.listar();
        }
        return activo ? service.listarActivas() : service.listarInactivas();
    }

    @GetMapping("/{id}")
    public Categoria obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada: " + id));
    }

    @PostMapping
    public ResponseEntity<Categoria> crear(@Valid @RequestBody Categoria categoria) {
        if (service.existeNombre(categoria.getNombre())) {
            throw new ReglaNegocioException("Ya existe una categoría con ese nombre");
        }
        categoria.setIdCategoria(null);
        Categoria guardada = service.guardar(categoria);
        return ResponseEntity.created(URI.create("/api/categorias/" + guardada.getIdCategoria())).body(guardada);
    }

    @PutMapping("/{id}")
    public Categoria actualizar(@PathVariable Long id, @Valid @RequestBody Categoria datos) {
        Categoria actual = obtener(id);
        actual.setNombre(datos.getNombre());
        actual.setDescripcion(datos.getDescripcion());
        actual.setImagenUrl(datos.getImagenUrl());
        actual.setActivo(datos.getActivo());
        return service.guardar(actual);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        obtener(id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
