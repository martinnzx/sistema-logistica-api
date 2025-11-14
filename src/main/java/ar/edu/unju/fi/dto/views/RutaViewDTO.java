package ar.edu.unju.fi.dto.views;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutaViewDTO {
    @NotNull(message = "La fecha no puede ser nula.")
    @FutureOrPresent(message = "La fecha de la ruta debe ser hoy o una fecha futura.")
    private LocalDate fecha;

    @NotEmpty(message = "La ruta debe contener al menos un código de envío.")
    private List<String> codigoEnvios;

    @NotBlank(message = "La patente del vehículo no puede estar vacía.")
    private String patenteVehiculo;
}
