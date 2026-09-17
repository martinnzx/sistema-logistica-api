package dev.logistica.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Esta es la pieza central de Spring Security. 
     * Define qué rutas son públicas, cuáles requieren roles, y qué filtros se aplican.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitamos CSRF porque trabajamos con tokens Stateless
            .csrf(csrf -> csrf.disable())

            // 2. Definimos las reglas de autorización basadas en nuestra Especificación (SDD)
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Rutas que requieren permisos específicos (Matriz de Autorización)
                .requestMatchers(HttpMethod.POST, "/api/envios").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLEADO")
                .requestMatchers(HttpMethod.PUT, "/api/envios/*/avanzar").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLEADO")
                .requestMatchers(HttpMethod.GET, "/api/envios/remitente/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLEADO", "ROLE_CLIENTE")
                .requestMatchers("/api/clientes/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLEADO")

                // Cualquier otra petición requiere autenticación mínima
                .anyRequest().authenticated()
            )

            // 3. Indicamos que no guarde sesiones (STATELESS) porque cada petición tendrá su propio JWT
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 4. Configurar manejo de excepciones para retornar 401 Unauthorized en peticiones sin autenticación
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "No autorizado")
                )
            )

            // 5. Conectamos nuestro proveedor de autenticación y nuestro filtro JWT
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Proveedor de autenticación que usa nuestro CustomUserDetailsService y nuestro Encriptador
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Nos permite inyectar el AuthenticationManager en los controladores (ej: para el Login)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Encriptador de contraseñas. Usa el algoritmo BCrypt, el estándar seguro.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
