package dev.logistica.api.dto;

import dev.logistica.api.enums.NivelFragilidad;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Datos para registrar un paquete. Nota: Los campos requeridos dependen del 'tipo' seleccionado.")
public class PaqueteDTO {
    @Schema(description = "Código único de seguimiento (Autogenerado). No enviar.", accessMode = Schema.AccessMode.READ_ONLY)
    private String codigo;

    @Schema(description = "Tipo de paquete. Determina qué otros campos son obligatorios.",
            example = "FRAGIL",
            allowableValues = {"FRAGIL", "REFRIGERADOS", "ESTANDAR"})
    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    @Schema(description = "Peso total en Kg.", example = "15.5")
    @NotNull(message = "El peso es obligatorio")
    @Positive
    private Double pesoKg;

    @Schema(description = "Volumen total en dm3.", example = "20.0")
    @NotNull(message = "El volumen es obligatorio")
    @Positive
    private Double volumenDm3;

    // --- EXCLUSIVO FRÁGIL ---

    @Schema(description = "[Solo FRAGIL] Nivel de cuidado requerido.",
            example = "ALTO",
            nullable = true)
    private NivelFragilidad nivelFragilidad;

    @Schema(description = "[Solo FRAGIL] Indica si requiere seguro extra.",
            example = "true",
            defaultValue = "false",
            nullable = true)
    private Boolean seguroAdicional;

    // --- EXCLUSIVO REFRIGERADOS ---

    @Schema(description = "[Solo REFRIGERADOS] Temperatura ideal de transporte en °C.",
            example = "-5.0",
            nullable = true)
    private Double temperaturaObjetivo;

    @Schema(description = "[Solo REFRIGERADOS] Temperatura mínima tolerada en °C.",
            example = "-10.0",
            nullable = true)
    private Double rangoMin;

    @Schema(description = "[Solo REFRIGERADOS] Temperatura máxima tolerada en °C.",
            example = "0.0",
            nullable = true)
    private Double rangoMax;

    @Schema(description = "[Solo REFRIGERADOS] Tiempo máximo permitido fuera de la cadena de frío (Horas).",
            example = "4",
            nullable = true)
    private Integer horasMaxFueraDeFrio;
}

