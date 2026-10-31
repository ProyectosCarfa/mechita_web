package com.mechita.mechita_web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "carrito_items")
public class CarritoItem {

    @EmbeddedId
    private CarritoItemId id = new CarritoItemId();

    @MapsId("idCarrito")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrito")
    private Carrito carrito;

    @MapsId("idProducto")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @NotNull
    @Positive(message = "La cantidad debe ser mayor a cero")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 1;

    public BigDecimal getSubtotal() {
        return producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }
}
