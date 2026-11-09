package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.model.RolUsuario;
import com.autoscolombia.parqueadero.model.Usuario;
import com.autoscolombia.parqueadero.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "admin@autoscolombia.co", roles = {"ADMINISTRADOR"})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    @DisplayName("PT-11: GET /usuarios debe responder 200 OK y cargar modelo con lista de usuarios")
    void testListarUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attributeExists("roles"));
    }

    @Test
    @DisplayName("PT-14: GET /usuarios/nuevo debe responder 200 OK y presentar formulario vacío")
    void testFormularioNuevo() throws Exception {
        mockMvc.perform(get("/usuarios/nuevo"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attribute("esEdicion", false));
    }

    @Test
    @DisplayName("PT-12: POST /usuarios/guardar con datos válidos debe redirigir a /usuarios con mensaje de éxito")
    void testGuardarUsuarioExitoso() throws Exception {
        mockMvc.perform(post("/usuarios/guardar")
                        .with(csrf())
                        .param("documento", "4455667788")
                        .param("nombre", "Usuario Test MVC")
                        .param("telefono", "3124445566")
                        .param("correo", "testmvc@autoscolombia.co")
                        .param("rol", "CLIENTE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(flash().attributeExists("exito"));
    }

    @Test
    @DisplayName("PT-15: GET /usuarios/{id}/editar carga formulario con datos existentes")
    void testFormularioEditar() throws Exception {
        Usuario u = usuarioService.guardar(new Usuario("3322114455", "Editar Mock Test", "3000000000", RolUsuario.CLIENTE));

        mockMvc.perform(get("/usuarios/" + u.getCodigo() + "/editar"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("esEdicion", true))
                .andExpect(model().attributeExists("usuario"));
    }
}
