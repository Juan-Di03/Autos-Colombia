package com.autoscolombia.parqueadero.security.dto;

public class AuthResponse {

    private String token;
    private long expiresIn;
    private String issuedAt;
    private String rol;
    private String nombre;

    public AuthResponse() {
    }

    public AuthResponse(String token, long expiresIn, String issuedAt, String rol, String nombre) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.issuedAt = issuedAt;
        this.rol = rol;
        this.nombre = nombre;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
