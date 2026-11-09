package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.model.Celda;
import com.autoscolombia.parqueadero.model.EstadoCelda;
import com.autoscolombia.parqueadero.service.CeldaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "admin@autoscolombia.co", roles = {"ADMINISTRADOR"})
class CeldaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CeldaService celdaService;

    @Test
    @DisplayName("PT-20: GET /celdas debe responder 200 OK y presentar tablero con métricas y celdas")
    void testTableroCeldas() throws Exception {
        mockMvc.perform(get("/celdas"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("celdasAuto"))
                .andExpect(model().attributeExists("celdasMoto"))
                .andExpect(model().attributeExists("totalCeldas"))
                .andExpect(model().attributeExists("totalDisponibles"));
    }

    @Test
    @DisplayName("PT-21: POST /celdas/{id}/estado cambia el estado de DISPONIBLE a FUERA_DE_SERVICIO")
    void testCambiarEstadoCelda() throws Exception {
        List<Celda> celdas = celdaService.listarTodas();
        Celda primera = celdas.get(0);

        mockMvc.perform(post("/celdas/" + primera.getCodigo() + "/estado")
                        .with(csrf())
                        .param("nuevoEstado", "FUERA_DE_SERVICIO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/celdas"))
                .andExpect(flash().attributeExists("exito"));

        Celda actualizada = celdaService.buscarPorCodigo(primera.getCodigo()).orElseThrow();
        assertEquals(EstadoCelda.FUERA_DE_SERVICIO, actualizada.getEstado());
    }
}
