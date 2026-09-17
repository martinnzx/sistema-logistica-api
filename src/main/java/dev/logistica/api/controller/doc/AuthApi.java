package dev.logistica.api.controller.doc;

import dev.logistica.api.controller.dto.MensajeError;
import dev.logistica.api.dto.JwtResponse;
import dev.logistica.api.dto.LoginRequest;
import dev.logistica.api.dto.RegistroRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Endpoints para registro y login de usuarios")
public interface AuthApi {

    @Operation(summary = "Registrar un nuevo usuario en el sistema", description = "Crea un usuario con rol USER o ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente", content = @Content(schema = @Schema(implementation = MensajeError.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "El usuario ya existe", content = @Content)
    })
    @PostMapping("/register")
    ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest request);

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa", content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas", content = @Content)
    })
    @PostMapping("/login")
    ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request);
}
