package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.repository.EstadiaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/historial")
public class HistorialController {

    private final EstadiaRepository estadiaRepository;

    public HistorialController(EstadiaRepository estadiaRepository) {
        this.estadiaRepository = estadiaRepository;
    }

    @GetMapping
    public String historial(@RequestParam(required = false) String placa, Model model) {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        model.addAttribute("titulo", "Historial");
        model.addAttribute("seccion", "Administración");
        model.addAttribute("subseccion", "Historial de Estadías");
        model.addAttribute("moduloActivo", "historial");
        model.addAttribute("fechaActual", fecha);
        model.addAttribute("contenido", "historial/historial");
        model.addAttribute("placa", placa != null ? placa : "");

        if (placa != null && !placa.isBlank()) {
            model.addAttribute("estadias", estadiaRepository.findByVehiculoPlacaIgnoreCaseOrderByFechaHoraIngresoDesc(placa.trim()));
        } else {
            model.addAttribute("estadias", estadiaRepository.findAllByOrderByFechaHoraIngresoDesc());
        }
        return "layout/base";
    }
}
