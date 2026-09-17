package dev.logistica.api.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("SDD - Acceder a un endpoint protegido sin token debe retornar 401 Unauthorized")
    void alAccederEndpointProtegidoSinToken_debeRetornar401() throws Exception {
        // Intentamos obtener un envío que, según la especificación, requiere autenticación.
        // Dado que hemos agregado spring-security-test y spring-boot-starter-security, 
        // pero aún no hemos configurado quién entra y quién no, 
        // el comportamiento por defecto de Spring Security bloqueará la petición.
        
        mockMvc.perform(get("/api/envios/estado/GENERADO"))
               .andExpect(status().isUnauthorized());
    }
}
