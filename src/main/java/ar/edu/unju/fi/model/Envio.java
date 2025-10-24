package ar.edu.unju.fi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Envios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String remitente;

    @NotBlank
    private String destinatario;

    @NotBlank
    private String direccionEntrega;

    @NotBlank
    private String comprobanteEntrega;

    @Enumerated(EnumType.STRING)
    private EstadoEnvio estado;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Paquete> paquetes;

}
