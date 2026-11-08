package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.model.Producto;
import com.mechita.mechita_web.service.CategoriaService;
import com.mechita.mechita_web.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CatalogoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("nombreEmpresa", "Productos Piuranos Mechita E.I.R.L");
        List<Producto> productosActivos = productoService.listarActivos();
        List<Producto> destacados = productosActivos.stream().limit(4).collect(Collectors.toList());
        model.addAttribute("productosDestacados", destacados);
        model.addAttribute("totalProductos", productosActivos.size());
        return "index";
    }

    @GetMapping("/productos")
    public String catalogo(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long cat,
            Model model) {

        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("busqueda", q);
        model.addAttribute("categoriaSeleccionada", cat);

        return "productos";
    }

    @GetMapping("/api/productos")
    @ResponseBody
    public ResponseEntity<List<Producto>> apiProductos(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long cat) {

        List<Producto> productos;

        if (q != null && !q.isBlank()) {
            productos = productoService.buscarPorNombre(q);
        } else if (cat != null) {
            productos = productoService.listarPorCategoria(cat);
        } else {
            productos = productoService.listarActivos();
        }

        return ResponseEntity.ok(productos);
    }
}