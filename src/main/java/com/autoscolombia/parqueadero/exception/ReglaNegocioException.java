package com.autoscolombia.parqueadero.exception;

public class ReglaNegocioException extends RuntimeException {

    private final String tipoError; // 'PLACA_INVALIDA', 'SIN_CUPOS', 'INGRESO_ACTIVO', 'DATOS_FALTANTES', etc.

    public ReglaNegocioException(String message) {
        super(message);
        this.tipoError = "GENERAL";
    }

    public ReglaNegocioException(String tipoError, String message) {
        super(message);
        this.tipoError = tipoError;
    }

    public String getTipoError() {
        return tipoError;
    }
}
