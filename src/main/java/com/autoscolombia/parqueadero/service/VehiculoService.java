package com.autoscolombia.parqueadero.service;

import com.autoscolombia.parqueadero.exception.ReglaNegocioException;
import com.autoscolombia.parqueadero.model.TipoVehiculo;
import com.autoscolombia.parqueadero.model.Usuario;
import com.autoscolombia.parqueadero.model.Vehiculo;
import com.autoscolombia.parqueadero.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class VehiculoService {

    // Automóvil: 3 letras y 3 números (ej: ABC123)
    private static final Pattern PATRON_AUTOMOVIL = Pattern.compile("^[A-Z]{3}[0-9]{3}$");

    // Motocicleta: 3 letras, 2 números y 1 letra/dígito (ej: ABC12D o ABC123)
    private static final Pattern PATRON_MOTOCICLETA = Pattern.compile("^[A-Z]{3}[0-9]{2}[A-Z0-9]$");

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    /**
     * HU-02: Validar la placa del vehículo según el tipo.
     */
    public void validarPlacaSegunTipo(String placa, TipoVehiculo tipo) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new ReglaNegocioException("PLACA_INVALIDA", "Ingresa la placa del vehículo.");
        }

        String placaLimpia = placa.trim().toUpperCase();

        if (tipo == TipoVehiculo.AUTOMOVIL) {
            if (!PATRON_AUTOMOVIL.matcher(placaLimpia).matches()) {
                throw new ReglaNegocioException("PLACA_INVALIDA",
                        "Para este ejemplo de automóvil se esperan 3 letras y 3 números, como ABC123. " + placaLimpia + " no coincide con el formato.");
            }
        } else if (tipo == TipoVehiculo.MOTOCICLETA) {
            if (!PATRON_MOTOCICLETA.matcher(placaLimpia).matches()) {
                throw new ReglaNegocioException("PLACA_INVALIDA",
                        "Para motocicleta se esperan 3 letras, 2 números y 1 letra (ej: ABC12D). " + placaLimpia + " no coincide con el formato.");
            }
        }
    }

    @Transactional
    public Vehiculo registrarOActualizarVehiculo(String placa, TipoVehiculo tipo, Usuario conductor) {
        String placaLimpia = placa.trim().toUpperCase();
        return vehiculoRepository.findByPlacaIgnoreCase(placaLimpia)
                .map(vehiculoExistente -> {
                    vehiculoExistente.setTipo(tipo);
                    if (vehiculoExistente.getPropietario() == null && conductor != null) {
                        vehiculoExistente.setPropietario(conductor);
                    }
                    return vehiculoRepository.save(vehiculoExistente);
                })
                .orElseGet(() -> {
                    Vehiculo nuevo = new Vehiculo(placaLimpia, tipo, null, conductor);
                    return vehiculoRepository.save(nuevo);
                });
    }

    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        if (placa == null) return Optional.empty();
        return vehiculoRepository.findByPlacaIgnoreCase(placa.trim());
    }
}
