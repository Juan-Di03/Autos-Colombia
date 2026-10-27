package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.dto.RegistroIngresoDto;
import com.autoscolombia.parqueadero.dto.ValidacionSalidaDto;
import com.autoscolombia.parqueadero.exception.ReglaNegocioException;
import com.autoscolombia.parqueadero.model.Estadia;
import com.autoscolombia.parqueadero.model.TipoVehiculo;
import com.autoscolombia.parqueadero.service.CeldaService;
import com.autoscolombia.parqueadero.service.EstadiaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Controller
public class EstadiaController {

    private final EstadiaService estadiaService;
    private final CeldaService celdaService;
    private final DateTimeFormatter formateadorFecha = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "CO"));

    public EstadiaController(EstadiaService estadiaService, CeldaService celdaService) {
        this.estadiaService = estadiaService;
        this.celdaService = celdaService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/ingreso";
    }

    // ===============================================================
    // CICLO 1: REGISTRO DE INGRESO
    // ===============================================================

    @GetMapping("/ingreso")
    public String mostrarFormularioIngreso(@RequestParam(value = "tipo", required = false) TipoVehiculo tipoPreseleccionado,
                                           Model model) {
        if (!model.containsAttribute("dto")) {
            RegistroIngresoDto dto = new RegistroIngresoDto();
            if (tipoPreseleccionado != null) {
                dto.setTipo(tipoPreseleccionado);
            }
            model.addAttribute("dto", dto);
        }

        cargarDatosComunesIngreso(model);
        return "estadias/registro-ingreso";
    }

    @PostMapping("/ingreso")
    public String procesarIngreso(@ModelAttribute("dto") RegistroIngresoDto dto,
                                  BindingResult bindingResult,
                                  Model model) {
        try {
            Estadia estadiaRegistrada = estadiaService.registrarIngreso(dto);
            return "redirect:/ingreso/exito/" + estadiaRegistrada.getCodigo();
        } catch (ReglaNegocioException ex) {
            cargarDatosComunesIngreso(model);
            model.addAttribute("errorTipo", ex.getTipoError());
            model.addAttribute("errorMensaje", ex.getMessage());
            model.addAttribute("dto", dto);
            return "estadias/registro-ingreso";
        } catch (Exception ex) {
            cargarDatosComunesIngreso(model);
            model.addAttribute("errorTipo", "GENERAL");
            model.addAttribute("errorMensaje", "Ocurrió un error inesperado al registrar el ingreso: " + ex.getMessage());
            model.addAttribute("dto", dto);
            return "estadias/registro-ingreso";
        }
    }

    @GetMapping("/ingreso/exito/{codigo}")
    public String mostrarIngresoExitoso(@PathVariable("codigo") Long codigo, Model model) {
        Estadia estadia = estadiaService.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado"));

        model.addAttribute("estadia", estadia);
        model.addAttribute("fechaActual", LocalDate.now().format(formateadorFecha));
        return "estadias/ingreso-exitoso";
    }

    // ===============================================================
    // CICLO 2: REGISTRO DE SALIDA (HU-06 a HU-10)
    // ===============================================================

    @GetMapping("/salida")
    public String mostrarModuloSalida(@RequestParam(value = "placa", required = false) String placa,
                                      Model model) {
        model.addAttribute("fechaActual", LocalDate.now().format(formateadorFecha));
        model.addAttribute("placaBuscada", placa != null ? placa.trim().toUpperCase() : "");

        if (placa != null && !placa.trim().isEmpty()) {
            String placaLimpia = placa.trim().toUpperCase();
            Optional<ValidacionSalidaDto> validacionOpt = estadiaService.evaluarSalidaPorPlaca(placaLimpia);

            if (validacionOpt.isEmpty()) {
                // HU-10: Sin ingreso activo encontrado
                model.addAttribute("noEncontrado", true);
            } else {
                ValidacionSalidaDto validacion = validacionOpt.get();
                model.addAttribute("validacion", validacion);
                model.addAttribute("estadia", validacion.getEstadia());

                if (!validacion.isPuedeSalir()) {
                    // HU-09: Salida bloqueada por novedad o pago pendiente
                    model.addAttribute("bloqueada", true);
                } else {
                    // HU-06: Ingreso activo listo para registrar salida
                    model.addAttribute("listaParaSalida", true);
                }
            }
        }

        return "estadias/registro-salida";
    }

    @PostMapping("/salida/confirmar")
    public String confirmarSalida(@RequestParam("codigoEstadia") Long codigoEstadia,
                                  RedirectAttributes redirectAttributes) {
        try {
            Estadia estadiaFinalizada = estadiaService.confirmarSalida(codigoEstadia);
            return "redirect:/salida/exito/" + estadiaFinalizada.getCodigo();
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("errorMensaje", ex.getMessage());
            return "redirect:/salida";
        }
    }

    @GetMapping("/salida/exito/{codigo}")
    public String mostrarSalidaExitosa(@PathVariable("codigo") Long codigo, Model model) {
        Estadia estadia = estadiaService.buscarPorCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Estadía no encontrada"));

        model.addAttribute("estadia", estadia);
        model.addAttribute("fechaActual", LocalDate.now().format(formateadorFecha));
        return "estadias/salida-exitosa";
    }

    private void cargarDatosComunesIngreso(Model model) {
        model.addAttribute("disponiblesAutos", celdaService.contarDisponiblesPorTipo(TipoVehiculo.AUTOMOVIL));
        model.addAttribute("disponiblesMotos", celdaService.contarDisponiblesPorTipo(TipoVehiculo.MOTOCICLETA));
        model.addAttribute("fechaActual", LocalDate.now().format(formateadorFecha));
    }
}
