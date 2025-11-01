package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


public class Estado_Entregado extends Estado{


    @Override
    public void FlujoEnvio(Envio envio) {
        System.out.println("Envio Entregado");

        Estado nuevo_Estado = new Estado_Generado();
        HistorialEnvio historial = new HistorialEnvio(
                envio,
                this,
                "Envio Entregado",
                "El Envio fue Entregado a las " +  ahora.format(formatoHora) + " del dia " + ahora.format(formatoFecha)
        );
        //historialEnvioRepository.save(historial);
        envio.setEstadoN(nuevo_Estado);

    }
}
