package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;

public class Estado_Generado extends Estado{
    @Override
    public void FlujoEnvio(Envio envio) {
        System.out.println("Envio Generado ");

        Estado nuevo_Estado = new Estado_Generado();
        HistorialEnvio historial = new HistorialEnvio(
                envio,
                this,
                "Envio Generado",
                "El Envio fue generado a las " +  ahora.format(formatoHora) + " del dia " + ahora.format(formatoFecha)
        );
        //HistorialEnvioRepository.save(historial);

        envio.setEstadoN(nuevo_Estado);

    }
}
