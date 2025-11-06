package ar.edu.unju.fi.state;

import ar.edu.unju.fi.model.Envio;

public interface EstadoEnvioState {
    void avanzar(Envio envio);
    void cancelar(Envio envio);
    void devolver(Envio envio);
}