package ar.edu.unju.fi.dto;

import java.time.LocalDate;
import java.util.List;

public class RutaDTO {
    private Long id;
    private LocalDate fecha;
    private List<EnvioDTO> envios;
}
