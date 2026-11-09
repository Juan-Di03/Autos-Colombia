package com.autoscolombia.parqueadero.security;

import com.autoscolombia.parqueadero.model.Usuario;
import com.autoscolombia.parqueadero.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carga usuarios desde la base de datos para Spring Security.
 * Busca por documento o correo (case-insensitive).
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String termino = username != null ? username.trim() : "";
        Usuario usuario = usuarioRepository.findByDocumentoIgnoreCaseOrCorreoIgnoreCase(termino, termino)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con documento o correo: " + termino));

        return UserPrincipal.fromUsuario(usuario);
    }
}
