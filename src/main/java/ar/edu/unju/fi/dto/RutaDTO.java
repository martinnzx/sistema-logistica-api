package ar.edu.unju.fi.dto;

import lombok.Getter;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RutaDTO {
    private Long id;
    private LocalDate fecha;
    private List<EnvioDTO> envios;
    private VehiculoDTO vehiculo;
}
