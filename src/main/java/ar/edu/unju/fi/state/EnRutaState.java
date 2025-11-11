package ar.edu.unju.fi.state;

import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.model.HistorialEstadoEnvio;
import ar.edu.unju.fi.repository.HistorialEstadoEnvioRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class EnRutaState implements EstadoEnvioState {

    @Override
    public void avanzar(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        if (envio.getComprobanteEntrega() == null || envio.getComprobanteEntrega().isBlank()) {
            throw new IllegalStateException("No se puede marcar como ENTREGADO sin comprobante de entrega.");
        }
        cambiarEstado(envio, EstadoEnvio.ENTREGADO, "Envío entregado correctamente", historialRepo);
    }

    @Override
    public void cancelar(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        throw new IllegalStateException("No se puede cancelar un envío que ya está en ruta.");
    }

    @Override
    public void devolver(Envio envio, HistorialEstadoEnvioRepository historialRepo, String observacion) {
        cambiarEstado(envio, EstadoEnvio.DEVUELTO, "Envío devuelto al almacén", historialRepo);
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
