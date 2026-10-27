package com.autoscolombia.parqueadero.dto;

import com.autoscolombia.parqueadero.model.Estadia;
import com.autoscolombia.parqueadero.model.Novedad;

import java.util.ArrayList;
import java.util.List;

public class ValidacionSalidaDto {

    private Estadia estadia;
    private boolean puedeSalir;
    private boolean pagoPendiente;
    private List<Novedad> novedadesBloqueantes = new ArrayList<>();
    private List<String> motivosBloqueo = new ArrayList<>();

    public ValidacionSalidaDto(Estadia estadia) {
        this.estadia = estadia;
        this.puedeSalir = true;
    }

    public void agregarMotivoBloqueo(String motivo) {
        this.motivosBloqueo.add(motivo);
        this.puedeSalir = false;
    }

    public Estadia getEstadia() {
        return estadia;
    }

    public void setEstadia(Estadia estadia) {
        this.estadia = estadia;
    }

    public boolean isPuedeSalir() {
        return puedeSalir;
    }

    public void setPuedeSalir(boolean puedeSalir) {
        this.puedeSalir = puedeSalir;
    }

    public boolean isPagoPendiente() {
        return pagoPendiente;
    }

    public void setPagoPendiente(boolean pagoPendiente) {
        this.pagoPendiente = pagoPendiente;
    }

    public List<Novedad> getNovedadesBloqueantes() {
        return novedadesBloqueantes;
    }

    public void setNovedadesBloqueantes(List<Novedad> novedadesBloqueantes) {
        this.novedadesBloqueantes = novedadesBloqueantes;
    }

    public List<String> getMotivosBloqueo() {
        return motivosBloqueo;
    }

    public void setMotivosBloqueo(List<String> motivosBloqueo) {
        this.motivosBloqueo = motivosBloqueo;
    }
}
