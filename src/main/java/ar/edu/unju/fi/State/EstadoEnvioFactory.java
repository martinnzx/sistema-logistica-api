package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Enum.EstadoEnvio;

public class EstadoEnvioFactory {

    public static EstadoEnvioState getEstado(EstadoEnvio estado) {
        return switch (estado) {
            case GENERADO -> new GeneradoState();
            case EN_ALMACEN -> new EnAlmacenState();
            case EN_RUTA -> new EnRutaState();
            case ENTREGADO -> new EntregadoState();
            case DEVUELTO -> new DevueltoState();
            case CANCELADO -> new CanceladoState();
        };
    }
}
