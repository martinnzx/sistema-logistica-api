package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.HistorialEnvio;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("ENTREGADO")
public class Estado_Entregado extends Estado {

    @Override
    public void FlujoEnvio(Envio envio, HistorialEnvioRepository historialRepo) {
        System.out.println("El envío ID: " + envio.getId() + " ya fue ENTREGADO. No hay más acciones.");
        LocalDateTime ahora = getAhora();
        String fechayHora = "El Envio ENTREGADO en la sucursal a las " + ahora.format(formatoHora) + " del dia " + ahora.format(formatoFecha);

        Estado estado = this;
        HistorialEnvio historial = new HistorialEnvio(
                envio,
                estado,
                "Envio Entregado al destinatario",
                fechayHora
        );
        historialRepo.save(historial);
        ComprobanteEntrega(envio.getId(), historial.getId());
    }

    public void ComprobanteEntrega(Long idEnvio, Long idHistorial) {
        System.out.println("El paquete entregado a DESTINATARIO con firma: .......");
        System.out.println("El envio con Identificador: " + idEnvio + " y Historial con Identificador: " + idHistorial);
    }
}