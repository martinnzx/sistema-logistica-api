package ar.edu.unju.fi.controller;

import ar.edu.unju.fi.controller.dto.MensajeError;
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
@Tag(name = "rutas", description = "Gestión de rutas y asignación de envíos")
public class RutaController {

    private final RutaService rutaService;

    @Operation(
            summary = "Crear una nueva Ruta",
            description = "Genera una ruta asignando un vehículo (por patente) y una lista de envíos (por códigos).",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Ruta creada exitosamente",
                            content = @Content(mediaType = "application/json",
                                    // La RESPUESTA sigue siendo el DTO completo
                                    schema = @Schema(implementation = RutaDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Validación fallida o datos incorrectos",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)))
            }
    )
    @PostMapping
    public ResponseEntity<RutaDTO> crearRuta(
            @Valid @RequestBody RutaViewDTO viewDTO
    ) {
        log.info("Solicitud para crear ruta recibida para patente: {}", viewDTO.getPatenteVehiculo());

        RutaDTO nueva = rutaService.crearRuta(viewDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    /* =======================================================
       2. CONSULTAR ENVÍOS ASIGNADOS A UNA RUTA EN UNA FECHA
    ======================================================= */
    @Operation(
            summary = "Consultar envíos asignados a una ruta en una fecha",
            description = "Devuelve la ruta y los envíos asociados si coinciden con la fecha indicada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ruta encontrada y envíos devueltos",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RutaDTO.class))),
                    @ApiResponse(responseCode = "204", description = "La ruta existe pero no tiene envíos en esa fecha"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)))
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

