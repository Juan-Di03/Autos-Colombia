package com.autoscolombia.parqueadero.service;

import com.autoscolombia.parqueadero.dto.RegistroIngresoDto;
import com.autoscolombia.parqueadero.exception.ReglaNegocioException;
import com.autoscolombia.parqueadero.model.*;
import com.autoscolombia.parqueadero.repository.CeldaRepository;
import com.autoscolombia.parqueadero.repository.EstadiaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EstadiaServiceTest {

    @Autowired
    private EstadiaService estadiaService;

    @Autowired
    private CeldaRepository celdaRepository;

    @Autowired
    private EstadiaRepository estadiaRepository;

    @Test
    @DisplayName("HU-01, HU-03, HU-04: Registrar ingreso válido asigna celda compatible y fecha/hora")
    void registrarIngresoValido() {
        RegistroIngresoDto dto = new RegistroIngresoDto(
                "ABC123",
                TipoVehiculo.AUTOMOVIL,
                "Valentina Rojas Méndez",
                "1024567890"
        );

        Estadia estadia = estadiaService.registrarIngreso(dto);

        assertNotNull(estadia.getCodigo());
        assertEquals("ABC123", estadia.getVehiculo().getPlaca());
        assertEquals(TipoVehiculo.AUTOMOVIL, estadia.getVehiculo().getTipo());
        assertEquals("Valentina Rojas Méndez", estadia.getConductor().getNombre());
        assertEquals(EstadoEstadia.ACTIVO, estadia.getEstado());
        assertNotNull(estadia.getFechaHoraIngreso());
        assertNotNull(estadia.getCelda());
        assertEquals(EstadoCelda.OCUPADA, estadia.getCelda().getEstado());
        assertTrue(estadia.getCelda().getNumero().startsWith("A-"));
    }

    @Test
    @DisplayName("HU-02: Placa con formato inválido para automóvil debe ser rechazada")
    void rechazarPlacaInvalidaAutomovil() {
        RegistroIngresoDto dto = new RegistroIngresoDto(
                "AB12", // Placa incompleta
                TipoVehiculo.AUTOMOVIL,
                "Valentina Rojas",
                "1024567890"
        );

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> estadiaService.registrarIngreso(dto));
        assertEquals("PLACA_INVALIDA", ex.getTipoError());
    }

    @Test
    @DisplayName("RF-19: Impedir más de un ingreso activo para el mismo vehículo")
    void impedirIngresoDuplicadoActivo() {
        RegistroIngresoDto dto1 = new RegistroIngresoDto(
                "XYZ789",
                TipoVehiculo.AUTOMOVIL,
                "Pedro Gomez",
                "987654321"
        );
        estadiaService.registrarIngreso(dto1);

        RegistroIngresoDto dto2 = new RegistroIngresoDto(
                "XYZ789",
                TipoVehiculo.AUTOMOVIL,
                "Pedro Gomez",
                "987654321"
        );

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> estadiaService.registrarIngreso(dto2));
        assertEquals("INGRESO_ACTIVO", ex.getTipoError());
    }

    @Test
    @DisplayName("HU-05: Sin cupos disponibles debe rechazar el ingreso")
    void rechazarIngresoSinCeldasDisponibles() {
        // Ocupar artificialmente todas las celdas de moto
        celdaRepository.findByTipoAndEstadoOrderByNumeroAsc(TipoVehiculo.MOTOCICLETA, EstadoCelda.DISPONIBLE)
                .forEach(c -> {
                    c.marcarOcupada();
                    celdaRepository.save(c);
                });

        RegistroIngresoDto dto = new RegistroIngresoDto(
                "MOT12A",
                TipoVehiculo.MOTOCICLETA,
                "Camila Duarte",
                "1009876543"
        );

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> estadiaService.registrarIngreso(dto));
        assertEquals("SIN_CUPOS", ex.getTipoError());
    }
}
