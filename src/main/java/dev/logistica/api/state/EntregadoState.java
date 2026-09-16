package dev.logistica.api.state;

import dev.logistica.api.model.Envio;
import dev.logistica.api.repository.HistorialEstadoEnvioRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntregadoState implements EstadoEnvioState {

    @Override
    public void avanzar(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        throw new IllegalStateException("El envío ya fue ENTREGADO. No puede avanzar más.");
    }

    @Override
    public void cancelar(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        throw new IllegalStateException("No se puede cancelar un envío ya ENTREGADO.");
    }

    @Override
    public void devolver(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        throw new IllegalStateException("No se puede devolver un envío ENTREGADO.");
    }
}
