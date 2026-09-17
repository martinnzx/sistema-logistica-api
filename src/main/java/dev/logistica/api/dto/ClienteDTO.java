package dev.logistica.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ClienteDTO {
        private Long id;

        @NotBlank(message = "El nombre o razón social es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombreRazonSocial;

        @NotBlank(message = "El documento o CUIT es obligatorio")
    @Size(min = 7, max = 11, message = "El documento/CUIT debe tener entre 7 y 11 dígitos")
    private String documentoOCuit;

        @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

        @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ingresar un formato de email válido")
    private String email;

        @NotBlank(message = "La dirección es obligatoria")
    private String direccionPrincipal;

        @NotBlank(message = "El código postal es obligatorio")
    @Size(max = 10, message = "El código postal es demasiado largo")
    private String codigoPostal;
}

