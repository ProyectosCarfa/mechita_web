package com.mechita.mechita_web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "distritos")
public class Distrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_distrito")
    private Long idDistrito;

    @NotNull(message = "La provincia es obligatoria")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_provincia", nullable = false)
    private Provincia provincia;

    @NotBlank(message = "El nombre del distrito no puede estar vacío")
    @Size(max = 60)
    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;
}
