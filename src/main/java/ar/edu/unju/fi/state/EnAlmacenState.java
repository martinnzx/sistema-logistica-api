package ar.edu.unju.fi.state;

import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.model.HistorialEstadoEnvio;
import ar.edu.unju.fi.repository.HistorialEstadoEnvioRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class EnAlmacenState implements EstadoEnvioState {
    @Override
    public void avanzar(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        cambiarEstado(envio, EstadoEnvio.EN_RUTA, "Envío salió a reparto", historialRepo);
    }

    @Override
    public void cancelar(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        cambiarEstado(envio, EstadoEnvio.CANCELADO, "Envío cancelado desde almacén", historialRepo);
    }
    @Override
    public void devolver(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        throw new IllegalStateException("No se puede devolver un envío que está en almacén.");
    }

    private void cambiarEstado(Envio envio, EstadoEnvio nuevo, String observacion,
                               HistorialEstadoEnvioRepository historialRepo) {
        EstadoEnvio anterior = envio.getEstado();
        envio.setEstado(nuevo);
        historialRepo.save(HistorialEstadoEnvio.builder()
                .envio(envio)
                .estadoAnterior(anterior)
                .estadoNuevo(nuevo)
                .fechaHora(LocalDateTime.now())
                .observacion(observacion)
                .build());
        log.info("Envío {} cambió de {} a {}", envio.getCodigoUnico(), anterior, nuevo);
    }
}
