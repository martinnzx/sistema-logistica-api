package dev.logistica.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Este método intercepta CADA petición HTTP que llega al servidor.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Extraemos el Header llamado "Authorization"
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 2. Si no hay header o no empieza con "Bearer ", ignoramos y dejamos pasar la petición
        // (Spring Security luego decidirá si rechazarla si la ruta requería auth)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraemos el Token (quitando la palabra "Bearer ")
        jwt = authHeader.substring(7);
        
        // 4. Extraemos el username del Token
        username = jwtUtils.extractUsername(jwt);

        // 5. Si hay username y aún no está autenticado en el contexto actual de Spring
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Buscamos al usuario en la BD
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validamos que el token sea correcto (firma ok y no expirado)
            if (jwtUtils.isTokenValid(jwt, userDetails)) {
                
                // Creamos un objeto de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                
                // Le agregamos detalles técnicos (IP, sesión, etc)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // FINALMENTE: Guardamos la autenticación en el Contexto de Spring. 
                // A partir de aquí, Spring sabe que este usuario está logueado.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // Continuamos con el flujo (al siguiente filtro o al controlador final)
        filterChain.doFilter(request, response);
    }
}
