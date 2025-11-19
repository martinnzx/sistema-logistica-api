package ar.edu.unju.fi.dto.views;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PDFcomprobanteDTO {
    private String Remitente;
    private String Destinatario;
    private String codigo;
    private String estado;
    private LocalDateTime fechaHora;
    private String observaciones;
}
