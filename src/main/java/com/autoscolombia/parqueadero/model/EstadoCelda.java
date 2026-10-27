package com.autoscolombia.parqueadero.model;

public enum EstadoCelda {
    DISPONIBLE("Disponible"),
    OCUPADA("Ocupada"),
    FUERA_DE_SERVICIO("Fuera de servicio");

    private final String etiqueta;

    EstadoCelda(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
