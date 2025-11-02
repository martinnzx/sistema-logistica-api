package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;

import java.time.LocalDateTime;


public class Estado_Entregado extends Estado {

    public Estado_Entregado(HistorialEnvioRepository repo) {
        super(repo);
    }

    @Override
    public void FlujoEnvio(Envio envio) {
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
        historialEnvioRepository.save(historial);
        ComprobanteEntrega(envio.getId(), historial.getId());
    }

    public void ComprobanteEntrega(Long idEnvio, Long idHistorial) {
        System.out.println("El paquete entregado a DESTINATARIO con firma: .......");
        System.out.println("El envio con Identificador: " + idEnvio + " y Historial con Identificador: " + idHistorial);
    }
}