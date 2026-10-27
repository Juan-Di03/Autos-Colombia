package com.autoscolombia.parqueadero.service;

import com.autoscolombia.parqueadero.dto.RegistroIngresoDto;
import com.autoscolombia.parqueadero.dto.ValidacionSalidaDto;
import com.autoscolombia.parqueadero.exception.ReglaNegocioException;
import com.autoscolombia.parqueadero.model.*;
import com.autoscolombia.parqueadero.repository.CeldaRepository;
import com.autoscolombia.parqueadero.repository.EstadiaRepository;
import com.autoscolombia.parqueadero.repository.NovedadRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EstadiaSalidaServiceTest {

    @Autowired
    private EstadiaService estadiaService;

    @Autowired
    private CeldaRepository celdaRepository;

    @Autowired
    private EstadiaRepository estadiaRepository;

    @Autowired
    private NovedadRepository novedadRepository;

    @Test
    @DisplayName("HU-06, HU-07, HU-08: Confirmar salida válida libera la celda y finaliza la estadía")
    void registrarSalidaValida() {
        // 1. Ingreso
        RegistroIngresoDto dto = new RegistroIngresoDto(
                "ABC123",
                TipoVehiculo.AUTOMOVIL,
                "Valentina Rojas",
                "1024567890"
        );
        Estadia estadia = estadiaService.registrarIngreso(dto);
        Celda celda = estadia.getCelda();
        assertEquals(EstadoCelda.OCUPADA, celda.getEstado());

        // 2. Evaluar salida
        Optional<ValidacionSalidaDto> validacionOpt = estadiaService.evaluarSalidaPorPlaca("ABC123");
        assertTrue(validacionOpt.isPresent());
        assertTrue(validacionOpt.get().isPuedeSalir());

        // 3. Confirmar salida
        Estadia salida = estadiaService.confirmarSalida(estadia.getCodigo());

        assertEquals(EstadoEstadia.FINALIZADO, salida.getEstado());
        assertNotNull(salida.getFechaHoraSalida());

        // Verificar que la celda quedó liberada
        Celda celdaActualizada = celdaRepository.findById(celda.getCodigo()).orElseThrow();
        assertEquals(EstadoCelda.DISPONIBLE, celdaActualizada.getEstado());
    }

    @Test
    @DisplayName("HU-09: Salida bloqueada por novedad pendiente de revisión")
    void bloquearSalidaPorNovedadPendiente() {
        RegistroIngresoDto dto = new RegistroIngresoDto(
                "KLM456",
                TipoVehiculo.AUTOMOVIL,
                "Andres Torres",
                "1032456789"
        );
        Estadia estadia = estadiaService.registrarIngreso(dto);

        // Crear una novedad bloqueante
        Novedad novedad = new Novedad(estadia, null, "Daño en carrocería", "Rayón en puerta delantera derecha", true, null);
        novedadRepository.save(novedad);

        // Evaluar salida
        Optional<ValidacionSalidaDto> validacionOpt = estadiaService.evaluarSalidaPorPlaca("KLM456");
        assertTrue(validacionOpt.isPresent());
        assertFalse(validacionOpt.get().isPuedeSalir());
        assertFalse(validacionOpt.get().getMotivosBloqueo().isEmpty());

        // Intentar forzar salida debe lanzar excepción de negocio
        assertThrows(ReglaNegocioException.class, () -> estadiaService.confirmarSalida(estadia.getCodigo()));
    }

    @Test
    @DisplayName("HU-09: Salida bloqueada por pago pendiente")
    void bloquearSalidaPorPagoPendiente() {
        RegistroIngresoDto dto = new RegistroIngresoDto(
                "DEF456",
                TipoVehiculo.AUTOMOVIL,
                "Carlos Sanchez",
                "1011121314"
        );
        Estadia estadia = estadiaService.registrarIngreso(dto);
        estadia.setPagoPendiente(true);
        estadiaRepository.save(estadia);

        Optional<ValidacionSalidaDto> validacionOpt = estadiaService.evaluarSalidaPorPlaca("DEF456");
        assertTrue(validacionOpt.isPresent());
        assertFalse(validacionOpt.get().isPuedeSalir());

        assertThrows(ReglaNegocioException.class, () -> estadiaService.confirmarSalida(estadia.getCodigo()));
    }

    @Test
    @DisplayName("HU-10: Buscar salida para vehículo sin ingreso activo retorna vacío")
    void sinIngresoActivo() {
        Optional<ValidacionSalidaDto> validacionOpt = estadiaService.evaluarSalidaPorPlaca("XYZ999");
        assertTrue(validacionOpt.isEmpty());
    }
}
