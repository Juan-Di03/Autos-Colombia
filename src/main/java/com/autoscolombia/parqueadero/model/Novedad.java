package com.autoscolombia.parqueadero.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "novedad")
public class Novedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Estadia estadia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "celda_id")
    private Celda celda;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;

    @Column(name = "tipo_dano", length = 80)
    private String tipoDano;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 25)
    private EstadoNovedad estado = EstadoNovedad.PENDIENTE;

    @Column(name = "bloquea_salida", nullable = false)
    private Boolean bloqueaSalida = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id")
    private Usuario empleado;

    public Novedad() {
    }

    public Novedad(Estadia estadia, Celda celda, String tipoDano, String descripcion, Boolean bloqueaSalida, Usuario empleado) {
        this.estadia = estadia;
        this.celda = celda;
        this.tipoDano = tipoDano;
        this.descripcion = descripcion;
        this.bloqueaSalida = bloqueaSalida != null ? bloqueaSalida : false;
        this.empleado = empleado;
        this.fecha = LocalDateTime.now();
        this.estado = EstadoNovedad.PENDIENTE;
    }

    public boolean isPendiente() {
        return this.estado == EstadoNovedad.PENDIENTE;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Estadia getEstadia() {
        return estadia;
    }

    public void setEstadia(Estadia estadia) {
        this.estadia = estadia;
    }

    public Celda getCelda() {
        return celda;
    }

    public void setCelda(Celda celda) {
        this.celda = celda;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoDano() {
        return tipoDano;
    }

    public void setTipoDano(String tipoDano) {
        this.tipoDano = tipoDano;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public EstadoNovedad getEstado() {
        return estado;
    }

    public void setEstado(EstadoNovedad estado) {
        this.estado = estado;
    }

    public Boolean getBloqueaSalida() {
        return bloqueaSalida;
    }

    public void setBloqueaSalida(Boolean bloqueaSalida) {
        this.bloqueaSalida = bloqueaSalida;
    }

    public Usuario getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Usuario empleado) {
        this.empleado = empleado;
    }
}
