package com.autoscolombia.parqueadero.repository;

import com.autoscolombia.parqueadero.model.EstadoNovedad;
import com.autoscolombia.parqueadero.model.Novedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NovedadRepository extends JpaRepository<Novedad, Long> {

    List<Novedad> findByEstadiaCodigoOrderByFechaDesc(Long estadiaCodigo);

    List<Novedad> findByCeldaCodigoOrderByFechaDesc(Long celdaCodigo);

    List<Novedad> findByEstadiaVehiculoPlacaIgnoreCaseOrderByFechaDesc(String placa);

    boolean existsByEstadiaCodigoAndBloqueaSalidaTrueAndEstado(Long estadiaCodigo, EstadoNovedad estado);

    List<Novedad> findByEstadiaCodigoAndBloqueaSalidaTrueAndEstado(Long estadiaCodigo, EstadoNovedad estado);

    List<Novedad> findByEstadoOrderByFechaDesc(EstadoNovedad estado);

    List<Novedad> findAllByOrderByFechaDesc();
}
