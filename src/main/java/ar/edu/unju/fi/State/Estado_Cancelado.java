package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.HistorialEnvio;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("CANCELADO")
public class Estado_Cancelado extends Estado{

    @Override
    public void FlujoEnvio(Envio envio, HistorialEnvioRepository historialRepo) {
        System.out.println("El Envio ha sido cancelado");

        LocalDateTime ahora = getAhora();
        String fechayHora = "El Envio CANCELADO a las " + ahora.format(formatoHora) + " del dia " + ahora.format(formatoFecha);

        Estado estado = this;

        HistorialEnvio historial = new HistorialEnvio(
                envio,
                estado,
                "Envio CANCELADO por el remitente",
                fechayHora
        );
        historialRepo.save(historial);
    }
}
