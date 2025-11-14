package ar.edu.unju.fi.dto.views;

import ar.edu.unju.fi.enums.EstadoEnvio;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnvioViewEstadoDTO {
    private String destinatarioNombre;
    private String remitenteNombre;
    private String direccionEntrega;
    private String codigoPostal;
    private EstadoEnvio estado;
    private String codigoUnico;
    private List<String> codigoPaquetes;
}
