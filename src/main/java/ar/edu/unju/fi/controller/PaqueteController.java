package ar.edu.unju.fi.controller;

import ar.edu.unju.fi.controller.dto.MensajeError;
import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.service.PaqueteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones sobre paquetes.
 * Permite crear y listar paquetes por peso o volumen.
 */
@Slf4j
@RestController
@RequestMapping("/api/paquetes")
@RequiredArgsConstructor
@Tag(name = "paquetes", description = "Operaciones sobre Paquetes (frágiles o refrigerados)")
public class PaqueteController {

    private final PaqueteService paqueteService;

    @Operation(
            summary = "Crear un nuevo Paquete",
            description = "Crea un paquete del tipo indicado (frágil o refrigerado) validando sus datos antes de guardarlo.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Paquete creado exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PaqueteDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error de validación en los datos o tipo de paquete inválido",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<PaqueteDTO> crearPaquete(@Valid @RequestBody PaqueteDTO paqueteDTO) {
        log.info("Creando paquete tipo: {}", paqueteDTO.getTipo());
        PaqueteDTO nuevo = paqueteService.crearPaquete(paqueteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @Operation(
            summary = "Listar paquetes por rango de peso",
            description = "Devuelve todos los paquetes cuyo peso esté dentro del rango especificado (en kg).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Búsqueda exitosa",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = PaqueteDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetros inválidos o rango incorrecto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class))
                    )
            }
    )
    @GetMapping("/por-peso")
    public ResponseEntity<List<PaqueteDTO>> listarPorPeso(
            @RequestParam Double min,
            @RequestParam Double max) {

        log.info("Listando paquetes con peso entre {} kg y {} kg", min, max);
        List<PaqueteDTO> lista = paqueteService.listarPorPeso(min, max);
        return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Listar paquetes por rango de volumen",
            description = "Devuelve todos los paquetes cuyo volumen esté dentro del rango especificado (en dm3).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Búsqueda exitosa",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = PaqueteDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetros inválidos o rango incorrecto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class))
                    )
            }
    )
    @GetMapping("/por-volumen")
    public ResponseEntity<List<PaqueteDTO>> listarPorVolumen(
            @RequestParam Double min,
            @RequestParam Double max) {

        log.info("Listando paquetes con volumen entre {} dm3 y {} dm3", min, max);
        List<PaqueteDTO> lista = paqueteService.listarPorVolumen(min, max);
        return ResponseEntity.ok(lista);
    }
}