package com.mechita.mechita_web;

import com.mechita.mechita_web.model.Categoria;
import com.mechita.mechita_web.model.Cliente;
import com.mechita.mechita_web.model.Producto;
import com.mechita.mechita_web.model.Usuario;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidacionEntidadesTest {

    private static Validator validator;

    @BeforeAll
    static void iniciar() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Producto productoValido() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Dulces");
        Producto p = new Producto();
        p.setCategoria(categoria);
        p.setNombre("Algarrobina");
        p.setPrecio(new BigDecimal("12.50"));
        p.setStock(5);
        return p;
    }

    @Test
    void productoValidoNoTieneErrores() {
        assertTrue(validator.validate(productoValido()).isEmpty());
    }

    @Test
    void productoConPrecioNegativoEsInvalido() {
        Producto p = productoValido();
        p.setPrecio(new BigDecimal("-1"));
        assertFalse(validator.validate(p).isEmpty());
    }

    @Test
    void productoConStockNegativoEsInvalido() {
        Producto p = productoValido();
        p.setStock(-3);
        assertEquals(1, validator.validate(p).size());
    }

    @Test
    void usuarioConCorreoInvalidoEsInvalido() {
        Usuario u = new Usuario();
        u.setCorreo("no-es-correo");
        u.setContrasena("123456");
        assertFalse(validator.validate(u).isEmpty());
    }

    @Test
    void clienteConDniDeSieteDigitosEsInvalido() {
        Cliente c = new Cliente();
        c.setNombres("Ana");
        c.setApellidos("Pérez");
        c.setDni("1234567");
        assertEquals(1, validator.validate(c).size());
    }
}
