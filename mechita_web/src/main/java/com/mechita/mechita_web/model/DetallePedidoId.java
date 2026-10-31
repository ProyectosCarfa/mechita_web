package com.mechita.mechita_web.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class DetallePedidoId implements Serializable {

    @Column(name = "id_pedido")
    private Long idPedido;

    @Column(name = "id_producto")
    private Long idProducto;
}
