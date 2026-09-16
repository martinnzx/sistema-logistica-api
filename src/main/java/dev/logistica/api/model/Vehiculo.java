package dev.logistica.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String patente;

    @Positive
    private Double capacidadMaxPesoKg;

    @Positive
    private Double capacidadMaxVolDm3;

    @NotNull
    private Boolean refrigerado;

    private Double rangoTemperaturaMin;

    private Double rangoTemperaturaMax;
}

