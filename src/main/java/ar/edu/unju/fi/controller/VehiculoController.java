package ar.edu.unju.fi.controller;

import ar.edu.unju.fi.controller.dto.MensajeError;
import ar.edu.unju.fi.dto.VehiculoDTO;
import ar.edu.unju.fi.service.VehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/vehiculos")
@Tag(name="vehiculos", description = "operaciones sobre Vehiculos")
public class VehiculoController {
    private final VehiculoService vehiculoService;
    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @Operation(
            summary = "Registrar un nuevo vehículo",
            description = "Crea un nuevo vehículo en el sistema. Se validará que la patente no exista previamente y que las capacidades sean valores positivos. Si es refrigerado, se validarán los rangos de temperatura.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Vehículo creado exitosamente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = VehiculoDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos inválidos. Revise el formato de la patente o valores negativos.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflicto: Ya existe un vehículo registrado con esa patente.",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor.",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping
    public ResponseEntity<VehiculoDTO> crearVehiculo(@Valid @RequestBody VehiculoDTO vehiculoDTO) {
        log.info("Iniciando creación de vehículo con patente: {}", vehiculoDTO.getPatente());

        VehiculoDTO nuevo = vehiculoService.crearVehiculo(vehiculoDTO);
        URI location = URI.create(String.format("/api/vehiculos/%s", nuevo.getPatente()));

        return ResponseEntity.created(location).body(nuevo);
    }

    @Operation(
            summary = "Buscar vehículos por estado de refrigeración",
            description = "Devuelve una lista de vehículos filtrados por si tienen o no sistema de refrigeración.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Búsqueda exitosa",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = VehiculoDTO.class))
                    )
            }
    )
    @GetMapping("/buscar/refrigerados")
    public ResponseEntity<List<VehiculoDTO>> buscarPorRefrigeracion(
            @RequestParam("refrigerado") Boolean refrigerado) {

        List<VehiculoDTO> lista = vehiculoService.buscarVehiculosRefrigerados(refrigerado);
        return ResponseEntity.ok(lista);
    }


    @Operation(
            summary = "Buscar vehículos por capacidad de peso",
            description = "Devuelve una lista de vehículos que soportan una capacidad de peso mínima requerida.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Búsqueda exitosa",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = VehiculoDTO.class))
                    )
            }
    )
    @GetMapping("/buscar/por-peso")
    public ResponseEntity<List<VehiculoDTO>> buscarPorPeso(
            @RequestParam("pesoMinimo") Double pesoRequerido) {

        List<VehiculoDTO> lista = vehiculoService.buscarVehiculosPorPeso(pesoRequerido);
        return ResponseEntity.ok(lista);
    }


    @Operation(
            summary = "Buscar vehículos por capacidad de volumen",
            description = "Devuelve una lista de vehículos que soportan una capacidad de volumen mínima requerida.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Búsqueda exitosa",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = VehiculoDTO.class))
                    )
            }
    )
    @GetMapping("/buscar/por-volumen")
    public ResponseEntity<List<VehiculoDTO>> buscarPorVolumen(
            @RequestParam("volumenMinimo") Double volumen) {

        List<VehiculoDTO> lista = vehiculoService.buscarVehiculosPorVolumen(volumen);
        return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Listar todos los vehículos",
            description = "Devuelve el inventario completo de vehículos registrados en el sistema.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado recuperado exitosamente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    // Corrección: ArraySchema debe apuntar a VehiculoDTO, no a ClassLoader
                                    array = @ArraySchema(schema = @Schema(implementation = VehiculoDTO.class))
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<VehiculoDTO>> listar() {
        log.info("Solicitando listado completo de vehículos...");
        List<VehiculoDTO> lista = vehiculoService.listarVehiculos();
        return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Obtener detalle de un vehículo",
            description = "Busca un vehículo específico por su patente única."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vehículo encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehiculoDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró ningún vehículo con esa patente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MensajeError.class)) // Importante: Usa tu clase de error
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MensajeError.class))
            )
    })
    @GetMapping("/patente/{patente}")
    public ResponseEntity<VehiculoDTO> buscarPorPatente(
            @Parameter(description = "Patente del vehículo (sin guiones ni espacios)", example = "AA123BB")
            @PathVariable("patente") String patente) {

        VehiculoDTO vehiculoDTO = vehiculoService.buscarVehiculoPorPatente(patente);
        return ResponseEntity.ok(vehiculoDTO);
    }
}