package ar.edu.unju.fi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Información fiscal y de contacto del Cliente.")
public class ClienteDTO {
    @Schema(
            description = "Identificador único (Auto-generado por el sistema). No enviar al crear.",
            example = "10",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Nombre completo de la persona o Razón Social de la empresa.",
            example = "Logística Norte S.A."
    )
    @NotBlank(message = "El nombre o razón social es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombreRazonSocial;

    @Schema(
            description = "Número de documento (DNI) o CUIT sin guiones ni puntos.",
            example = "20301234567"
    )
    @NotBlank(message = "El documento o CUIT es obligatorio")
    @Size(min = 7, max = 11, message = "El documento/CUIT debe tener entre 7 y 11 dígitos")
    private String documentoOCuit;

    @Schema(
            description = "Teléfono de contacto principal.",
            example = "+5491122334455"
    )
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Schema(
            description = "Correo electrónico para notificaciones y facturación.",
            example = "contacto@logisticanorte.com.ar"
    )
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ingresar un formato de email válido")
    private String email;

    @Schema(
            description = "Dirección física principal (Calle y altura).",
            example = "Av. General Belgrano 1234"
    )
    @NotBlank(message = "La dirección es obligatoria")
    private String direccionPrincipal;

    @Schema(
            description = "Código Postal de la localidad.",
            example = "4600"
    )
    @NotBlank(message = "El código postal es obligatorio")
    @Size(max = 10, message = "El código postal es demasiado largo")
    private String codigoPostal;
}
