package ar.edu.unju.fi.State;

import ar.edu.unju.fi.model.Envio;

public class Estado_En_Ruta extends Estado {
    @Override
    public void FlujoEnvio(Envio envio) {
        System.out.println("El envio se encuentra en Ruta");// log.info("Estado_En_Almacen del envio");

        Estado nuevo_Estado = new Estado_Generado();
        HistorialEnvio historial = new HistorialEnvio(
                envio,
                this,
                "Envio en Ruta",
                "El Envio en Ruta a las " +  ahora.format(formatoHora) + " del dia " + ahora.format(formatoFecha)
        );
        //historialEnvioRepository.save(historial);
        envio.setEstadoN(nuevo_Estado);
    }
}
