package com.autoscolombia.parqueadero.model;

public enum RolUsuario {
    ADMINISTRADOR("Administrador"),
    EMPLEADO("Empleado"),
    CLIENTE("Cliente");

    private final String descripcion;

    RolUsuario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
