package com.autoscolombia.parqueadero.service;

import com.autoscolombia.parqueadero.exception.ReglaNegocioException;
import com.autoscolombia.parqueadero.model.Celda;
import com.autoscolombia.parqueadero.model.EstadoCelda;
import com.autoscolombia.parqueadero.model.TipoVehiculo;
import com.autoscolombia.parqueadero.repository.CeldaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CeldaService {

    private final CeldaRepository celdaRepository;

    public CeldaService(CeldaRepository celdaRepository) {
        this.celdaRepository = celdaRepository;
    }

    /**
     * HU-04 y HU-05: Asigna automáticamente la primera celda compatible y disponible.
     * Si no hay celdas disponibles, lanza excepción de negocio.
     */
    @Transactional
    public Celda asignarCeldaDisponible(TipoVehiculo tipo) {
        List<Celda> disponibles = celdaRepository.findByTipoAndEstadoOrderByNumeroAsc(tipo, EstadoCelda.DISPONIBLE);

        if (disponibles.isEmpty()) {
            throw new ReglaNegocioException("SIN_CUPOS",
                    "No hay celdas disponibles compatibles para " + tipo.getNombre() + ". No es posible confirmar sin una ubicación válida.");
        }

        Celda celdaAsignada = disponibles.get(0);
        celdaAsignada.marcarOcupada();
        return celdaRepository.save(celdaAsignada);
    }

    @Transactional
    public void liberarCelda(Celda celda) {
        if (celda != null) {
            celda.marcarDisponible();
            celdaRepository.save(celda);
        }
    }

    public long contarDisponiblesPorTipo(TipoVehiculo tipo) {
        return celdaRepository.countByTipoAndEstado(tipo, EstadoCelda.DISPONIBLE);
    }

    public long contarPorEstado(EstadoCelda estado) {
        return celdaRepository.countByEstado(estado);
    }

    public long contarTotalCeldas() {
        return celdaRepository.count();
    }

    public List<Celda> listarTodas() {
        return celdaRepository.findAllByOrderByNumeroAsc();
    }

    public List<Celda> listarPorTipo(TipoVehiculo tipo) {
        return celdaRepository.findByTipoOrderByNumeroAsc(tipo);
    }

    public Optional<Celda> buscarPorCodigo(Long codigo) {
        return celdaRepository.findById(codigo);
    }

    public Optional<Celda> buscarPorNumero(String numero) {
        return celdaRepository.findByNumero(numero);
    }

    @Transactional
    public Celda actualizarEstado(Long codigoCelda, EstadoCelda nuevoEstado) {
        Celda celda = celdaRepository.findById(codigoCelda)
                .orElseThrow(() -> new ReglaNegocioException("La celda con código " + codigoCelda + " no existe."));
        celda.setEstado(nuevoEstado);
        return celdaRepository.save(celda);
    }
}
