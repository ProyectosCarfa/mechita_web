package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

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

        List<Usuario> usuarios;

        if (q != null && !q.isBlank()) {
            usuarios = usuarioService.buscarPorCorreoParcial(q);
        } else if ("activos".equals(filtro)) {
            usuarios = usuarioService.listarActivos();
        } else if ("inactivos".equals(filtro)) {
            usuarios = usuarioService.listarInactivos();
        } else if ("admins".equals(filtro)) {
            usuarios = usuarioService.listarPorRol("ADMIN");
        } else if ("clientes".equals(filtro)) {
            usuarios = usuarioService.listarPorRol("CLIENTE");
        } else {
            usuarios = usuarioService.listar();
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("total", usuarioService.contarTotal());
        model.addAttribute("activos", usuarioService.contarActivos());
        model.addAttribute("filtroActual", filtro);
        model.addAttribute("busqueda", q);

        return "roles/usuarios";
    }

    @GetMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) return "redirect:/login";

        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u.getIdUsuario().equals(id)) {
            redirectAttributes.addFlashAttribute("error", "No puedes desactivar tu propia cuenta.");
            return "redirect:/admin/usuarios";
        }

        usuarioService.cambiarEstado(id);
        redirectAttributes.addFlashAttribute("success", "Estado del usuario actualizado.");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) return "redirect:/login";

        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        if (u.getIdUsuario().equals(id)) {
            redirectAttributes.addFlashAttribute("error", "No puedes eliminar tu propia cuenta de administrador.");
            return "redirect:/admin/usuarios";
        }

        try {
            usuarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el usuario. Es posible que tenga pedidos asociados.");
        }

        return "redirect:/admin/usuarios";
    }
}