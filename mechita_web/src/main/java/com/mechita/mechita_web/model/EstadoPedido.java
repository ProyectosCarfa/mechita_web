package com.mechita.mechita_web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "estados_pedido")
public class EstadoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Long idEstado;

    @NotBlank
    @Size(max = 30)
    @Column(name = "nombre", nullable = false, unique = true, length = 30)
    private String nombre;
}
