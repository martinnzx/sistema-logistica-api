package ar.edu.unju.fi.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehiculoDTO {
    private String patente;
    private Double capacidadMaxPesoKg;
    private Double capacidadMaxVolDm3;
    private Boolean refrigerado;
    private Double rangoTemperaturaMin;
    private Double rangoTemperaturaMax;
}
