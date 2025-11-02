package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;

import java.time.LocalDateTime;

public class Estado_Devuelto extends Estado {

    public Estado_Devuelto(HistorialEnvioRepository repo) {
        super(repo);
    }

    @Override
    public void FlujoEnvio(Envio envio) {
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

        historialEnvioRepository.save(historial);
    }
}
