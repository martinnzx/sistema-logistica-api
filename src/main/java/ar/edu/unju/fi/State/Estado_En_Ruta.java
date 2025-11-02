package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;

import java.time.LocalDateTime;

public class Estado_En_Ruta extends Estado {
    // 1. Inyectamos el SIGUIENTE estado
    private final Estado_Entregado estadoEntregado;

    public Estado_En_Ruta(HistorialEnvioRepository repo, Estado_Entregado estadoEntregado) {
        super(repo);
        this.estadoEntregado = estadoEntregado;
    }

    @Override
    public void FlujoEnvio(Envio envio) {
        System.out.println("El envio se encuentra en Ruta");

        LocalDateTime ahora = getAhora();
        String fechayHora = "El Envio preparado para entregar a las " + ahora.format(formatoHora) + " del dia " + ahora.format(formatoFecha);

        Estado anterior = this;
        Estado nuevo = this.estadoEntregado;

        HistorialEnvio historial = new HistorialEnvio(
                envio,
                anterior,
                nuevo,
                "Envio se encuentra listo para Entregar",
                fechayHora
        );

        historialEnvioRepository.save(historial);
        envio.setEstadoN(nuevo);
    }
}
