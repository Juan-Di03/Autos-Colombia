package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.exception.ReglaNegocioException;
import com.autoscolombia.parqueadero.model.EstadoNovedad;
import com.autoscolombia.parqueadero.model.Usuario;
import com.autoscolombia.parqueadero.model.RolUsuario;
import com.autoscolombia.parqueadero.repository.EstadiaRepository;
import com.autoscolombia.parqueadero.service.CeldaService;
import com.autoscolombia.parqueadero.service.NovedadService;
import com.autoscolombia.parqueadero.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/novedades")
public class NovedadController {

    private final NovedadService novedadService;
    private final CeldaService celdaService;
    private final UsuarioService usuarioService;
    private final EstadiaRepository estadiaRepository;

    public NovedadController(NovedadService novedadService,
                             CeldaService celdaService,
                             UsuarioService usuarioService,
                             EstadiaRepository estadiaRepository) {
        this.novedadService = novedadService;
        this.celdaService = celdaService;
        this.usuarioService = usuarioService;
        this.estadiaRepository = estadiaRepository;
    }

    // ── Novedades de Celda ───────────────────────────────────────────
    @GetMapping("/celda")
    public String novedadesCelda(Model model) {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        model.addAttribute("titulo", "Novedades de Celda");
        model.addAttribute("seccion", "Operación");
        model.addAttribute("subseccion", "Novedad de Celda");
        model.addAttribute("moduloActivo", "novedad_celda");
        model.addAttribute("fechaActual", fecha);
        model.addAttribute("contenido", "novedades/novedades-celda");
        model.addAttribute("novedades", novedadService.listarTodas().stream()
                .filter(n -> n.getCelda() != null).toList());
        model.addAttribute("celdas", celdaService.listarTodas());
        model.addAttribute("empleados", usuarioService.listarPorRol(RolUsuario.EMPLEADO));
        model.addAttribute("error", "");
        model.addAttribute("exito", "");
        return "layout/base";
    }

    @PostMapping("/celda/registrar")
    public String registrarNovedadCelda(@RequestParam Long celdaId,
                                        @RequestParam String descripcion,
                                        @RequestParam(required = false) String tipoDano,
                                        @RequestParam Long empleadoId,
                                        RedirectAttributes flash) {
        try {
            Usuario empleado = usuarioService.buscarPorId(empleadoId)
                    .orElseThrow(() -> new ReglaNegocioException("EMP_NO_ENCONTRADO", "Empleado no encontrado."));
            novedadService.registrarNovedadCelda(celdaId, descripcion, tipoDano, empleado);
            flash.addFlashAttribute("exito", "Novedad de celda registrada correctamente.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("errorGlobal", e.getMensaje());
        }
        return "redirect:/novedades/celda";
    }

    @PostMapping("/celda/{id}/resolver")
    public String resolverNovedadCelda(@PathVariable Long id, RedirectAttributes flash) {
        try {
            novedadService.resolver(id);
            flash.addFlashAttribute("exito", "Novedad marcada como resuelta.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("errorGlobal", e.getMensaje());
        }
        return "redirect:/novedades/celda";
    }

    // ── Novedades de Vehículo ────────────────────────────────────────
    @GetMapping("/vehiculo")
    public String novedadesVehiculo(@RequestParam(required = false) String placa, Model model) {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        model.addAttribute("titulo", "Novedades de Vehículo");
        model.addAttribute("seccion", "Administración");
        model.addAttribute("subseccion", "Novedades de Vehículo");
        model.addAttribute("moduloActivo", "novedad_vehiculo");
        model.addAttribute("fechaActual", fecha);
        model.addAttribute("contenido", "novedades/novedades-vehiculo");
        model.addAttribute("placa", placa != null ? placa : "");

        if (placa != null && !placa.isBlank()) {
            model.addAttribute("novedades", novedadService.listarPorPlacaVehiculo(placa));
        } else {
            model.addAttribute("novedades", novedadService.listarTodas().stream()
                    .filter(n -> n.getEstadia() != null).toList());
        }
        model.addAttribute("empleados", usuarioService.listarPorRol(RolUsuario.EMPLEADO));
        model.addAttribute("estadiasActivas", estadiaRepository.findAll().stream()
                .filter(e -> e.getEstado().name().equals("ACTIVO")).toList());
        return "layout/base";
    }

    @PostMapping("/vehiculo/{id}/resolver")
    public String resolverNovedadVehiculo(@PathVariable Long id, RedirectAttributes flash) {
        try {
            novedadService.resolver(id);
            flash.addFlashAttribute("exito", "Novedad marcada como resuelta.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("errorGlobal", e.getMensaje());
        }
        return "redirect:/novedades/vehiculo";
    }
}
