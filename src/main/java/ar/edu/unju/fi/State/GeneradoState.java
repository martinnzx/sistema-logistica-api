package ar.edu.unju.fi.State;

import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.Enum.EstadoEnvio;

public class GeneradoState implements EstadoEnvioState {

    @Override
    public void avanzar(Envio envio) {
        envio.setEstado(EstadoEnvio.EN_ALMACEN);
    }

    @Override
    public void cancelar(Envio envio) {
        envio.setEstado(EstadoEnvio.CANCELADO);
    }

    @Override
    public void devolver(Envio envio) {
        throw new IllegalStateException("No se puede devolver un envío en estado GENERADO");
    }
}
