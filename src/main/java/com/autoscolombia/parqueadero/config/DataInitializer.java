package com.autoscolombia.parqueadero.config;

import com.autoscolombia.parqueadero.model.*;
import com.autoscolombia.parqueadero.repository.CeldaRepository;
import com.autoscolombia.parqueadero.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final CeldaRepository celdaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CeldaRepository celdaRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.celdaRepository = celdaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Inicializar usuarios base si la tabla está vacía
        if (usuarioRepository.count() == 0) {
            String passHash = passwordEncoder.encode("123456");

            Usuario empleado = new Usuario("1024567890", "Valentina Rojas Méndez", "3001234567", RolUsuario.EMPLEADO);
            empleado.setCorreo("valerojas@autoscolombia.co");
            empleado.setPassword(passHash);

            Usuario admin = new Usuario("1000000001", "Juan Diego Johnson", "3109876543", RolUsuario.ADMINISTRADOR);
            admin.setCorreo("admin@autoscolombia.co");
            admin.setPassword(passHash);

            Usuario clienteEjemplo = new Usuario("1009876543", "Camila Duarte López", "3123456789", RolUsuario.CLIENTE);
            clienteEjemplo.setCorreo("camila.d@gmail.com");
            clienteEjemplo.setPassword(passHash);

            usuarioRepository.save(empleado);
            usuarioRepository.save(admin);
            usuarioRepository.save(clienteEjemplo);
            log.info("DataInitializer: 3 usuarios creados con contraseña '123456'");
        } else {
            // Asegurar que los usuarios existentes en la BD tengan contraseña BCrypt válida
            List<Usuario> existentes = usuarioRepository.findAll();
            for (Usuario u : existentes) {
                boolean needsReset = u.getPassword() == null 
                        || u.getPassword().isBlank()
                        || "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a".equals(u.getPassword())
                        || !passwordEncoder.matches("123456", u.getPassword());

                if (needsReset) {
                    u.setPassword(passwordEncoder.encode("123456"));
                    usuarioRepository.save(u);
                    log.info("DataInitializer: Contraseña establecida/corregida para usuario '{}'", u.getDocumento());
                }
            }
        }

        // 2. Inicializar las 20 celdas si no existen
        if (celdaRepository.count() == 0) {
            for (int i = 1; i <= 12; i++) {
                String numero = String.format("A-%03d", i);
                celdaRepository.save(new Celda(numero, TipoVehiculo.AUTOMOVIL, EstadoCelda.DISPONIBLE));
            }
            for (int i = 1; i <= 8; i++) {
                String numero = String.format("B-%03d", i);
                celdaRepository.save(new Celda(numero, TipoVehiculo.MOTOCICLETA, EstadoCelda.DISPONIBLE));
            }
            log.info("DataInitializer: 20 celdas creadas");
        }
    }
}
