package ar.edu.unju.fi.dto.views;

import ar.edu.unju.fi.enums.EstadoEnvio;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioViewDestinatarioDTO {
    private String destinatarioNombre;
    private String correoDestinatario;
    private String remitenteNombre;
    private String direccionEntrega;
    private String codigoPostal;
    private EstadoEnvio estado;
    private String codigoUnico;
    private List<String> codigoPaquetes;
}
