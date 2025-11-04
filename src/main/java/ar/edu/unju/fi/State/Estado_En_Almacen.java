package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.HistorialEnvio;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Entity
@DiscriminatorValue("En_Almacen")
public class Estado_En_Almacen extends Estado {

    @Override
    // ERROR 1: La firma del método estaba incorrecta
    public void FlujoEnvio(Envio envio, HistorialEnvioRepository historialRepo) {
        System.out.println("El paquete se encuentra en el almacen listo para ruta");

        LocalDateTime ahora = getAhora();
        String fechayHora = "El Envio paso a Ruta a las " + ahora.format(formatoHora) + " del dia " + ahora.format(formatoFecha);

        Estado anterior = this;
        Estado nuevo = new Estado_En_Ruta();

        HistorialEnvio historial = new HistorialEnvio(
                envio,
                anterior,
                nuevo,
                "Envio preparado para Ruta",
                fechayHora
        );
        historialRepo.save(historial);
        envio.setEstado(nuevo);
    }
}
