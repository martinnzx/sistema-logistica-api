package dev.logistica.api.dto.views;

import dev.logistica.api.enums.EstadoEnvio;
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

