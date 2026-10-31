package com.mechita.mechita_web.dto;

import com.mechita.mechita_web.model.Pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoDto(Long idPago, Long idPedido, String metodo, BigDecimal monto, LocalDateTime fechaPago, String referencia) {

    public static PagoDto de(Pago p) {
        return new PagoDto(
                p.getIdPago(),
                p.getPedido().getIdPedido(),
                p.getMetodoPago().getNombre(),
                p.getMonto(),
                p.getFechaPago(),
                p.getReferencia());
    }
}
