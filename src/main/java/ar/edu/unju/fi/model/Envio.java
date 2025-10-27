package ar.edu.unju.fi.model;

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

    @NotBlank
    private String remitente;

    @NotBlank
    private String destinatario;

    @NotBlank
    private String direccionEntrega;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EstadoEnvio estado;

    private String comprobanteEntrega;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Paquete> paquetes;

}
