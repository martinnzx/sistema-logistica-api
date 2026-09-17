package dev.logistica.api.controller;

import dev.logistica.api.controller.dto.MensajeError;
import dev.logistica.api.dto.JwtResponse;
import dev.logistica.api.dto.LoginRequest;
import dev.logistica.api.dto.RegistroRequest;
import dev.logistica.api.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints públicos para registro y login de usuarios")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest request) {
        log.info("Intento de registro para usuario: {}", request.getUsername());
        authService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MensajeError("Usuario registrado exitosamente. Ya puede iniciar sesión."));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getUsername());
        JwtResponse response = authService.autenticar(request);
        return ResponseEntity.ok(response);
    }
}
