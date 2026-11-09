package com.autoscolombia.parqueadero.service;

import com.autoscolombia.parqueadero.exception.ReglaNegocioException;
import com.autoscolombia.parqueadero.model.*;
import com.autoscolombia.parqueadero.repository.CeldaRepository;
import com.autoscolombia.parqueadero.repository.EstadiaRepository;
import com.autoscolombia.parqueadero.repository.NovedadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NovedadService {

    private final NovedadRepository novedadRepository;
    private final EstadiaRepository estadiaRepository;
    private final CeldaRepository celdaRepository;

    public NovedadService(NovedadRepository novedadRepository,
                          EstadiaRepository estadiaRepository,
                          CeldaRepository celdaRepository) {
        this.novedadRepository = novedadRepository;
        this.estadiaRepository = estadiaRepository;
        this.celdaRepository = celdaRepository;
    }

    public List<Novedad> listarTodas() {
        return novedadRepository.findAllByOrderByFechaDesc();
    }

    public List<Novedad> listarPorEstado(EstadoNovedad estado) {
        return novedadRepository.findByEstadoOrderByFechaDesc(estado);
    }

    public List<Novedad> listarPorCelda(Long celdaId) {
        return novedadRepository.findByCeldaCodigoOrderByFechaDesc(celdaId);
    }

    public List<Novedad> listarPorPlacaVehiculo(String placa) {
        return novedadRepository.findByEstadiaVehiculoPlacaIgnoreCaseOrderByFechaDesc(placa);
    }

    public Optional<Novedad> buscarPorId(Long id) {
        return novedadRepository.findById(id);
    }

    /** Registra una novedad sobre una celda (sin ticket asociado) */
    @Transactional
    public Novedad registrarNovedadCelda(Long celdaId, String descripcion,
                                          String tipoDano, Usuario empleado) {
        Celda celda = celdaRepository.findById(celdaId)
                .orElseThrow(() -> new ReglaNegocioException("CELDA_NO_ENCONTRADA",
                        "La celda con ID " + celdaId + " no existe."));

        Novedad novedad = new Novedad(null, celda, tipoDano, descripcion, false, empleado);
        return novedadRepository.save(novedad);
    }

    /** Registra una novedad sobre un ticket/vehículo */
    @Transactional
    public Novedad registrarNovedadVehiculo(Long estadiaId, String descripcion,
                                             String tipoDano, boolean bloqueaSalida,
                                             Usuario empleado) {
        Estadia estadia = estadiaRepository.findById(estadiaId)
                .orElseThrow(() -> new ReglaNegocioException("ESTADIA_NO_ENCONTRADA",
                        "El ticket con ID " + estadiaId + " no existe."));

        Novedad novedad = new Novedad(estadia, null, tipoDano, descripcion, bloqueaSalida, empleado);
        return novedadRepository.save(novedad);
    }

    /** Marca una novedad como RESUELTA */
    @Transactional
    public Novedad resolver(Long novedadId) {
        Novedad novedad = novedadRepository.findById(novedadId)
                .orElseThrow(() -> new ReglaNegocioException("NO_ENCONTRADA",
                        "Novedad con ID " + novedadId + " no encontrada."));
        novedad.setEstado(EstadoNovedad.RESUELTA);
        return novedadRepository.save(novedad);
    }
}
