package ar.edu.unju.fi.controller;

import ar.edu.unju.fi.controller.dto.MensajeError;
import ar.edu.unju.fi.dto.VehiculoDTO;
import ar.edu.unju.fi.service.VehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
            summary = "Crear un Vehiculo",
            description = "Creando un Vehiculo, verificando patente y persistiendo/creando si correcponde",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Vehiculo creado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VehiculoDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error de validación o negocio",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MensajeError.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<VehiculoDTO> crearVehiculo(@Valid @RequestBody VehiculoDTO vehiculoDTO){
        log.info("Creando un vehiculo");
        VehiculoDTO nuevo = vehiculoService.crearVehiculo(vehiculoDTO);
        return ResponseEntity.created(URI.create("/api/vehiculos" + nuevo.getPatente())).body(nuevo);
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
}

