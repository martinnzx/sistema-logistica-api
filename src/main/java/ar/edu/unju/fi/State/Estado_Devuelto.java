package ar.edu.unju.fi.State;

import ar.edu.unju.fi.model.Envio;

public class Estado_Devuelto extends Estado {
    @Override
    public void FlujoEnvio(Envio envio) {
        System.out.println("El envido fue devuelto");// log.info("Estado_En_Almacen del envio");

        Estado nuevo_Estado = new Estado_Generado();
        HistorialEnvio historial = new HistorialEnvio(
                envio,
                this,
                "Envio ah sido devuelto",
                "El Envio esta devuelto a las " +  ahora.format(formatoHora) + " del dia " + ahora.format(formatoFecha)
        );
        //historialEnvioRepository.save(historial);
        envio.setEstadoN(nuevo_Estado);
    }
}
