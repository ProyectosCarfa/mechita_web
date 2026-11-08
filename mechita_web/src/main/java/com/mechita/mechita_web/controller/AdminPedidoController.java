package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.exception.RecursoNoEncontradoException;
import com.mechita.mechita_web.exception.ReglaNegocioException;
import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.repository.EstadoPedidoRepository;
import com.mechita.mechita_web.repository.MetodoPagoRepository;
import com.mechita.mechita_web.service.PagoService;
import com.mechita.mechita_web.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPedidoController {

    private final PedidoService pedidoService;
    private final PagoService pagoService;
    private final EstadoPedidoRepository estadoRepository;
    private final MetodoPagoRepository metodoPagoRepository; // <-- Agregado

    private boolean esAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        return usuario != null && usuario.esAdmin();
    }

    @GetMapping("/pedidos")
    public String pedidos(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        model.addAttribute("pedidos", pedidoService.listar());
        model.addAttribute("estados", estadoRepository.findAllByOrderByIdEstadoAsc());
        return "roles/pedidos";
    }

    @PostMapping("/pedidos/estado")
    public String cambiarEstado(
            @RequestParam Long idPedido,
            @RequestParam String estado,
            HttpSession session,
            RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        try {
            pedidoService.cambiarEstado(idPedido, estado);
            ra.addFlashAttribute("success", "Estado del pedido #" + idPedido + " actualizado");
        } catch (RecursoNoEncontradoException | ReglaNegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/pedidos";
    }

    @GetMapping("/pagos")
    public String pagos(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        model.addAttribute("pagos", pagoService.listar());
        model.addAttribute("totalRecaudado", pagoService.totalRecaudado());
        model.addAttribute("metodosPago", metodoPagoRepository.findAll()); // <-- Agregado
        return "roles/pagos";
    }
}