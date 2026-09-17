package dev.logistica.api.security;

import dev.logistica.api.model.Usuario;
import dev.logistica.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Spring Security llama a este método automáticamente durante el Login (y en nuestro Filtro) 
     * para buscar si el usuario existe en nuestra base de datos.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscamos nuestra entidad Usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Lo convertimos al objeto UserDetails que entiende Spring Security
        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(usuario.getRol().name()))
        );
    }
}
