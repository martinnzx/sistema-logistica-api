package dev.logistica.api.dto.views;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioViewDTO {
    @NotBlank(message = "El CUIL del remitente es obligatorio.")
    @Size(min = 6, max = 16, message = "El CUIL del remitente debe tener entre 6 y 11 dígitos/caracteres.")
    @Schema(description = "Identificador fiscal (CUIL) del remitente.", example = "20998877665")
    private String cuilRemitente;

    @NotBlank(message = "El CUIL del destinatario es obligatorio.")
    @Size(min = 6, max = 11, message = "El CUIL del destinatario debe tener entre 6 y 11 dígitos/caracteres.")
    @Schema(description = "Identificador fiscal (CUIL) del destinatario.", example = "27112233449")
    private String cuilDestinatario;

    @NotBlank(message = "La dirección de entrega es obligatoria.")
    @Size(max = 100, message = "La dirección de entrega no puede superar los 100 caracteres.")
    @Schema(description = "Dirección física donde se debe realizar la entrega.", example = "Av. Siempre Viva 742")
    private String direccionEntrega;

    @NotBlank(message = "El código postal es obligatorio.")
    @Size(min = 4, max = 10, message = "El código postal debe tener entre 4 y 10 caracteres.")
    @Schema(description = "Código postal de la zona de entrega.", example = "C1010AAR")
    private String codigoPostal;

    @Schema(description = "Indica si el envío requiere condiciones especiales de frío.", example = "true")
    private Boolean requiereFrio = false; // Se puede inicializar para que no sea null

    @NotBlank(message = "La lista de paquetes no puede estar vacía.")
    @Schema(description = "Lista de identificadores de los paquetes incluidos en el envío.", example = "[\"PAQ-001\", \"PAQ-002\"]")
    private List<String> paquetes;
}

