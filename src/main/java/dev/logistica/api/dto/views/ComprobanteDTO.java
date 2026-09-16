package dev.logistica.api.dto.views;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteDTO {
    @Schema(
            description = "Texto del comprobante o hash de firma",
            example = "Entregado en recepción a Juan Pérez - Código #A99"
    )
    @NotBlank(message = "El comprobante no puede estar vacío")
    private String comprobante;
}

