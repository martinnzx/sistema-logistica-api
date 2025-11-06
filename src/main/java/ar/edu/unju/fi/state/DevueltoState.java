package ar.edu.unju.fi.state;

import ar.edu.unju.fi.model.Envio;

public class DevueltoState implements EstadoEnvioState {

    @Override
    public void avanzar(Envio envio) {
        throw new IllegalStateException("El envío DEVUELTO no puede avanzar más.");
    }

    @Override
    public void cancelar(Envio envio) {
        throw new IllegalStateException("El envío DEVUELTO no puede ser cancelado.");
    }

    @Override
    public void devolver(Envio envio) {
        throw new IllegalStateException("El envío ya está DEVUELTO.");
    }
}
