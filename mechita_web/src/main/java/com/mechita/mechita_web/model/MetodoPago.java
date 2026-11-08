package com.mechita.mechita_web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "metodos_pago")
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Long idMetodoPago;

    @NotBlank
    @Size(max = 40)
    @Column(name = "nombre", nullable = false, unique = true, length = 40)
    private String nombre;

    @Size(max = 255, message = "La URL de la imagen no debe superar los 255 caracteres")
    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;
}