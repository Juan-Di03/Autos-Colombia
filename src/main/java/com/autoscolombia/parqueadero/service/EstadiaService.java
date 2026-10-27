package com.autoscolombia.parqueadero.service;

import com.autoscolombia.parqueadero.dto.RegistroIngresoDto;
import com.autoscolombia.parqueadero.dto.ValidacionSalidaDto;
import com.autoscolombia.parqueadero.exception.ReglaNegocioException;
import com.autoscolombia.parqueadero.model.*;
import com.autoscolombia.parqueadero.repository.EstadiaRepository;
import com.autoscolombia.parqueadero.repository.NovedadRepository;
import com.autoscolombia.parqueadero.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EstadiaService {

    private final EstadiaRepository estadiaRepository;
    private final VehiculoService vehiculoService;
    private final CeldaService celdaService;
    private final UsuarioRepository usuarioRepository;
    private final NovedadRepository novedadRepository;

    public EstadiaService(EstadiaRepository estadiaRepository,
                          VehiculoService vehiculoService,
                          CeldaService celdaService,
                          UsuarioRepository usuarioRepository,
                          NovedadRepository novedadRepository) {
        this.estadiaRepository = estadiaRepository;
        this.vehiculoService = vehiculoService;
        this.celdaService = celdaService;
        this.usuarioRepository = usuarioRepository;
        this.novedadRepository = novedadRepository;
    }

    /**
     * Ciclo 1: HU-01 a HU-05
     * Registro de ingreso de vehículo de forma atómica y consistente.
     */
    @Transactional
    public Estadia registrarIngreso(RegistroIngresoDto dto) {
        if (dto == null) {
            throw new ReglaNegocioException("DATOS_FALTANTES", "Información de ingreso no proporcionada.");
        }
        if (dto.getTipo() == null) {
            throw new ReglaNegocioException("TIPO_REQUERIDO", "Selecciona el tipo de vehículo.");
        }
        if (dto.getPlaca() == null || dto.getPlaca().trim().isEmpty()) {
            throw new ReglaNegocioException("PLACA_REQUERIDA", "Ingresa la placa del vehículo.");
        }
        if (dto.getNombreConductor() == null || dto.getNombreConductor().trim().isEmpty()) {
            throw new ReglaNegocioException("CONDUCTOR_REQUERIDO", "Ingresa el nombre completo del conductor.");
        }
        if (dto.getDocumentoConductor() == null || dto.getDocumentoConductor().trim().isEmpty()) {
            throw new ReglaNegocioException("DOCUMENTO_REQUERIDO", "Ingresa el documento del conductor.");
        }

        String placaLimpia = dto.getPlaca().trim().toUpperCase();

        // HU-02: Validar formato de la placa según el tipo de vehículo
        vehiculoService.validarPlacaSegunTipo(placaLimpia, dto.getTipo());

        // RF-19: Impedir más de una estadía activa simultánea para el mismo vehículo
        if (estadiaRepository.existsByVehiculoPlacaIgnoreCaseAndEstado(placaLimpia, EstadoEstadia.ACTIVO)) {
            throw new ReglaNegocioException("INGRESO_ACTIVO",
                    "El vehículo con placa " + placaLimpia + " ya tiene un ingreso activo en el parqueadero.");
        }

        // HU-04 y HU-05: Validar disponibilidad y asignar celda compatible automáticamente
        Celda celdaAsignada = celdaService.asignarCeldaDisponible(dto.getTipo());

        // Conductor
        Usuario conductor = usuarioRepository.findByDocumento(dto.getDocumentoConductor().trim())
                .map(existente -> {
                    existente.setNombre(dto.getNombreConductor().trim());
                    if (dto.getTelefonoConductor() != null) {
                        existente.setTelefono(dto.getTelefonoConductor().trim());
                    }
                    return usuarioRepository.save(existente);
                })
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario(
                            dto.getDocumentoConductor().trim(),
                            dto.getNombreConductor().trim(),
                            dto.getTelefonoConductor() != null ? dto.getTelefonoConductor().trim() : null,
                            RolUsuario.CLIENTE
                    );
                    return usuarioRepository.save(nuevo);
                });

        // Vehículo
        Vehiculo vehiculo = vehiculoService.registrarOActualizarVehiculo(placaLimpia, dto.getTipo(), conductor);

        // Empleado de ingreso
        Usuario empleado = usuarioRepository.findFirstByRol(RolUsuario.EMPLEADO)
                .orElseGet(() -> usuarioRepository.findAll().stream().findFirst()
                        .orElseGet(() -> usuarioRepository.save(new Usuario("0", "Sistema", null, RolUsuario.EMPLEADO))));

        // HU-03: Registrar fecha y hora automática
        LocalDateTime ahora = LocalDateTime.now();

        Estadia estadia = new Estadia(vehiculo, celdaAsignada, conductor, empleado, ahora);
        return estadiaRepository.save(estadia);
    }

    /**
     * Ciclo 2: HU-06, HU-09, HU-10
     * Evalúa si una estadía activa existe y si cumple las condiciones para salida.
     */
    public Optional<ValidacionSalidaDto> evaluarSalidaPorPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return Optional.empty();
        }

        String placaLimpia = placa.trim().toUpperCase();
        Optional<Estadia> estadiaOpt = estadiaRepository.findByVehiculoPlacaIgnoreCaseAndEstado(placaLimpia, EstadoEstadia.ACTIVO);

        if (estadiaOpt.isEmpty()) {
            return Optional.empty();
        }

        Estadia estadia = estadiaOpt.get();
        ValidacionSalidaDto validacion = new ValidacionSalidaDto(estadia);

        // Validar novedades pendientes que bloqueen salida (HU-09)
        List<Novedad> novedadesBloqueantes = novedadRepository
                .findByEstadiaCodigoAndBloqueaSalidaTrueAndEstado(estadia.getCodigo(), EstadoNovedad.PENDIENTE);
        validacion.setNovedadesBloqueantes(novedadesBloqueantes);

        if (!novedadesBloqueantes.isEmpty()) {
            for (Novedad n : novedadesBloqueantes) {
                validacion.agregarMotivoBloqueo("Novedad pendiente: " + (n.getTipoDano() != null ? n.getTipoDano() + " - " : "") + n.getDescripcion());
            }
        }

        // Validar si tiene pagos pendientes (HU-09)
        if (Boolean.TRUE.equals(estadia.getPagoPendiente())) {
            validacion.setPagoPendiente(true);
            validacion.agregarMotivoBloqueo("Pago pendiente: El estado de pago de esta estadía está pendiente.");
        }

        return Optional.of(validacion);
    }

    /**
     * Ciclo 2: HU-06, HU-07, HU-08
     * Registra la salida, finaliza la estadía y libera la celda en una sola transacción atómica.
     */
    @Transactional
    public Estadia confirmarSalida(Long codigoEstadia) {
        Estadia estadia = estadiaRepository.findById(codigoEstadia)
                .orElseThrow(() -> new ReglaNegocioException("La estadía con código " + codigoEstadia + " no existe."));

        if (!estadia.isActiva()) {
            throw new ReglaNegocioException("La estadía ya fue finalizada anteriormente.");
        }

        // Validar que no tenga novedades pendientes que bloqueen la salida
        boolean tieneNovedadesBloqueantes = novedadRepository
                .existsByEstadiaCodigoAndBloqueaSalidaTrueAndEstado(estadia.getCodigo(), EstadoNovedad.PENDIENTE);
        if (tieneNovedadesBloqueantes) {
            throw new ReglaNegocioException("SALIDA_BLOQUEADA", "No se puede registrar la salida porque existen novedades pendientes de revisión.");
        }

        // Validar pago
        if (Boolean.TRUE.equals(estadia.getPagoPendiente())) {
            throw new ReglaNegocioException("PAGO_PENDIENTE", "No se puede registrar la salida porque presenta un pago pendiente.");
        }

        Usuario empleadoSalida = usuarioRepository.findFirstByRol(RolUsuario.EMPLEADO)
                .orElseGet(() -> usuarioRepository.findAll().stream().findFirst()
                        .orElse(null));

        // HU-07: Registrar fecha y hora de salida automáticamente
        estadia.finalizar(LocalDateTime.now(), empleadoSalida);

        // HU-08: Liberar automáticamente la celda ocupada
        celdaService.liberarCelda(estadia.getCelda());

        return estadiaRepository.save(estadia);
    }

    public Optional<Estadia> buscarPorCodigo(Long codigo) {
        return estadiaRepository.findById(codigo);
    }

    public Optional<Estadia> buscarEstadiaActivaPorPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return Optional.empty();
        }
        return estadiaRepository.findByVehiculoPlacaIgnoreCaseAndEstado(placa.trim().toUpperCase(), EstadoEstadia.ACTIVO);
    }

    public List<Estadia> listarTodas() {
        return estadiaRepository.findAllByOrderByFechaHoraIngresoDesc();
    }
}
