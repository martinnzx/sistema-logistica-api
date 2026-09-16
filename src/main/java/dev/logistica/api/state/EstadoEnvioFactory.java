package dev.logistica.api.state;

import dev.logistica.api.enums.EstadoEnvio;

public class EstadoEnvioFactory {
    private EstadoEnvioFactory(){
        throw new IllegalStateException("estado envio factory");
    }
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

