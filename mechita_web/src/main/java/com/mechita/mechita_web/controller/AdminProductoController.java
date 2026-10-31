package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.model.Categoria;
import com.mechita.mechita_web.model.Producto;
import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.service.CategoriaService;
import com.mechita.mechita_web.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private Validator validator;

    private boolean esAdmin(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        return u != null && u.esAdmin();
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long cat,
            HttpSession session,
            Model model) {

        if (!esAdmin(session)) return "redirect:/login";

        List<Producto> productos;

        if (q != null && !q.isBlank()) {
            productos = productoService.buscarPorNombre(q);
        } else if (cat != null) {
            productos = productoService.listarPorCategoria(cat);
        } else if ("activos".equals(filtro)) {
            productos = productoService.listarActivos();
        } else if ("inactivos".equals(filtro)) {
            productos = productoService.listarInactivos();
        } else if ("stock-bajo".equals(filtro)) {
            productos = productoService.listarStockBajo();
        } else {
            productos = productoService.listar();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("total", productoService.contarTotal());
        model.addAttribute("activos", productoService.contarActivos());
        model.addAttribute("filtroActual", filtro);
        model.addAttribute("busqueda", q);
        model.addAttribute("categoriaSeleccionada", cat);

        return "roles/productos";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";

        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("modo", "crear");

        return "roles/productos-form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";

        Optional<Producto> p = productoService.buscarPorId(id);
        if (p.isEmpty()) return "redirect:/admin/productos";

        model.addAttribute("producto", p.get());
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("modo", "editar");

        return "roles/productos-form";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) Long idProducto,
            @RequestParam Long idCategoria,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam java.math.BigDecimal precio,
            @RequestParam Integer stock,
            @RequestParam(required = false) String imagenUrl,
            HttpSession session,
            Model model) {

        if (!esAdmin(session)) return "redirect:/login";

        Optional<Categoria> cat = categoriaService.buscarPorId(idCategoria);
        if (cat.isEmpty()) {
            model.addAttribute("error", "La categoría seleccionada no existe.");
            model.addAttribute("categorias", categoriaService.listarActivas());
            model.addAttribute("modo", idProducto != null ? "editar" : "crear");
            return "roles/productos-form";
        }

        Producto p;
        if (idProducto != null) {
            p = productoService.buscarPorId(idProducto).orElse(new Producto());
        } else {
            p = new Producto();
        }

        p.setCategoria(cat.get());
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio);
        p.setStock(stock);
        p.setImagenUrl(imagenUrl);

        Set<ConstraintViolation<Producto>> errores = validator.validate(p);
        if (!errores.isEmpty()) {
            model.addAttribute("error", errores.iterator().next().getMessage());
            model.addAttribute("producto", p);
            model.addAttribute("categorias", categoriaService.listarActivas());
            model.addAttribute("modo", idProducto != null ? "editar" : "crear");
            return "roles/productos-form";
        }

        productoService.guardar(p);

        return "redirect:/admin/productos";
    }

    @GetMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        productoService.cambiarEstado(id);
        return "redirect:/admin/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) return "redirect:/login";

        try {
            productoService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar el producto porque tiene registros asociados (ej. pedidos o carritos).");
        }

        return "redirect:/admin/productos";
    }
}