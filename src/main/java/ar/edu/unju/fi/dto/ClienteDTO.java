package ar.edu.unju.fi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    private Long id;
    private String nombreRazonSocial;
    private String documentoOCuit;
    private String telefono;
    private String email;
    private String direccionPrincipal;
    private String codigoPostal;
}
