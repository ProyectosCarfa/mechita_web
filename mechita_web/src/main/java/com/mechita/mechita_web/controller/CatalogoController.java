package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.model.Categoria;
import com.mechita.mechita_web.model.Producto;
import com.mechita.mechita_web.service.CategoriaService;
import com.mechita.mechita_web.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

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
        List<Producto> destacados = productosActivos.stream()
                .limit(4)
                .toList();

        model.addAttribute("productosDestacados", destacados);
        model.addAttribute("totalProductos", productosActivos.size());

        return "index";
    }

    @GetMapping("/productos")
    public String catalogo(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long cat,
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max,
            Model model) {

        List<Producto> productos;

        if (q != null && !q.isBlank()) {
            productos = productoService.buscarPorNombre(q);
        } else if (cat != null) {
            productos = productoService.listarPorCategoria(cat);
        } else {
            productos = productoService.listarActivos();
        }

        if (min != null || max != null) {
            productos = productos.stream()
                    .filter(p -> {
                        BigDecimal precio = p.getPrecio();
                        if (min != null && precio.compareTo(min) < 0) return false;
                        if (max != null && precio.compareTo(max) > 0) return false;
                        return true;
                    })
                    .toList();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("busqueda", q);
        model.addAttribute("categoriaSeleccionada", cat);
        model.addAttribute("precioMin", min);
        model.addAttribute("precioMax", max);

        return "productos";
    }
}