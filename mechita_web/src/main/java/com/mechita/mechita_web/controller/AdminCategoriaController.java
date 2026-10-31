package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.model.Categoria;
import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.service.CategoriaService;
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
@RequestMapping("/admin/categorias")
public class AdminCategoriaController {

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
            HttpSession session,
            Model model) {

        if (!esAdmin(session)) return "redirect:/login";

        List<Categoria> categorias;

        if (q != null && !q.isBlank()) {
            categorias = categoriaService.buscarPorNombreParcial(q);
        } else if ("activas".equals(filtro)) {
            categorias = categoriaService.listarActivas();
        } else if ("inactivas".equals(filtro)) {
            categorias = categoriaService.listarInactivas();
        } else {
            categorias = categoriaService.listar();
        }

        model.addAttribute("categorias", categorias);
        model.addAttribute("total", categoriaService.contarTotal());
        model.addAttribute("activas", categoriaService.contarActivas());
        model.addAttribute("filtroActual", filtro);
        model.addAttribute("busqueda", q);

        return "roles/categorias";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";

        model.addAttribute("categoria", new Categoria());
        model.addAttribute("modo", "crear");

        return "roles/categorias-form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";

        Optional<Categoria> c = categoriaService.buscarPorId(id);
        if (c.isEmpty()) return "redirect:/admin/categorias";

        model.addAttribute("categoria", c.get());
        model.addAttribute("modo", "editar");

        return "roles/categorias-form";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) Long idCategoria,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String imagenUrl,
            HttpSession session,
            Model model) {

        if (!esAdmin(session)) return "redirect:/login";

        Categoria c;
        if (idCategoria != null) {
            c = categoriaService.buscarPorId(idCategoria).orElse(new Categoria());
        } else {
            c = new Categoria();
        }

        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setImagenUrl(imagenUrl);

        Set<ConstraintViolation<Categoria>> errores = validator.validate(c);
        if (!errores.isEmpty()) {
            model.addAttribute("categoria", c);
            model.addAttribute("modo", idCategoria != null ? "editar" : "crear");
            model.addAttribute("error", errores.iterator().next().getMessage());
            return "roles/categorias-form";
        }

        Optional<Categoria> existente = categoriaService.buscarPorNombre(nombre);
        if (existente.isPresent() && (idCategoria == null || !existente.get().getIdCategoria().equals(idCategoria))) {
            model.addAttribute("categoria", c);
            model.addAttribute("modo", idCategoria != null ? "editar" : "crear");
            model.addAttribute("error", "Ya existe una categoría con ese nombre");
            return "roles/categorias-form";
        }

        categoriaService.guardar(c);

        return "redirect:/admin/categorias";
    }

    @GetMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";
        categoriaService.cambiarEstado(id);
        return "redirect:/admin/categorias";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) return "redirect:/login";

        try {
            categoriaService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Categoría eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar la categoría porque tiene productos asociados.");
        }

        return "redirect:/admin/categorias";
    }
}