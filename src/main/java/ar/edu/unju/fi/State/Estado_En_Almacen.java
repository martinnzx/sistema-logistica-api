package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class Estado_En_Almacen extends Estado {
    private final Estado_En_Ruta estadoEnRuta;

    public Estado_En_Almacen(HistorialEnvioRepository repo, Estado_En_Ruta estadoEnRuta) {
        super(repo);

        this.estadoEnRuta = estadoEnRuta;
    }

    @Override
    public void FlujoEnvio(Envio envio) {
        System.out.println("El paquete se encuentra en el almacen listo para ruta");

        LocalDateTime ahora = getAhora();
        String fechayHora = "El Envio paso a Ruta a las " + ahora.format(formatoHora) + " del dia " + ahora.format(formatoFecha);

        Estado anterior = this;
        Estado nuevo = this.estadoEnRuta;

        HistorialEnvio historial = new HistorialEnvio(
                envio,
                anterior,
                nuevo,
                "Envio preparado para Ruta",
                fechayHora
        );

        historialEnvioRepository.save(historial);
        envio.setEstadoN(nuevo);
    }
}
