package com.mechita.mechita_web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "departamentos")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_departamento")
    private Long idDepartamento;

    @NotBlank(message = "El nombre del departamento no puede estar vacío")
    @Size(max = 60)
    @Column(name = "nombre", nullable = false, unique = true, length = 60)
    private String nombre;
}
