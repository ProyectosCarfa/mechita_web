package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.exception.RecursoNoEncontradoException;
import com.mechita.mechita_web.exception.ReglaNegocioException;
import com.mechita.mechita_web.model.Categoria;
import com.mechita.mechita_web.service.CategoriaService;
import com.mechita.mechita_web.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaRestController {

    private final CategoriaService service;
    private final ProductoService productoService;

    @GetMapping
    public List<Map<String, Object>> listar(@RequestParam(required = false) Boolean activo) {
        List<Categoria> categorias;

        if (activo == null) {
            categorias = service.listar();
        } else if (activo) {
            categorias = service.listarActivas();
        } else {
            categorias = service.listarInactivas();
        }

        return categorias.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idCategoria", c.getIdCategoria());
            map.put("nombre", c.getNombre());
            map.put("descripcion", c.getDescripcion());
            map.put("imagenUrl", c.getImagenUrl());
            map.put("activo", c.getActivo());

            // ⭐ CONTEO DIRECTO Y SEGURO ⭐
            map.put("cantidadProductos", productoService.contarPorCategoria(c.getIdCategoria()));

            return map;
        }).collect(Collectors.toList());
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