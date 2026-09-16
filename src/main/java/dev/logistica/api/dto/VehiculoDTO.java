package dev.logistica.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Schema(description = "Modelo para la creación de un nuevo vehículo de transporte.")
public class VehiculoDTO {
    @Schema(
            description = "Patente o matrícula del vehículo. Debe ser única.",
            example = "AA123BB"
    )
    @NotBlank(message = "La patente no puede estar vacía")
    @Size(min = 6, max = 7, message = "La patente debe tener entre 6 y 7 caracteres")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "La patente solo puede contener letras mayúsculas y números")
    private String patente;

    @Schema(
            description = "Capacidad máxima de peso en Kilogramos.",
            example = "1500.5"
    )
    @NotNull(message = "La capacidad de peso es obligatoria")
    @Positive(message = "La capacidad de peso debe ser mayor a cero")
    private Double capacidadMaxPesoKg;

    @Schema(
            description = "Capacidad máxima de volumen en Decímetros cúbicos (Litros).",
            example = "3000.0"
    )
    @NotNull(message = "La capacidad de volumen es obligatoria")
    @Positive(message = "La capacidad de volumen debe ser mayor a cero")
    private Double capacidadMaxVolDm3;

    @Schema(
            description = "Indica si el vehículo cuenta con sistema de refrigeración.",
            example = "true"
    )
    @NotNull(message = "Debe especificar si es refrigerado")
    private Boolean refrigerado;

    @Schema(
            description = "Temperatura mínima soportada (Obligatorio si es refrigerado).",
            example = "-10.0"
    )
    private Double rangoTemperaturaMin;

    @Schema(
            description = "Temperatura máxima soportada (Obligatorio si es refrigerado).",
            example = "5.0"
    )
    private Double rangoTemperaturaMax;
}

