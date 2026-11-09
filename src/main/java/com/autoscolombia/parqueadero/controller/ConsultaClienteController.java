package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.model.Estadia;
import com.autoscolombia.parqueadero.model.EstadoEstadia;
import com.autoscolombia.parqueadero.repository.EstadiaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
public class ConsultaClienteController {

    private final EstadiaRepository estadiaRepository;

    public ConsultaClienteController(EstadiaRepository estadiaRepository) {
        this.estadiaRepository = estadiaRepository;
    }

    @GetMapping("/consultar-celda")
    public String consultarCelda(@RequestParam(required = false) String placa, Model model) {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        model.addAttribute("titulo", "Consultar Mi Celda");
        model.addAttribute("seccion", "Cliente");
        model.addAttribute("subseccion", "Ubicación de Vehículo");
        model.addAttribute("moduloActivo", "consulta_cliente");
        model.addAttribute("fechaActual", fecha);
        model.addAttribute("contenido", "cliente/consultar-celda");
        model.addAttribute("placa", placa != null ? placa.toUpperCase().trim() : "");
        model.addAttribute("consultado", placa != null && !placa.isBlank());

        if (placa != null && !placa.isBlank()) {
            Optional<Estadia> estadiaActiva = estadiaRepository
                    .findByVehiculoPlacaIgnoreCaseAndEstado(placa.trim(), EstadoEstadia.ACTIVO);
            model.addAttribute("ticketActivo", estadiaActiva.orElse(null));
        } else {
            model.addAttribute("ticketActivo", null);
        }

        return "layout/base";
    }
}
