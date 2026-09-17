package dev.logistica.api.dto;

import dev.logistica.api.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroRequest {
    
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message = "Debe especificar un rol válido (ROLE_ADMIN, ROLE_EMPLEADO, ROLE_CLIENTE)")
    private Rol rol;
}
