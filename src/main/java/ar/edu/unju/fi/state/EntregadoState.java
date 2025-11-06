package ar.edu.unju.fi.state;

import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.enums.EstadoEnvio;

public class EntregadoState implements EstadoEnvioState {

    @Override
    public void avanzar(Envio envio) {
        if (envio.getComprobanteEntrega() == null || envio.getComprobanteEntrega().isBlank()) {
            throw new IllegalStateException("No se puede entregar el envío sin comprobante de entrega.");
        }
        envio.setEstado(EstadoEnvio.ENTREGADO);
    }

    @Override
    public void cancelar(Envio envio) {
        throw new IllegalStateException("No se puede cancelar un envío ENTREGADO.");
    }

    @Override
    public void devolver(Envio envio) {
        throw new IllegalStateException("No se puede devolver un envío ENTREGADO.");
    }
}