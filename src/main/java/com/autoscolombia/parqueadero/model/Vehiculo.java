package com.autoscolombia.parqueadero.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @Column(name = "placa", nullable = false, length = 10)
    private String placa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoVehiculo tipo;

    @Column(name = "modelo", length = 40)
    private String modelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id")
    private Usuario propietario;

    public Vehiculo() {
    }

    public Vehiculo(String placa, TipoVehiculo tipo, String modelo, Usuario propietario) {
        this.placa = placa != null ? placa.trim().toUpperCase() : null;
        this.tipo = tipo;
        this.modelo = modelo;
        this.propietario = propietario;
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

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }
}
