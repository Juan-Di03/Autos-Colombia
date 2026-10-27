package com.autoscolombia.parqueadero.dto;

import com.autoscolombia.parqueadero.model.TipoVehiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegistroIngresoDto {

    @NotBlank(message = "Ingresa la placa del vehículo.")
    private String placa;

    @NotNull(message = "Selecciona un tipo de vehículo.")
    private TipoVehiculo tipo;

    @NotBlank(message = "Ingresa el nombre completo.")
    private String nombreConductor;

    @NotBlank(message = "Ingresa el documento del conductor.")
    private String documentoConductor;

    private String telefonoConductor;

    public RegistroIngresoDto() {
    }

    public RegistroIngresoDto(String placa, TipoVehiculo tipo, String nombreConductor, String documentoConductor) {
        this.placa = placa;
        this.tipo = tipo;
        this.nombreConductor = nombreConductor;
        this.documentoConductor = documentoConductor;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa != null ? placa.trim().toUpperCase() : null;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVehiculo tipo) {
        this.tipo = tipo;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    public String getDocumentoConductor() {
        return documentoConductor;
    }

    public void setDocumentoConductor(String documentoConductor) {
        this.documentoConductor = documentoConductor;
    }

    public String getTelefonoConductor() {
        return telefonoConductor;
    }

    public void setTelefonoConductor(String telefonoConductor) {
        this.telefonoConductor = telefonoConductor;
    }
}
