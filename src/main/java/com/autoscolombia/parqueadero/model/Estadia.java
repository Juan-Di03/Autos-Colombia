package com.autoscolombia.parqueadero.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
public class Estadia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "placa_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codigo_celda", nullable = false)
    private Celda celda;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "conductor_id", nullable = false)
    private Usuario conductor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empleado_ingreso_id", nullable = false)
    private Usuario empleadoIngreso;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empleado_salida_id")
    private Usuario empleadoSalida;

    @Column(name = "fecha_hora_ingreso", nullable = false)
    private LocalDateTime fechaHoraIngreso;

    @Column(name = "fecha_hora_salida")
    private LocalDateTime fechaHoraSalida;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoEstadia estado = EstadoEstadia.ACTIVO;

    @Column(name = "pago_pendiente", nullable = false)
    private Boolean pagoPendiente = false;

    public Estadia() {
    }

    public Estadia(Vehiculo vehiculo, Celda celda, Usuario conductor, Usuario empleadoIngreso, LocalDateTime fechaHoraIngreso) {
        this.vehiculo = vehiculo;
        this.celda = celda;
        this.conductor = conductor;
        this.empleadoIngreso = empleadoIngreso;
        this.fechaHoraIngreso = fechaHoraIngreso != null ? fechaHoraIngreso : LocalDateTime.now();
        this.estado = EstadoEstadia.ACTIVO;
        this.pagoPendiente = false;
    }

    public boolean isActiva() {
        return this.estado == EstadoEstadia.ACTIVO;
    }

    public void finalizar(LocalDateTime fechaHoraSalida, Usuario empleadoSalida) {
        this.fechaHoraSalida = fechaHoraSalida != null ? fechaHoraSalida : LocalDateTime.now();
        this.empleadoSalida = empleadoSalida;
        this.estado = EstadoEstadia.FINALIZADO;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Celda getCelda() {
        return celda;
    }

    public void setCelda(Celda celda) {
        this.celda = celda;
    }

    public Usuario getConductor() {
        return conductor;
    }

    public void setConductor(Usuario conductor) {
        this.conductor = conductor;
    }

    public Usuario getEmpleadoIngreso() {
        return empleadoIngreso;
    }

    public void setEmpleadoIngreso(Usuario empleadoIngreso) {
        this.empleadoIngreso = empleadoIngreso;
    }

    public Usuario getEmpleadoSalida() {
        return empleadoSalida;
    }

    public void setEmpleadoSalida(Usuario empleadoSalida) {
        this.empleadoSalida = empleadoSalida;
    }

    public LocalDateTime getFechaHoraIngreso() {
        return fechaHoraIngreso;
    }

    public void setFechaHoraIngreso(LocalDateTime fechaHoraIngreso) {
        this.fechaHoraIngreso = fechaHoraIngreso;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public EstadoEstadia getEstado() {
        return estado;
    }

    public void setEstado(EstadoEstadia estado) {
        this.estado = estado;
    }

    public Boolean getPagoPendiente() {
        return pagoPendiente;
    }

    public void setPagoPendiente(Boolean pagoPendiente) {
        this.pagoPendiente = pagoPendiente;
    }
}
