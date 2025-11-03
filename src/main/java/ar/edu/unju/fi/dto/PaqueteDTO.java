package ar.edu.unju.fi.dto;

import ar.edu.unju.fi.Enum.NivelFragilidad;
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
    private String codigo;

    private NivelFragilidad nivelFragilidad;
    private Boolean seguroAdicional;

    private Double temperaturaObjetivo;
    private Double rangoMin;
    private Double rangoMax;
    private Integer horasMaxFueraDeFrio;
}
