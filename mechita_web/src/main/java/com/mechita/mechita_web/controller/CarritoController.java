package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.exception.RecursoNoEncontradoException;
import com.mechita.mechita_web.exception.ReglaNegocioException;
import com.mechita.mechita_web.model.Cliente;
import com.mechita.mechita_web.model.Pedido;
import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.repository.ClienteRepository;
import com.mechita.mechita_web.repository.DireccionRepository;
import com.mechita.mechita_web.repository.MetodoPagoRepository;
import com.mechita.mechita_web.service.CarritoService;
import com.mechita.mechita_web.service.PagoService;
import com.mechita.mechita_web.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final PagoService pagoService;
    private final ClienteRepository clienteRepository;
    private final DireccionRepository direccionRepository;
    private final MetodoPagoRepository metodoPagoRepository;

    private Cliente clienteActual(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        return usuario == null ? null : clienteRepository.findByUsuario(usuario).orElse(null);
    }

    private void actualizarContador(HttpSession session, Cliente cliente) {
        session.setAttribute("carritoCantidad", carritoService.contarUnidades(cliente));
    }

    @GetMapping("/carrito")
    public String ver(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        Cliente cliente = clienteActual(session);
        model.addAttribute("cliente", cliente);
        if (cliente != null) {
            model.addAttribute("carrito", carritoService.obtenerOCrear(cliente));
            model.addAttribute("direcciones", direccionRepository.findByCliente(cliente));
            model.addAttribute("metodos", metodoPagoRepository.findAll());
            actualizarContador(session, cliente);
        }
        return "carrito";
    }

    @PostMapping("/carrito/agregar")
    public String agregar(
            @RequestParam Long idProducto,
            @RequestParam(defaultValue = "1") int cantidad,
            HttpSession session,
            RedirectAttributes ra) {

        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        Cliente cliente = clienteActual(session);
        if (cliente == null) {
            ra.addFlashAttribute("error", "Solo las cuentas de cliente pueden comprar.");
            return "redirect:/productos";
        }
        try {
            carritoService.agregar(cliente, idProducto, cantidad);
            actualizarContador(session, cliente);
            ra.addFlashAttribute("success", "Producto agregado al carrito");
        } catch (RecursoNoEncontradoException | ReglaNegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/quitar")
    public String quitar(@RequestParam Long idProducto, HttpSession session, RedirectAttributes ra) {
        Cliente cliente = clienteActual(session);
        if (cliente == null) {
            return "redirect:/login";
        }
        carritoService.quitar(cliente, idProducto);
        actualizarContador(session, cliente);
        ra.addFlashAttribute("success", "Producto quitado del carrito");
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/confirmar")
    public String confirmar(
            @RequestParam Long idDireccion,
            @RequestParam Long idMetodoPago,
            HttpSession session,
            RedirectAttributes ra) {

        Cliente cliente = clienteActual(session);
        if (cliente == null) {
            return "redirect:/login";
        }
        try {
            Pedido pedido = pedidoService.crearDesdeCarrito(cliente, idDireccion);
            pagoService.registrar(pedido.getIdPedido(), idMetodoPago, null);
            actualizarContador(session, cliente);
            ra.addFlashAttribute("success", "Pedido #" + pedido.getIdPedido() + " registrado correctamente");
            return "redirect:/perfil";
        } catch (RecursoNoEncontradoException | ReglaNegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito";
        }
    }

    @GetMapping("/mis-compras")
    public String misCompras() {
        return "redirect:/perfil";
    }
}
