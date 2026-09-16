package dev.logistica.api.state;

import dev.logistica.api.model.Envio;
import dev.logistica.api.repository.HistorialEstadoEnvioRepository;

public interface EstadoEnvioState {
    void avanzar(Envio envio, HistorialEstadoEnvioRepository historialRepo,String observacion);
    void cancelar(Envio envio, HistorialEstadoEnvioRepository historialRepo,String observacion);
    void devolver(Envio envio, HistorialEstadoEnvioRepository historialRepo,String observacion);
}
