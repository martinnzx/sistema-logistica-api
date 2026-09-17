package dev.logistica.api.dto;

import dev.logistica.api.enums.NivelFragilidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaqueteDTO {
        private String codigo;

        @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

        @NotNull(message = "El peso es obligatorio")
    @Positive
    private Double pesoKg;

        @NotNull(message = "El volumen es obligatorio")
    @Positive
    private Double volumenDm3;

    // --- EXCLUSIVO FRÁGIL ---

        private NivelFragilidad nivelFragilidad;

        private Boolean seguroAdicional;

    // --- EXCLUSIVO REFRIGERADOS ---

        private Double temperaturaObjetivo;

        private Double rangoMin;

        private Double rangoMax;

        private Integer horasMaxFueraDeFrio;
}

