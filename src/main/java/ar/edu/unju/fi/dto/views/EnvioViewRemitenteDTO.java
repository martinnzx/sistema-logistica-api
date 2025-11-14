package ar.edu.unju.fi.dto.views;

import ar.edu.unju.fi.enums.EstadoEnvio;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioViewRemitenteDTO {
    private String remitenteNombre;
    private String correoRemitente;
    private String destinatarioNombre;
    private String direccionEntrega;
    private String codigoPostal;
    private EstadoEnvio estado;
    private String codigoUnico;
    private List<String> codigoPaquetes;
}
