package dev.logistica.api.service;

import dev.logistica.api.dto.JwtResponse;
import dev.logistica.api.dto.LoginRequest;
import dev.logistica.api.dto.RegistroRequest;
import dev.logistica.api.model.Usuario;
import dev.logistica.api.repository.UsuarioRepository;
import dev.logistica.api.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Registra un nuevo usuario en la base de datos.
     * Encripta la contraseña antes de guardarla.
     */
    @Transactional
    public void registrarUsuario(RegistroRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El email ya se encuentra registrado.");
        }

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .build();

        usuarioRepository.save(usuario);
    }

    /**
     * Autentica al usuario usando el AuthenticationManager de Spring Security.
     * Si las credenciales son correctas, genera y devuelve el JWT.
     */
    public JwtResponse autenticar(LoginRequest request) {
        // Esto lanzará una excepción si el usuario o contraseña son incorrectos
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Si llegó hasta aquí, las credenciales son correctas. Buscamos al usuario.
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        Usuario usuarioEntity = usuarioRepository.findByUsername(request.getUsername()).orElseThrow();

        // Generamos el token JWT
        String jwtToken = jwtUtils.generateToken(userDetails);

        // Retornamos el DTO
        return JwtResponse.builder()
                .token(jwtToken)
                .username(usuarioEntity.getUsername())
                .rol(usuarioEntity.getRol().name())
                .build();
    }
}
