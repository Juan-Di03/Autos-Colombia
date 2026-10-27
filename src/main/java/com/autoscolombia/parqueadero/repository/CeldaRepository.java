package com.autoscolombia.parqueadero.repository;

import com.autoscolombia.parqueadero.model.Celda;
import com.autoscolombia.parqueadero.model.EstadoCelda;
import com.autoscolombia.parqueadero.model.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CeldaRepository extends JpaRepository<Celda, Long> {
    Optional<Celda> findByNumero(String numero);

    List<Celda> findByTipoAndEstadoOrderByNumeroAsc(TipoVehiculo tipo, EstadoCelda estado);

    long countByTipoAndEstado(TipoVehiculo tipo, EstadoCelda estado);

    long countByEstado(EstadoCelda estado);

    List<Celda> findAllByOrderByNumeroAsc();

    List<Celda> findByTipoOrderByNumeroAsc(TipoVehiculo tipo);

    List<Celda> findByEstadoOrderByNumeroAsc(EstadoCelda estado);
}
