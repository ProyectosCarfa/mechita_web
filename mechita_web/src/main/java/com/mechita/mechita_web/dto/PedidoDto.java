package com.mechita.mechita_web.dto;

import com.mechita.mechita_web.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoDto(
        Long idPedido,
        Long idCliente,
        String cliente,
        String direccion,
        String estado,
        LocalDateTime fecha,
        BigDecimal total,
        boolean pagado,
        List<DetalleDto> detalles) {

    public record DetalleDto(Long idProducto, String producto, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
    }

    public static PedidoDto de(Pedido p) {
        List<DetalleDto> detalles = p.getDetalles().stream()
                .map(d -> new DetalleDto(
                        d.getProducto().getIdProducto(),
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal()))
                .toList();
        return new PedidoDto(
                p.getIdPedido(),
                p.getCliente().getIdCliente(),
                p.getCliente().getNombres() + " " + p.getCliente().getApellidos(),
                p.getDireccion().getDireccion() + ", " + p.getDireccion().getDistrito().getNombre(),
                p.getEstado().getNombre(),
                p.getFechaPedido(),
                p.getTotal(),
                p.getPago() != null,
                detalles);
    }
}
