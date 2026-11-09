package com.autoscolombia.parqueadero.security.dto;

public class AuthRequest {

    private String identificador; // Puede ser correo o número de documento
    private String password;

    public AuthRequest() {
    }

    public AuthRequest(String identificador, String password) {
        this.identificador = identificador;
        this.password = password;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
