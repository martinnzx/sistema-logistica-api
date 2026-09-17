package dev.logistica.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "El usuario (email) no puede estar vacío")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
}
