package ar.edu.unju.fi.State;


import ar.edu.unju.fi.model.Envio;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class HistorialEnvio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Envio envio;
    private Estado anterior;
    private Estado nuevo;
    private String fechayHora;
    private String observacion;

    public HistorialEnvio(Envio envio, Estado estadoInicial, String observacion,String fechayHora) {
        this.envio = envio;
        this.anterior = null;
        this.nuevo = estadoInicial;
        this.observacion = observacion;
        this.fechayHora = fechayHora;
    }
    public HistorialEnvio(Envio envio,Estado estadoAnterior, Estado estadoNuevo, String observacion) {
        this.envio = envio;
        this.anterior = estadoAnterior;
        this.nuevo = estadoNuevo;
        this.observacion = observacion;
        this.fechayHora = java.time.LocalDateTime.now().toString();
    }
}
