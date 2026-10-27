package com.autoscolombia.parqueadero.model;

import jakarta.persistence.*;

@Entity
@Table(name = "celda")
public class Celda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Column(name = "numero", nullable = false, unique = true, length = 15)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoVehiculo tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 25)
    private EstadoCelda estado = EstadoCelda.DISPONIBLE;

    public Celda() {
    }

    public Celda(String numero, TipoVehiculo tipo) {
        this.numero = numero;
        this.tipo = tipo;
        this.estado = EstadoCelda.DISPONIBLE;
    }

    public Celda(String numero, TipoVehiculo tipo, EstadoCelda estado) {
        this.numero = numero;
        this.tipo = tipo;
        this.estado = estado;
    }

    public boolean isDisponible() {
        return this.estado == EstadoCelda.DISPONIBLE;
    }

    public boolean isCompatibleCon(TipoVehiculo tipoVehiculo) {
        return this.tipo == tipoVehiculo;
    }

    public void marcarOcupada() {
        this.estado = EstadoCelda.OCUPADA;
    }

    public void marcarDisponible() {
        this.estado = EstadoCelda.DISPONIBLE;
    }

    public void marcarFueraDeServicio() {
        this.estado = EstadoCelda.FUERA_DE_SERVICIO;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVehiculo tipo) {
        this.tipo = tipo;
    }

    public EstadoCelda getEstado() {
        return estado;
    }

    public void setEstado(EstadoCelda estado) {
        this.estado = estado;
    }
}
