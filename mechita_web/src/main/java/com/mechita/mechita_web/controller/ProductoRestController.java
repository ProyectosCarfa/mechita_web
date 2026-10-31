package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.exception.RecursoNoEncontradoException;
import com.mechita.mechita_web.exception.ReglaNegocioException;
import com.mechita.mechita_web.model.Categoria;
import com.mechita.mechita_web.model.Producto;
import com.mechita.mechita_web.service.CategoriaService;
import com.mechita.mechita_web.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoRestController {

    private final ProductoService service;
    private final CategoriaService categoriaService;

    @GetMapping
    public List<Producto> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoria) {
        if (q != null && !q.isBlank()) {
            return service.buscarPorNombre(q);
        }
        if (categoria != null) {
            return service.listarPorCategoria(categoria);
        }
        return service.listar();
    }

    @GetMapping("/stock-bajo")
    public List<Producto> stockBajo() {
        return service.listarStockBajo();
    }

    @GetMapping("/{id}")
    public Producto obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        producto.setIdProducto(null);
        producto.setCategoria(resolverCategoria(producto));
        Producto guardado = service.guardar(producto);
        return ResponseEntity.created(URI.create("/api/productos/" + guardado.getIdProducto())).body(guardado);
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @Valid @RequestBody Producto datos) {
        Producto actual = obtener(id);
        actual.setCategoria(resolverCategoria(datos));
        actual.setNombre(datos.getNombre());
        actual.setDescripcion(datos.getDescripcion());
        actual.setPrecio(datos.getPrecio());
        actual.setStock(datos.getStock());
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

    private Categoria resolverCategoria(Producto producto) {
        Long idCategoria = producto.getCategoria().getIdCategoria();
        if (idCategoria == null) {
            throw new ReglaNegocioException("Debe indicar categoria.idCategoria");
        }
        return categoriaService.buscarPorId(idCategoria)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada: " + idCategoria));
    }
}
