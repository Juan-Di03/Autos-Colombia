package com.autoscolombia.parqueadero.repository;

import com.autoscolombia.parqueadero.model.Estadia;
import com.autoscolombia.parqueadero.model.EstadoEstadia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstadiaRepository extends JpaRepository<Estadia, Long> {

    Optional<Estadia> findByVehiculoPlacaIgnoreCaseAndEstado(String placa, EstadoEstadia estado);

    boolean existsByVehiculoPlacaIgnoreCaseAndEstado(String placa, EstadoEstadia estado);

    Optional<Estadia> findFirstByCeldaCodigoAndEstado(Long celdaCodigo, EstadoEstadia estado);

    List<Estadia> findAllByOrderByFechaHoraIngresoDesc();

    List<Estadia> findByVehiculoPlacaIgnoreCaseOrderByFechaHoraIngresoDesc(String placa);

    @Query("SELECT e FROM Estadia e WHERE " +
           "(:placa IS NULL OR LOWER(e.vehiculo.placa) LIKE LOWER(CONCAT('%', :placa, '%'))) AND " +
           "(:fechaInicio IS NULL OR e.fechaHoraIngreso >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR e.fechaHoraIngreso <= :fechaFin) " +
           "ORDER BY e.fechaHoraIngreso DESC")
    List<Estadia> filtrarHistorial(
            @Param("placa") String placa,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}
