package ar.edu.unju.fi.State;

import ar.edu.unju.fi.model.Envio;

public class CanceladoState implements EstadoEnvioState {

    @Override
    public void avanzar(Envio envio) {
        throw new IllegalStateException("El envío CANCELADO no puede avanzar.");
    }

    @Override
    public void cancelar(Envio envio) {
        throw new IllegalStateException("El envío ya está CANCELADO.");
    }

    @Override
    public void devolver(Envio envio) {
        throw new IllegalStateException("No se puede devolver un envío CANCELADO.");
    }
}