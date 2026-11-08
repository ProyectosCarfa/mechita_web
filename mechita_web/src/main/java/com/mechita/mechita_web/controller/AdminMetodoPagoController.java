package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.model.MetodoPago;
import com.mechita.mechita_web.repository.MetodoPagoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/pagos")
@RequiredArgsConstructor
public class AdminMetodoPagoController {

    private final MetodoPagoRepository metodoPagoRepository;

    @PostMapping("/metodo/guardar")
    public String guardarMetodo(@Valid MetodoPago metodoPago, BindingResult result, RedirectAttributes ra) {

        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Revisa los datos del formulario. El nombre es obligatorio.");
            return "redirect:/admin/pagos";
        }

        try {
            metodoPagoRepository.save(metodoPago);
            ra.addFlashAttribute("success", "Método de pago agregado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ya existe un método de pago con ese nombre o hubo un error en la base de datos.");
        }

        return "redirect:/admin/pagos";
    }
}