package dev.logistica.api.controller.doc;

import dev.logistica.api.controller.dto.MensajeError;
import dev.logistica.api.controller.dto.Error404;
import dev.logistica.api.dto.PaqueteDTO;
import dev.logistica.api.service.PaqueteService;
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
 * Controlador para gestionar paquetes. Permite crearlos, buscarlos y filtrarlos
 * por distintos criterios como peso o volumen.
 */
@RequestMapping("/api/paquetes")
@Tag(name = "Paquetes", description = "ABM y consultas sobre paquetes (frágiles o refrigerados)")
public interface PaqueteApi {


    // ===========================================================
    //                       CREAR PAQUETE
    // ===========================================================

    @Operation(
            summary = "Crear un nuevo paquete (Frágil o Refrigerado)",
            description = """
            Registra un paquete validando sus campos específicos según el tipo:
        
            * **Si es FRAGIL:** Se requieren `nivelFragilidad` y `seguroAdicional`.
            * **Si es REFRIGERADOS:** Se requieren `temperaturaObjetivo`, `rangoMin`, `rangoMax` y `horasMaxFueraDeFrio`.
            * **Comunes:** `pesoKg` y `volumenDm3` son siempre obligatorios.
            * ** A los demas campos no rellenados dejarlos como `null`
        """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Paquete creado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PaqueteDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos inválidos o tipo de paquete incorrecto",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)
                            )
                    )
            }
    )
    @PostMapping
public ResponseEntity<PaqueteDTO> crearPaquete(@Valid @RequestBody PaqueteDTO paqueteDTO);

    // ===========================================================
    //                 LISTAR POR RANGO DE PESO
    // ===========================================================

    @Operation(
            summary = "Listar paquetes por rango de peso",
            description = "Devuelve los paquetes cuyo peso se encuentre dentro del rango especificado (kg).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado obtenido correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = PaqueteDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetros inválidos o rango ingresado incorrecto",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)
                            )
                    )
            }
    )
    @GetMapping("/por-peso")
public ResponseEntity<List<PaqueteDTO>> listarPorPeso(
            @RequestParam Double min,
@RequestParam Double max);

    // ===========================================================
    //                 LISTAR POR RANGO DE VOLUMEN
    // ===========================================================

    @Operation(
            summary = "Listar paquetes por rango de volumen",
            description = "Devuelve los paquetes cuyo volumen esté dentro del rango indicado (dm³).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado obtenido correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = PaqueteDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetros inválidos o rango ingresado incorrecto",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)
                            )
                    )
            }
    )
    @GetMapping("/por-volumen")
public ResponseEntity<List<PaqueteDTO>> listarPorVolumen(
            @RequestParam Double min,
@RequestParam Double max);

    // ===========================================================
    //                      BUSCAR POR CÓDIGO
    // ===========================================================

    @Operation(
            summary = "Buscar paquete por código",
            description = "Busca un paquete específico usando su código único.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Paquete encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PaqueteDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No se encontró paquete con el código indicado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error404.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor",
                            content = @Content
                    )
            }
    )
    @GetMapping("/codigo/{codigo}")
public ResponseEntity<PaqueteDTO> buscarPaquetePorCodigo(@PathVariable String codigo);

    // ===========================================================
    //                  LISTAR TODOS LOS PAQUETES
    // ===========================================================

    @Operation(
            summary = "Listar todos los paquetes",
            description = "Devuelve todos los paquetes registrados, sin importar su tipo o estado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado obtenido correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = PaqueteDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor",
                            content = @Content
                    )
            }
    )
    @GetMapping
public ResponseEntity<List<PaqueteDTO>> listarPaquetes();
}
