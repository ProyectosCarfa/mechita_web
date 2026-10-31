package com.mechita.mechita_web.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "carritos")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Long idCarrito;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false, unique = true)
    private Cliente cliente;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CarritoItem> items = new ArrayList<>();

    public CarritoItem buscarItem(Long idProducto) {
        return items.stream()
                .filter(i -> i.getProducto().getIdProducto().equals(idProducto))
                .findFirst()
                .orElse(null);
    }

    public void agregarItem(Producto producto, int cantidad) {
        CarritoItem item = new CarritoItem();
        item.setCarrito(this);
        item.setProducto(producto);
        item.setCantidad(cantidad);
        items.add(item);
    }

    public int getUnidades() {
        return items.stream().mapToInt(CarritoItem::getCantidad).sum();
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
