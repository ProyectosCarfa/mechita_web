package com.mechita.mechita_web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(max = 80, message = "El nombre no debe superar los 80 caracteres")
    @Column(name = "nombre", nullable = false, unique = true, length = 80)
    private String nombre;

    @Size(max = 255, message = "La descripción no debe superar los 255 caracteres")
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Size(max = 255, message = "La URL de la imagen no debe superar los 255 caracteres")
    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @NotNull(message = "El estado activo es obligatorio")
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}