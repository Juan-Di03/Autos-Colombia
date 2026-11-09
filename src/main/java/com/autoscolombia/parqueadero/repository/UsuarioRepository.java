package com.autoscolombia.parqueadero.repository;

import com.autoscolombia.parqueadero.model.RolUsuario;
import com.autoscolombia.parqueadero.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByDocumento(String documento);
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByDocumentoIgnoreCaseOrCorreoIgnoreCase(String documento, String correo);
    List<Usuario> findByRol(RolUsuario rol);
    Optional<Usuario> findFirstByRol(RolUsuario rol);
}
