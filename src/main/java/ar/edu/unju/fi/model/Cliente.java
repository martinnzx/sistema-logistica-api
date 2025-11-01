package ar.edu.unju.fi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "Clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String documento;

    @NotBlank
    @Column(nullable = false)
    private String telefono;

    @NotBlank
    @Column(nullable = false)
    private String direccionPrincipal;

    @NotBlank
    @Column(length = 10, nullable = false)
    private String codigoPostal;
}