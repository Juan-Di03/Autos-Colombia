package com.autoscolombia.parqueadero.model;

public enum EstadoNovedad {
    PENDIENTE("Pendiente de revisión"),
    RESUELTA("Cerrada");

    private final String etiqueta;

    EstadoNovedad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
