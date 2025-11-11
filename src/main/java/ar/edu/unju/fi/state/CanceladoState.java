package ar.edu.unju.fi.state;

import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.repository.HistorialEstadoEnvioRepository;
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