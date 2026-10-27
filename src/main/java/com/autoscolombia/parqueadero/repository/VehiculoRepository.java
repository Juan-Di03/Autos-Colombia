package com.autoscolombia.parqueadero.repository;

import com.autoscolombia.parqueadero.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, String> {
    Optional<Vehiculo> findByPlacaIgnoreCase(String placa);
    boolean existsByPlacaIgnoreCase(String placa);
}
