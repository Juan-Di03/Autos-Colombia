package com.autoscolombia.parqueadero.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Column(name = "documento", length = 30)
    private String documento;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "telefono", length = 25)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private RolUsuario rol = RolUsuario.CLIENTE;

    public Usuario() {
    }

    public Usuario(String documento, String nombre, String telefono, RolUsuario rol) {
        this.documento = documento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.rol = rol;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
}
