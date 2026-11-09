package com.autoscolombia.parqueadero.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ConsultaClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /consultar-celda sin parámetros debe responder 200 OK y presentar buscador")
    void testConsultarCeldaVistaInicial() throws Exception {
        mockMvc.perform(get("/consultar-celda"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("consultado", false))
                .andExpect(model().attributeDoesNotExist("ticketActivo"));
    }

    @Test
    @DisplayName("GET /consultar-celda?placa=INEXISTENTE debe responder 200 OK e indicar no encontrado")
    void testConsultarCeldaInexistente() throws Exception {
        mockMvc.perform(get("/consultar-celda").param("placa", "ZZZ999"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("consultado", true))
                .andExpect(model().attributeDoesNotExist("ticketActivo"));
    }
}
