package com.autoscolombia.parqueadero.model;

public enum EstadoEstadia {
    ACTIVO("Ingreso activo"),
    FINALIZADO("Finalizada");

    private final String etiqueta;

    EstadoEstadia(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
