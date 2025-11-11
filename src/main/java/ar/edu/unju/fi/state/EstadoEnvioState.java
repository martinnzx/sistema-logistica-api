package ar.edu.unju.fi.state;

import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.repository.HistorialEstadoEnvioRepository;

public interface EstadoEnvioState {
    void avanzar(Envio envio, HistorialEstadoEnvioRepository historialRepo,String observacion);
    void cancelar(Envio envio, HistorialEstadoEnvioRepository historialRepo,String observacion);
    void devolver(Envio envio, HistorialEstadoEnvioRepository historialRepo,String observacion);
}