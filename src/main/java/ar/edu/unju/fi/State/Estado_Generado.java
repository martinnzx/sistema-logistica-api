package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.HistorialEnvio;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("GENERADO")
public class Estado_Generado extends Estado{

    @Override
    public void FlujoEnvio(Envio envio, HistorialEnvioRepository historialRepo) {
        Estado nuevo = new Estado_En_Almacen();

        LocalDateTime ahora = getAhora();
        String fechaHora = "Cambio a EN_ALMACÉN a las " +
                ahora.format(formatoHora) + " del día " + ahora.format(formatoFecha);

        HistorialEnvio historial = new HistorialEnvio(
                envio,
                this,
                nuevo,
                "Envío listo para almacenarse",
                fechaHora
        );
        historialRepo.save(historial);
        envio.setEstado(nuevo);
    }
}
