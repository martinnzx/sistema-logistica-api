package ar.edu.unju.fi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

<<<<<<< HEAD
    private String comprobanteEntrega;
=======
    @OneToMany(cascade = CascadeType.ALL)
    private List<Paquete> paquetes;

>>>>>>> 2391608d1ae3387b382148ae5464e0ec3e95c49f
}
