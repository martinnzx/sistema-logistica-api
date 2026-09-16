package dev.logistica.api.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatosEmailDTO {
    private String emailPara;       // A quién se envía
    private String codigo;
    private String destinatario;

    private String remitente;

    private String direccion;
}

