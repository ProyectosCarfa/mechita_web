package com.mechita.mechita_web.service;

import com.mechita.mechita_web.exception.RecursoNoEncontradoException;
import com.mechita.mechita_web.exception.ReglaNegocioException;
import com.mechita.mechita_web.model.Carrito;
import com.mechita.mechita_web.model.CarritoItem;
import com.mechita.mechita_web.model.Cliente;
import com.mechita.mechita_web.model.Producto;
import com.mechita.mechita_web.repository.CarritoRepository;
import com.mechita.mechita_web.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public Carrito obtenerOCrear(Cliente cliente) {
        return carritoRepository.findByCliente(cliente).orElseGet(() -> {
            Carrito nuevo = new Carrito();
            nuevo.setCliente(cliente);
            return carritoRepository.save(nuevo);
        });
    }

    @Transactional
    public Carrito agregar(Cliente cliente, Long idProducto, int cantidad) {
        if (cantidad < 1) {
            throw new ReglaNegocioException("La cantidad debe ser al menos 1");
        }
        Producto producto = productoRepository.findById(idProducto)
                .filter(Producto::getActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no disponible: " + idProducto));

        Carrito carrito = obtenerOCrear(cliente);
        CarritoItem item = carrito.buscarItem(idProducto);
        int total = cantidad + (item == null ? 0 : item.getCantidad());

        if (total > producto.getStock()) {
            throw new ReglaNegocioException("Stock insuficiente de " + producto.getNombre() + " (disponible: " + producto.getStock() + ")");
        }

        if (item == null) {
            carrito.agregarItem(producto, cantidad);
        } else {
            item.setCantidad(total);
        }
        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito quitar(Cliente cliente, Long idProducto) {
        Carrito carrito = obtenerOCrear(cliente);
        carrito.getItems().removeIf(i -> i.getProducto().getIdProducto().equals(idProducto));
        return carrito;
    }

    @Transactional(readOnly = true)
    public int contarUnidades(Cliente cliente) {
        return carritoRepository.findByCliente(cliente).map(Carrito::getUnidades).orElse(0);
    }
}
