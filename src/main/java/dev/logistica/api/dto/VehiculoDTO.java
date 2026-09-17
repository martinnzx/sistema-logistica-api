package dev.logistica.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VehiculoDTO {
        @NotBlank(message = "La patente no puede estar vacía")
    @Size(min = 6, max = 7, message = "La patente debe tener entre 6 y 7 caracteres")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "La patente solo puede contener letras mayúsculas y números")
    private String patente;

        @NotNull(message = "La capacidad de peso es obligatoria")
    @Positive(message = "La capacidad de peso debe ser mayor a cero")
    private Double capacidadMaxPesoKg;

        @NotNull(message = "La capacidad de volumen es obligatoria")
    @Positive(message = "La capacidad de volumen debe ser mayor a cero")
    private Double capacidadMaxVolDm3;

        @NotNull(message = "Debe especificar si es refrigerado")
    private Boolean refrigerado;

        private Double rangoTemperaturaMin;

        private Double rangoTemperaturaMax;
}