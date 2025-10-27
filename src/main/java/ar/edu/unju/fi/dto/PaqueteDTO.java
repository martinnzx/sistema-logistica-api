package ar.edu.unju.fi.dto;

import ar.edu.unju.fi.model.NivelFragilidad;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaqueteDTO {
    private Long id;
    private Double pesoKg;
    private Double volumenDm3;
    private String tipo;
}
