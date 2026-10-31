package com.mechita.mechita_web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 80, message = "Los nombres no deben superar los 80 caracteres")
    @Column(name = "nombres", nullable = false, length = 80)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 80, message = "Los apellidos no deben superar los 80 caracteres")
    @Column(name = "apellidos", nullable = false, length = 80)
    private String apellidos;

    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "dni", unique = true, length = 8)
    private String dni;

    @Size(max = 15, message = "El teléfono no debe superar los 15 caracteres")
    @Column(name = "telefono", length = 15)
    private String telefono;
}
