package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.model.Cliente;
import com.mechita.mechita_web.model.Direccion;
import com.mechita.mechita_web.model.Distrito;
import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.repository.ClienteRepository;
import com.mechita.mechita_web.repository.DireccionRepository;
import com.mechita.mechita_web.repository.DistritoRepository;
import com.mechita.mechita_web.service.PerfilService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private DistritoRepository distritoRepository;

    @GetMapping
    public String verPerfil(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        model.addAttribute("usuario", usuarioLogueado);
        perfilService.cargarDatosEnModelo(usuarioLogueado, model);

        return "perfil";
    }

    @PostMapping("/actualizar")
    public String actualizarDatos(
            @RequestParam String direccion,
            @RequestParam String telefono,
            @RequestParam Long idDistrito,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        Optional<Cliente> clienteOpt = clienteRepository.findByUsuario(usuarioLogueado);
        if (clienteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se encontró el perfil del cliente.");
            return "redirect:/perfil";
        }

        Optional<Distrito> distrito = distritoRepository.findById(idDistrito);
        if (distrito.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El distrito seleccionado no existe.");
            return "redirect:/perfil";
        }
        if (telefono.length() > 15 || direccion.isBlank() || direccion.length() > 200) {
            redirectAttributes.addFlashAttribute("error", "Revisa el teléfono (máx. 15) y la dirección (máx. 200).");
            return "redirect:/perfil";
        }

        Cliente cliente = clienteOpt.get();
        cliente.setTelefono(telefono);
        clienteRepository.save(cliente);

        Direccion principal = perfilService.direccionPrincipal(cliente);
        if (principal == null) {
            principal = new Direccion();
            principal.setCliente(cliente);
        }
        principal.setDireccion(direccion);
        principal.setDistrito(distrito.get());
        direccionRepository.save(principal);

        redirectAttributes.addFlashAttribute("success", "Datos actualizados correctamente.");
        return "redirect:/perfil";
    }
}
