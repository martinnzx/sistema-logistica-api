package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.HistorialEnvio;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("EstadoEnRuta")
public class Estado_En_Ruta extends Estado {

    @Override
    public void FlujoEnvio(Envio envio, HistorialEnvioRepository historialRepo) {
        System.out.println("El envio se encuentra en Ruta");

        LocalDateTime ahora = getAhora();
        String fechayHora = "El Envio preparado para entregar a las " + ahora.format(formatoHora) + " del dia " + ahora.format(formatoFecha);

        Estado anterior = this;
        Estado nuevo = new Estado_Entregado();
        HistorialEnvio historial = new HistorialEnvio(
                envio,
                anterior,
                nuevo,
                "Envio se encuentra listo para Entregar",
                fechayHora
        );
        historialRepo.save(historial);
        envio.setEstado(nuevo);
    }
}
