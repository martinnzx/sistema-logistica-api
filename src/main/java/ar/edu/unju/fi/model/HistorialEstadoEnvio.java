package ar.edu.unju.fi.model;

import ar.edu.unju.fi.enums.EstadoEnvio;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialEstadoEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "envio_id")
    private Envio envio;

    @Enumerated(EnumType.STRING)
    private EstadoEnvio estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoEnvio estadoNuevo;

    private LocalDateTime fechaHora;

    private String observacion;
}