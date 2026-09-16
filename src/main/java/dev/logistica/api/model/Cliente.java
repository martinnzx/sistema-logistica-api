package dev.logistica.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombreRazonSocial;

    @NotBlank
    @Column(name = "documento_cuit")
    private String documentoOCuit;

    @NotBlank
    private String telefono;

    @Email
    private String email;

    @NotBlank
    private String direccionPrincipal;

    @NotBlank
    private String codigoPostal;
}
