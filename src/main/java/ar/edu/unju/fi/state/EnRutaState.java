package ar.edu.unju.fi.state;

import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.enums.EstadoEnvio;

public class EnRutaState implements EstadoEnvioState {

    @Override
    public void avanzar(Envio envio) {
        // Validar que tenga comprobante antes de marcar como ENTREGADO
        if (envio.getComprobanteEntrega() == null || envio.getComprobanteEntrega().isBlank()) {
            throw new IllegalStateException("No se puede marcar como ENTREGADO sin comprobante de entrega.");
        }

        envio.setEstado(EstadoEnvio.ENTREGADO);
    }

    @Override
    public void cancelar(Envio envio) {
        throw new IllegalStateException("No se puede cancelar un envío que ya está EN RUTA");
    }

    @Override
    public void devolver(Envio envio) {
        envio.setEstado(EstadoEnvio.DEVUELTO);
    }
}
