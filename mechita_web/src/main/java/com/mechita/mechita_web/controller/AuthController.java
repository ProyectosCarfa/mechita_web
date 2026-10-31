package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.model.Cliente;
import com.mechita.mechita_web.model.Direccion;
import com.mechita.mechita_web.model.Distrito;
import com.mechita.mechita_web.model.Rol;
import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.repository.ClienteRepository;
import com.mechita.mechita_web.repository.DistritoRepository;
import com.mechita.mechita_web.service.CarritoService;
import com.mechita.mechita_web.service.RegistroService;
import com.mechita.mechita_web.service.RolService;
import com.mechita.mechita_web.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.Set;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private RolService rolService;

    @Autowired
    private Validator validator;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DistritoRepository distritoRepository;

    @Autowired
    private RegistroService registroService;

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        Optional<Usuario> usuarioOpt = service.buscarPorCorreo(email);

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }

        Usuario u = usuarioOpt.get();

        if (!u.getContrasena().equals(password)) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }

        if (!u.getActivo()) {
            model.addAttribute("error", "Tu cuenta está desactivada");
            return "login";
        }

        session.setAttribute("usuarioLogueado", u);
        clienteRepository.findByUsuario(u).ifPresent(c ->
                session.setAttribute("carritoCantidad", carritoService.contarUnidades(c)));

        if (u.esAdmin()) {
            return "redirect:/admin";
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("distritos", distritoRepository.findAllByOrderByNombreAsc());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam Long idDistrito,
            @RequestParam String password,
            @RequestParam String passwordConfirm,
            Model model) {

        model.addAttribute("nombre", nombre);
        model.addAttribute("apellido", apellido);
        model.addAttribute("email", email);
        model.addAttribute("telefono", telefono);
        model.addAttribute("direccion", direccion);
        model.addAttribute("idDistrito", idDistrito);

        if (!password.equals(passwordConfirm)) {
            return errorRegistro(model, "Las contraseñas no coinciden");
        }

        if (password.length() < 6) {
            return errorRegistro(model, "La contraseña debe tener al menos 6 caracteres");
        }

        if (service.existeCorreo(email)) {
            return errorRegistro(model, "Ese correo ya está registrado");
        }

        Optional<Rol> rolCliente = rolService.buscarPorNombre("CLIENTE");
        if (rolCliente.isEmpty()) {
            return errorRegistro(model, "Error interno: rol CLIENTE no existe");
        }

        Optional<Distrito> distrito = distritoRepository.findById(idDistrito);
        if (distrito.isEmpty()) {
            return errorRegistro(model, "Selecciona un distrito válido");
        }

        Usuario nuevo = new Usuario();
        nuevo.setRol(rolCliente.get());
        nuevo.setCorreo(email);
        nuevo.setContrasena(password);
        nuevo.setActivo(true);

        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombres(nombre);
        nuevoCliente.setApellidos(apellido);
        nuevoCliente.setTelefono(telefono);

        Direccion nuevaDireccion = new Direccion();
        nuevaDireccion.setDireccion(direccion);
        nuevaDireccion.setDistrito(distrito.get());

        String problema = primerError(nuevo, nuevoCliente);
        if (problema == null && direccion.isBlank()) {
            problema = "La dirección no puede estar vacía";
        }
        if (problema == null && direccion.length() > 200) {
            problema = "La dirección no debe superar los 200 caracteres";
        }
        if (problema != null) {
            return errorRegistro(model, problema);
        }

        registroService.registrarCliente(nuevo, nuevoCliente, nuevaDireccion);

        model.addAttribute("success", "¡Cuenta creada! Ya puedes iniciar sesión");
        return "login";
    }

    private String errorRegistro(Model model, String mensaje) {
        model.addAttribute("error", mensaje);
        model.addAttribute("distritos", distritoRepository.findAllByOrderByNombreAsc());
        return "registro";
    }

    private String primerError(Object... objetos) {
        for (Object objeto : objetos) {
            Set<ConstraintViolation<Object>> errores = validator.validate(objeto);
            if (!errores.isEmpty()) {
                return errores.iterator().next().getMessage();
            }
        }
        return null;
    }
}
