package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.exception.ReglaNegocioException;
import com.autoscolombia.parqueadero.model.EstadoCelda;
import com.autoscolombia.parqueadero.model.TipoVehiculo;
import com.autoscolombia.parqueadero.service.CeldaService;
import com.autoscolombia.parqueadero.service.NovedadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/celdas")
public class CeldaController {

    private final CeldaService celdaService;
    private final NovedadService novedadService;

    public CeldaController(CeldaService celdaService, NovedadService novedadService) {
        this.celdaService = celdaService;
        this.novedadService = novedadService;
    }

    // ── Tablero principal ────────────────────────────────────────────
    @GetMapping
    public String tablero(Model model) {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

        model.addAttribute("titulo", "Tablero de Celdas");
        model.addAttribute("seccion", "Administración");
        model.addAttribute("subseccion", "Estado de Celdas");
        model.addAttribute("moduloActivo", "celdas");
        model.addAttribute("fechaActual", fecha);
        model.addAttribute("contenido", "celdas/tablero-celdas");

        model.addAttribute("celdasAuto",  celdaService.listarPorTipo(TipoVehiculo.AUTOMOVIL));
        model.addAttribute("celdasMoto",  celdaService.listarPorTipo(TipoVehiculo.MOTOCICLETA));
        model.addAttribute("estadosCelda", EstadoCelda.values());

        // Contadores resumen
        model.addAttribute("totalCeldas",          celdaService.contarTotalCeldas());
        model.addAttribute("disponiblesAuto",       celdaService.contarDisponiblesPorTipo(TipoVehiculo.AUTOMOVIL));
        model.addAttribute("disponiblesMoto",       celdaService.contarDisponiblesPorTipo(TipoVehiculo.MOTOCICLETA));
        model.addAttribute("totalFueraServicio",    celdaService.contarPorEstado(EstadoCelda.FUERA_DE_SERVICIO));
        model.addAttribute("totalOcupadas",         celdaService.contarPorEstado(EstadoCelda.OCUPADA));
        model.addAttribute("totalDisponibles",      celdaService.contarPorEstado(EstadoCelda.DISPONIBLE));
        return "layout/base";
    }

    // ── Cambiar estado de una celda ──────────────────────────────────
    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam EstadoCelda nuevoEstado,
                                RedirectAttributes flash) {
        try {
            celdaService.actualizarEstado(id, nuevoEstado);
            flash.addFlashAttribute("exito", "Estado de celda actualizado correctamente.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("errorGlobal", e.getMensaje());
        }
        return "redirect:/celdas";
    }
    
    // Crear una nueva celda
    
    @PostMapping("/celdas")
    public String crearCelda(@RequestParam String numero,
                         @RequestParam TipoVehiculo tipo,
                         RedirectAttributes redirectAttributes) {
    try {
        Celda celda = celdaService.crear(numero, tipo);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Celda " + celda.getNumero() + " creada correctamente.");
    } catch (ReglaNegocioException ex) {
        redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
    }
    return "redirect:/celdas";
}
}
