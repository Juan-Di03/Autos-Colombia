package com.autoscolombia.parqueadero.config;

import com.autoscolombia.parqueadero.model.*;
import com.autoscolombia.parqueadero.repository.CeldaRepository;
import com.autoscolombia.parqueadero.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final CeldaRepository celdaRepository;
    private final UsuarioRepository usuarioRepository;

    public DataInitializer(CeldaRepository celdaRepository, UsuarioRepository usuarioRepository) {
        this.celdaRepository = celdaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        // 1. Inicializar Usuarios base si no existen
        if (usuarioRepository.count() == 0) {
            Usuario empleado = new Usuario("1024567890", "Valentina Rojas Méndez", "3001234567", RolUsuario.EMPLEADO);
            Usuario admin = new Usuario("1000000001", "Esteban Montoya Molina", "3109876543", RolUsuario.ADMINISTRADOR);
            Usuario clienteEjemplo = new Usuario("1009876543", "Camila Duarte López", "3123456789", RolUsuario.CLIENTE);

            usuarioRepository.save(empleado);
            usuarioRepository.save(admin);
            usuarioRepository.save(clienteEjemplo);
        }

        // 2. Inicializar las 20 celdas según los mockups de Figma si no existen
        if (celdaRepository.count() == 0) {
            // Zona A - Automóviles (12 celdas: A-001 a A-012)
            for (int i = 1; i <= 12; i++) {
                String numero = String.format("A-%03d", i);
                celdaRepository.save(new Celda(numero, TipoVehiculo.AUTOMOVIL, EstadoCelda.DISPONIBLE));
            }

            // Zona B - Motocicletas (8 celdas: B-001 a B-008)
            for (int i = 1; i <= 8; i++) {
                String numero = String.format("B-%03d", i);
                celdaRepository.save(new Celda(numero, TipoVehiculo.MOTOCICLETA, EstadoCelda.DISPONIBLE));
            }
        }
    }
}
