package ar.edu.unju.fi.model;


import ar.edu.unju.fi.State.Estado;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class HistorialEnvio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "envio_id")
    private Envio envio;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "estado_anterior_id")
    private Estado anterior;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "estado_nuevo_id")
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
    public HistorialEnvio(Envio envio,Estado estadoAnterior, Estado estadoNuevo, String observacion,String fechayHora) {
        this.envio = envio;
        this.anterior = estadoAnterior;
        this.nuevo = estadoNuevo;
        this.observacion = observacion;
        this.fechayHora = fechayHora;
    }
}
