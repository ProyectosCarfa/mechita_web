package com.mechita.mechita_web.service;

import com.mechita.mechita_web.exception.RecursoNoEncontradoException;
import com.mechita.mechita_web.exception.ReglaNegocioException;
import com.mechita.mechita_web.model.*;
import com.mechita.mechita_web.repository.CarritoRepository;
import com.mechita.mechita_web.repository.DireccionRepository;
import com.mechita.mechita_web.repository.EstadoPedidoRepository;
import com.mechita.mechita_web.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final DireccionRepository direccionRepository;
    private final EstadoPedidoRepository estadoRepository;

    @Transactional(readOnly = true)
    public List<Pedido> listar() {
        return pedidoRepository.findAllByOrderByFechaPedidoDesc();
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorEstado(String estado) {
        return pedidoRepository.findByEstado_NombreOrderByFechaPedidoDesc(estado.trim().toUpperCase());
    }

    @Transactional(readOnly = true)
    public Pedido buscar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + id));
    }

    @Transactional
    public Pedido crearDesdeCarrito(Cliente cliente, Long idDireccion) {
        Carrito carrito = carritoRepository.findByCliente(cliente)
                .filter(c -> !c.getItems().isEmpty())
                .orElseThrow(() -> new ReglaNegocioException("El carrito está vacío"));

        Direccion direccion = direccionRepository.findById(idDireccion)
                .filter(d -> d.getCliente().getIdCliente().equals(cliente.getIdCliente()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Dirección no válida"));

        EstadoPedido pendiente = estadoRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new ReglaNegocioException("No existe el estado PENDIENTE en la base de datos"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDireccion(direccion);
        pedido.setEstado(pendiente);

        for (CarritoItem item : carrito.getItems()) {
            Producto producto = item.getProducto();
            if (producto.getStock() < item.getCantidad()) {
                throw new ReglaNegocioException("Stock insuficiente de " + producto.getNombre());
            }
            producto.setStock(producto.getStock() - item.getCantidad());
            pedido.agregarDetalle(producto, item.getCantidad());
        }

        carrito.getItems().clear();
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cambiarEstado(Long idPedido, String nombreEstado) {
        Pedido pedido = buscar(idPedido);
        EstadoPedido nuevo = estadoRepository.findByNombre(nombreEstado.trim().toUpperCase())
                .orElseThrow(() -> new ReglaNegocioException("Estado inválido: " + nombreEstado));

        if ("CANCELADO".equals(pedido.getEstado().getNombre())) {
            throw new ReglaNegocioException("No se puede modificar un pedido cancelado");
        }
        if ("CANCELADO".equals(nuevo.getNombre())) {
            pedido.getDetalles().forEach(d ->
                    d.getProducto().setStock(d.getProducto().getStock() + d.getCantidad()));
        }
        pedido.setEstado(nuevo);
        return pedido;
    }
}
