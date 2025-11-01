package ar.edu.unju.fi.State;

import ar.edu.unju.fi.model.Envio;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Estado_En_Almacen extends Estado {
    @Override
    public void FlujoEnvio(Envio envio) {
        System.out.println("El paquete se encuentra en el almacen");// log.info("Estado_En_Almacen del envio");

        Estado nuevo_Estado = new Estado_Generado();
        HistorialEnvio historial = new HistorialEnvio(
                envio,
                this,
                "Envio enta en el almacen",
                "El Envio en Almancen a las " +  ahora.format(formatoHora) + " del dia " + ahora.format(formatoFecha)
        );
        //historialEnvioRepository.save(historial);
        envio.setEstadoN(nuevo_Estado);
    }
}
