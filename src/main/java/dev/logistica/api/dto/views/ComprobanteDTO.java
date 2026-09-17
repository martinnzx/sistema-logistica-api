package dev.logistica.api.dto.views;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteDTO {
    @NotBlank(message = "El comprobante no puede estar vacío")
    private String comprobante;
}

