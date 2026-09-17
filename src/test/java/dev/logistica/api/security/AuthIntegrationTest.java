package dev.logistica.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.logistica.api.dto.LoginRequest;
import dev.logistica.api.dto.RegistroRequest;
import dev.logistica.api.enums.Rol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("SDD - Registro y Login exitoso devuelven los estados y tokens correctos")
    void alRegistrarYLoguear_deberiaDevolverToken() throws Exception {
        // 1. Probamos el Registro
        RegistroRequest registro = new RegistroRequest("admin@test.com", "password123", Rol.ROLE_ADMIN);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado exitosamente. Ya puede iniciar sesión."));

        // 2. Probamos el Login
        LoginRequest login = new LoginRequest("admin@test.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("admin@test.com"))
                .andExpect(jsonPath("$.rol").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("SDD - Login con password incorrecta devuelve 401")
    void alLoguearConCredencialesMalas_deberiaDevolver401() throws Exception {
        LoginRequest login = new LoginRequest("admin@test.com", "malaPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Usuario o contraseña incorrectos."));
    }
}
