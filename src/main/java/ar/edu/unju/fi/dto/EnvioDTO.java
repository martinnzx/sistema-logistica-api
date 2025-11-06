package ar.edu.unju.fi.dto;

import ar.edu.unju.fi.enums.EstadoEnvio;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnvioDTO {
    private Long id;
    private ClienteDTO remitente;
    private ClienteDTO destinatario;
    private String direccionEntrega;
    private String codigoPostal;
    private EstadoEnvio estado;
    private Boolean requiereFrio;
    private String codigoUnico;
    private String comprobanteEntrega;
    private List<PaqueteDTO> paquetes;
}
