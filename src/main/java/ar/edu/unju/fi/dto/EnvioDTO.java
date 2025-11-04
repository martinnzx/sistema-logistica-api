package ar.edu.unju.fi.dto;

import ar.edu.unju.fi.Enum.EstadoEnvio;
import ar.edu.unju.fi.model.Cliente;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnvioDTO {
    private ClienteDTO remitente;
    private ClienteDTO destinatario;
    private String direccionEntrega;
    private EstadoEnvio estado;
    private String comprobanteEntrega;
    private List<PaqueteDTO> paquetes;
}
