package com.mechita.mechita_web.service;

import com.mechita.mechita_web.exception.RecursoNoEncontradoException;
import com.mechita.mechita_web.exception.ReglaNegocioException;
import com.mechita.mechita_web.model.MetodoPago;
import com.mechita.mechita_web.model.Pago;
import com.mechita.mechita_web.model.Pedido;
import com.mechita.mechita_web.repository.EstadoPedidoRepository;
import com.mechita.mechita_web.repository.MetodoPagoRepository;
import com.mechita.mechita_web.repository.PagoRepository;
import com.mechita.mechita_web.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EstadoPedidoRepository estadoRepository;

    @Transactional(readOnly = true)
    public List<Pago> listar() {
        return pagoRepository.findAllByOrderByFechaPagoDesc();
    }

    @Transactional(readOnly = true)
    public BigDecimal totalRecaudado() {
        return pagoRepository.totalRecaudado();
    }

    @Transactional
    public Pago registrar(Long idPedido, Long idMetodoPago, String referencia) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + idPedido));
        MetodoPago metodo = metodoPagoRepository.findById(idMetodoPago)
                .orElseThrow(() -> new RecursoNoEncontradoException("Método de pago no encontrado: " + idMetodoPago));

        if (pedido.getPago() != null) {
            throw new ReglaNegocioException("El pedido ya tiene un pago registrado");
        }
        if ("CANCELADO".equals(pedido.getEstado().getNombre())) {
            throw new ReglaNegocioException("No se puede pagar un pedido cancelado");
        }

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMetodoPago(metodo);
        pago.setMonto(pedido.getTotal());
        pago.setReferencia(referencia);
        pedido.setPago(pago);

        if ("PENDIENTE".equals(pedido.getEstado().getNombre())) {
            estadoRepository.findByNombre("CONFIRMADO").ifPresent(pedido::setEstado);
        }
        return pagoRepository.save(pago);
    }
}
