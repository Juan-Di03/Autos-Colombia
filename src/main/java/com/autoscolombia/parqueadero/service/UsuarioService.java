package com.autoscolombia.parqueadero.service;

import com.autoscolombia.parqueadero.exception.ReglaNegocioException;
import com.autoscolombia.parqueadero.model.RolUsuario;
import com.autoscolombia.parqueadero.model.Usuario;
import com.autoscolombia.parqueadero.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorDocumento(String documento) {
        return usuarioRepository.findByDocumento(documento);
    }

    /**
     * Lista filtrado por documento parcial o rol.
     * Si ambos filtros están vacíos, retorna todos.
     */
    public List<Usuario> buscarConFiltros(String documento, RolUsuario rol) {
        if (documento != null && !documento.isBlank()) {
            return usuarioRepository.findByDocumento(documento)
                    .map(List::of)
                    .orElse(List.of());
        }
        if (rol != null) {
            return usuarioRepository.findByRol(rol);
        }
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        // Validar documento único al crear (sin ID aún)
        if (usuario.getCodigo() == null) {
            usuarioRepository.findByDocumento(usuario.getDocumento()).ifPresent(u -> {
                throw new ReglaNegocioException("DOC_DUPLICADO",
                        "Ya existe un usuario con el documento " + usuario.getDocumento());
            });
        }

        // Asignar y encriptar contraseña por defecto si viene vacía
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode("123456"));
        } else if (!usuario.getPassword().startsWith("$2a$")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Long id, Usuario datosNuevos) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("NO_ENCONTRADO",
                        "Usuario con ID " + id + " no encontrado"));

        // Si cambia documento, verificar que no esté en uso por otro
        if (!existente.getDocumento().equals(datosNuevos.getDocumento())) {
            usuarioRepository.findByDocumento(datosNuevos.getDocumento()).ifPresent(u -> {
                throw new ReglaNegocioException("DOC_DUPLICADO",
                        "El documento " + datosNuevos.getDocumento() + " ya está registrado");
            });
        }

        existente.setDocumento(datosNuevos.getDocumento());
        existente.setNombre(datosNuevos.getNombre());
        existente.setTelefono(datosNuevos.getTelefono());
        existente.setCorreo(datosNuevos.getCorreo());
        existente.setRol(datosNuevos.getRol());
        return usuarioRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("NO_ENCONTRADO",
                        "Usuario con ID " + id + " no encontrado"));
        try {
            usuarioRepository.delete(usuario);
            usuarioRepository.flush();
        } catch (Exception e) {
            throw new ReglaNegocioException("CON_REGISTROS",
                    "No se puede eliminar el usuario porque tiene registros asociados en el sistema.");
        }
    }
}
