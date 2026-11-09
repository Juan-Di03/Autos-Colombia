package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Usuario anónimo al acceder a ruta protegida (/ingreso) debe ser redirigido a /login")
    void testAnonimoRedirigidoALogin() throws Exception {
        mockMvc.perform(get("/ingreso"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Cliente o público general puede acceder a /consultar-celda sin autenticación")
    void testConsultaCeldaEsPublica() throws Exception {
        mockMvc.perform(get("/consultar-celda"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "empleado@autoscolombia.co", roles = {"EMPLEADO"})
    @DisplayName("EMPLEADO no tiene permiso para acceder a /usuarios (debe responder 403 Forbidden)")
    void testEmpleadoAccesoDenegadoAUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@autoscolombia.co", roles = {"ADMINISTRADOR"})
    @DisplayName("ADMINISTRADOR tiene acceso permitido a /usuarios (debe responder 200 OK)")
    void testAdminAccesoPermitidoAUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Login Administrador: genera JWT, guarda sesión y redirige a /usuarios")
    void testLoginAdminExitosoRedirigeAUsuarios() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("identificador", "admin@autoscolombia.co")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios"))
                .andExpect(cookie().exists(JwtAuthenticationFilter.JWT_COOKIE_NAME));
    }

    @Test
    @DisplayName("Login Empleado: genera JWT, guarda sesión y redirige a /ingreso")
    void testLoginEmpleadoExitosoRedirigeAIngreso() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("identificador", "valerojas@autoscolombia.co")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingreso"))
                .andExpect(cookie().exists(JwtAuthenticationFilter.JWT_COOKIE_NAME));
    }

    @Test
    @DisplayName("Login Cliente: genera JWT, guarda sesión y redirige a /consultar-celda")
    void testLoginClienteExitosoRedirigeAConsultaCelda() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("identificador", "1009876543")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consultar-celda"))
                .andExpect(cookie().exists(JwtAuthenticationFilter.JWT_COOKIE_NAME));
    }

    @Test
    @DisplayName("Login con credenciales incorrectas muestra mensaje de error")
    void testLoginCredencialesIncorrectas() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("identificador", "admin@autoscolombia.co")
                        .param("password", "password_invalida"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("errorLogin"));
    }

    @Test
    @DisplayName("REST API /api/auth/login: emite token JWT y rol correspondiente")
    void testRestApiLoginEmiteTokenJwt() throws Exception {
        String jsonBody = "{\"identificador\":\"admin@autoscolombia.co\",\"password\":\"123456\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"))
                .andExpect(cookie().exists(JwtAuthenticationFilter.JWT_COOKIE_NAME));
    }
}
