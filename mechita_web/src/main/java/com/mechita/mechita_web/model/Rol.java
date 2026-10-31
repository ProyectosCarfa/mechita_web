package com.mechita.mechita_web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @NotBlank(message = "El nombre del rol no puede estar vacío")
    @Size(max = 30, message = "El nombre del rol no debe superar los 30 caracteres")
    @Column(name = "nombre", nullable = false, unique = true, length = 30)
    private String nombre;
}