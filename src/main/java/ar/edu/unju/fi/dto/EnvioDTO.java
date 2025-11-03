package ar.edu.unju.fi.dto;

import ar.edu.unju.fi.Enum.EstadoEnvio;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnvioDTO {
    private String remitente;
    private String destinatario;
    private String direccionEntrega;
    private EstadoEnvio estado;
    private String comprobanteEntrega;
    private List<PaqueteDTO> paquetes;
}
