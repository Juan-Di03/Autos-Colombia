package com.autoscolombia.parqueadero.security;

import com.autoscolombia.parqueadero.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implementación de UserDetails para Autos Colombia (siguiendo el patrón de RollerApp).
 */
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String documento;
    private final String nombre;
    private final String correo;
    private final String password;
    private final String rol;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String documento, String nombre, String correo, String password, String rol) {
        this.id = id;
        this.documento = documento;
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    /**
     * Construye un UserPrincipal desde la entidad Usuario.
     * La contraseña DEBE existir en la BD (el DataInitializer la garantiza).
     */
    public static UserPrincipal fromUsuario(Usuario usuario) {
        return new UserPrincipal(
                usuario.getCodigo(),
                usuario.getDocumento(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getPassword(),
                usuario.getRol().name()
        );
    }

    public Long getId() {
        return id;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return documento;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
