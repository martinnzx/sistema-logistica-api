package ar.edu.unju.fi.state;

import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.enums.EstadoEnvio;

public class EnAlmacenState implements EstadoEnvioState {

    @Override
    public void avanzar(Envio envio) {
        envio.setEstado(EstadoEnvio.EN_RUTA);
    }

    @Override
    public void cancelar(Envio envio) {
        envio.setEstado(EstadoEnvio.CANCELADO);
    }

    @Override
    public void devolver(Envio envio) {
        throw new IllegalStateException("No se puede devolver un envío en estado EN ALMACÉN");
    }
}
