package ar.edu.unju.fi.controller;

import ar.edu.unju.fi.controller.dto.MensajeError;
import ar.edu.unju.fi.controller.dto.Error404;
import ar.edu.unju.fi.dto.RutaDTO;
import ar.edu.unju.fi.dto.views.RutaViewDTO;
import ar.edu.unju.fi.service.RutaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
@Tag(name = "Rutas", description = "Operaciones para gestionar rutas y sus envíos asociados")
public class RutaController {

    private final RutaService rutaService;

    // ===========================================================
    //                     CREAR RUTA NUEVA
    // ===========================================================

    @Operation(
            summary = "Crear una nueva ruta",
            description = "Genera una ruta asignando un vehículo (por patente) y un conjunto de envíos (por sus códigos).",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Ruta creada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RutaDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos inválidos o falló la validación",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<RutaDTO> crearRuta(@Valid @RequestBody RutaViewDTO viewDTO) {

        log.info("Solicitud para crear ruta recibida. Patente del vehículo: {}", viewDTO.getPatenteVehiculo());

        RutaDTO nueva = rutaService.crearRuta(viewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    // ===========================================================
    //           CONSULTAR ENVÍOS DE UNA RUTA EN UNA FECHA
    // ===========================================================

    @Operation(
            summary = "Consultar envíos asignados a una ruta en una fecha",
            description = "Busca la ruta por su ID y devuelve los envíos asociados a la fecha indicada.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ruta encontrada y envíos devueltos correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RutaDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "La ruta existe pero no tiene envíos en la fecha indicada"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetros inválidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "La ruta no existe",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error404.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}/envios")
    public ResponseEntity<?> obtenerEnviosPorRutaYFecha(
            @PathVariable Long id,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        log.info("Consultando envíos de la ruta {} en fecha {}", id, fecha);

        List<RutaDTO> resultado = rutaService.obtenerEnviosPorRutaYFecha(id, fecha);

        if (resultado.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resultado.get(0));
    }
}

