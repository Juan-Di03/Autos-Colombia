package com.autoscolombia.parqueadero.service;

import com.autoscolombia.parqueadero.exception.ReglaNegocioException;
import com.autoscolombia.parqueadero.model.RolUsuario;
import com.autoscolombia.parqueadero.model.Usuario;
import com.autoscolombia.parqueadero.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("PT-12: Registrar usuario con datos válidos lo persiste en la base de datos")
    void testGuardarUsuarioValido() {
        Usuario nuevo = new Usuario("9988776655", "Carlos Alberto Gómez", "3112223344", RolUsuario.CLIENTE);
        nuevo.setCorreo("carlos.gomez@gmail.com");

        Usuario guardado = usuarioService.guardar(nuevo);

        assertNotNull(guardado.getCodigo());
        assertEquals("9988776655", guardado.getDocumento());
        assertEquals("Carlos Alberto Gómez", guardado.getNombre());
        assertEquals(RolUsuario.CLIENTE, guardado.getRol());
    }

    @Test
    @DisplayName("PT-13: Registrar usuario con documento duplicado lanza ReglaNegocioException")
    void testGuardarUsuarioDocumentoDuplicado() {
        Usuario u1 = new Usuario("1122334455", "Usuario Original", "3001111111", RolUsuario.CLIENTE);
        usuarioService.guardar(u1);

        Usuario u2 = new Usuario("1122334455", "Usuario Clon", "3002222222", RolUsuario.EMPLEADO);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> usuarioService.guardar(u2));
        assertEquals("DOC_DUPLICADO", ex.getTipoError());
    }

    @Test
    @DisplayName("PT-15: Editar usuario actualiza sus campos correctamente")
    void testActualizarUsuario() {
        Usuario inicial = usuarioService.guardar(
                new Usuario("5544332211", "Laura Morales", "3158889900", RolUsuario.CLIENTE)
        );

        Usuario datosNuevos = new Usuario("5544332211", "Laura Morales Vélez", "3199999999", RolUsuario.EMPLEADO);
        datosNuevos.setCorreo("laura.morales@autoscolombia.co");

        Usuario actualizado = usuarioService.actualizar(inicial.getCodigo(), datosNuevos);

        assertEquals("Laura Morales Vélez", actualizado.getNombre());
        assertEquals("3199999999", actualizado.getTelefono());
        assertEquals(RolUsuario.EMPLEADO, actualizado.getRol());
        assertEquals("laura.morales@autoscolombia.co", actualizado.getCorreo());
    }

    @Test
    @DisplayName("PT-16: Eliminar usuario sin relaciones lo borra del repositorio")
    void testEliminarUsuario() {
        Usuario creado = usuarioService.guardar(
                new Usuario("7788990011", "Usuario Temporal", "3200000000", RolUsuario.CLIENTE)
        );
        Long id = creado.getCodigo();

        usuarioService.eliminar(id);

        Optional<Usuario> buscado = usuarioRepository.findById(id);
        assertTrue(buscado.isEmpty());
    }

    @Test
    @DisplayName("PT-18 / PT-19: Buscar usuarios con filtros por documento y rol")
    void testBuscarConFiltros() {
        usuarioService.guardar(new Usuario("8811223344", "Filtro Doc Test", "3003334455", RolUsuario.EMPLEADO));

        List<Usuario> porDoc = usuarioService.buscarConFiltros("8811223344", null);
        assertEquals(1, porDoc.size());
        assertEquals("Filtro Doc Test", porDoc.get(0).getNombre());

        List<Usuario> porRol = usuarioService.buscarConFiltros(null, RolUsuario.EMPLEADO);
        assertFalse(porRol.isEmpty());
        assertTrue(porRol.stream().allMatch(u -> u.getRol() == RolUsuario.EMPLEADO));
    }
}
