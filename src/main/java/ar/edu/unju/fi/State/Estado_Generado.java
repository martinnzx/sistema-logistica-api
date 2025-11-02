package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;

import java.time.LocalDateTime;

public class Estado_Generado extends Estado{
    private final Estado_En_Almacen estadoEnAlmacen;

    public Estado_Generado(HistorialEnvioRepository repo,
                           Estado_En_Almacen estadoEnAlmacen) {
        super(repo);
        this.estadoEnAlmacen = estadoEnAlmacen;
    }

    @Override
    public void FlujoEnvio(Envio envio) {
        System.out.println("Envio Generado ");

        LocalDateTime ahora = getAhora();
        String fechayHora = "El Envio en el Almacen a las " + ahora.format(formatoHora) + " del dia " + ahora.format(formatoFecha);

        Estado anterior = this;
        Estado nuevo = this.estadoEnAlmacen;
        HistorialEnvio historial = new HistorialEnvio(
                envio,
                anterior,
                nuevo,
                "Envio listo para pasar a Almacen",
                fechayHora
        );
        historialEnvioRepository.save(historial);

        envio.setEstadoN(nuevo);
    }
}
