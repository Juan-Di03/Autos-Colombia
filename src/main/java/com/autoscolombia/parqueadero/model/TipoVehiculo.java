package com.autoscolombia.parqueadero.model;

public enum TipoVehiculo {
    AUTOMOVIL("Automóvil", "Zona A"),
    MOTOCICLETA("Motocicleta", "Zona B");

    private final String nombre;
    private final String zona;

    TipoVehiculo(String nombre, String zona) {
        this.nombre = nombre;
        this.zona = zona;
    }

    public String getNombre() {
        return nombre;
    }

    public String getZona() {
        return zona;
    }
}
