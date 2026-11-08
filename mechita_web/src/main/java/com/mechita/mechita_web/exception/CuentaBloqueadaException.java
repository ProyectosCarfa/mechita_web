package com.mechita.mechita_web.exception;

import org.springframework.security.core.AuthenticationException;

public class CuentaBloqueadaException extends AuthenticationException {
    public CuentaBloqueadaException(String mensaje) {
        super(mensaje);
    }
}