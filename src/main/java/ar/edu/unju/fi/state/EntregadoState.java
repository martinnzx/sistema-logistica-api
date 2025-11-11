package ar.edu.unju.fi.state;

import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.repository.HistorialEstadoEnvioRepository;
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