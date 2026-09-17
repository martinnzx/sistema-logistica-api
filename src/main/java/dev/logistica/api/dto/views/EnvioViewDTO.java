package dev.logistica.api.dto.views;

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
    del remitente.", example = "20998877665")
    private String cuilRemitente;

    @NotBlank(message = "El CUIL del destinatario es obligatorio.")
    @Size(min = 6, max = 11, message = "El CUIL del destinatario debe tener entre 6 y 11 dígitos/caracteres.")
    del destinatario.", example = "27112233449")
    private String cuilDestinatario;

    @NotBlank(message = "La dirección de entrega es obligatoria.")
    @Size(max = 100, message = "La dirección de entrega no puede superar los 100 caracteres.")
    private String direccionEntrega;

    @NotBlank(message = "El código postal es obligatorio.")
    @Size(min = 4, max = 10, message = "El código postal debe tener entre 4 y 10 caracteres.")
    private String codigoPostal;

    private Boolean requiereFrio = false; // Se puede inicializar para que no sea null

    @NotBlank(message = "La lista de paquetes no puede estar vacía.")
    private List<String> paquetes;
}

