package dev.logistica.api.state;

import dev.logistica.api.model.Envio;
import dev.logistica.api.repository.HistorialEstadoEnvioRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CanceladoState implements EstadoEnvioState {

    @Override
    public void avanzar(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        throw new IllegalStateException("El envío CANCELADO no puede avanzar.");
    }

    @Override
    public void cancelar(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        throw new IllegalStateException("El envío ya está CANCELADO.");
    }

    @Override
    public void devolver(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        throw new IllegalStateException("No se puede devolver un envío CANCELADO.");
    }
}
