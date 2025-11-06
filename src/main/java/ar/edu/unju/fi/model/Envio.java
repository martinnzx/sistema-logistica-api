package ar.edu.unju.fi.model;

import ar.edu.unju.fi.enums.EstadoEnvio;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
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

    @ManyToOne
    @JoinColumn(name = "remitente_id")
    @NotNull
    private Cliente remitente;

    @ManyToOne
    @JoinColumn(name = "destinatario_id")
    @NotNull
    private Cliente destinatario;

    @NotBlank
    private String direccionEntrega;

    @NotBlank
    private String codigoPostal;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EstadoEnvio estado;

    private Boolean requiereFrio = false;

    @Column(unique = true, nullable = false)
    private String codigoUnico;

    private String comprobanteEntrega;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Paquete> paquetes = new ArrayList<>();;

}
