package ar.edu.unju.fi.model;

import ar.edu.unju.fi.State.Estado;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Envios")
@Builder
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "remitente_id", nullable = false)
    private Cliente remitente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Cliente destinatario;

    @NotBlank
    private String direccionEntrega;

    @NotBlank
    @Column(length = 10, nullable = false)
    private String codigoPostal;

    /*@Enumerated(EnumType.STRING)
    @NotNull
    //private EstadoEnvio estado;*/

    private String comprobanteEntrega;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Paquete> paquetes;

    @Transient
    private Estado estadoN;
}
