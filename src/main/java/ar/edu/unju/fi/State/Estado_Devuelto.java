package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.HistorialEnvio;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("Devuelto")
public class Estado_Devuelto extends Estado {

    @Override
    public void FlujoEnvio(Envio envio, HistorialEnvioRepository historialRepo) {
        System.out.println("El envio fue devuelto");

        LocalDateTime ahora = getAhora();
        String fechayHora = "El Envio Devuelto en la sucursal a las " + ahora.format(formatoHora) + " del dia " + ahora.format(formatoFecha);

        Estado estado = this;

        HistorialEnvio historial = new HistorialEnvio(
                envio,
                estado,
                "Envio Devuelto por el destinatario",
                fechayHora
        );

        historialRepo.save(historial);
    }
}
