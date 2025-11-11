package ar.edu.unju.fi.state;

import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.repository.HistorialEstadoEnvioRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DevueltoState implements EstadoEnvioState {


    @Override
    public void avanzar(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        throw new IllegalStateException("El envío DEVUELTO no puede avanzar más.");
    }

    @Override
    public void cancelar(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        throw new IllegalStateException("No se puede cancelar un envío DEVUELTO.");
    }

    @Override
    public void devolver(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        throw new IllegalStateException("El envío ya está DEVUELTO.");
    }
}
