package dev.logistica.api.controller.doc;

import dev.logistica.api.controller.dto.MensajeError;
import dev.logistica.api.controller.dto.Error404;
import dev.logistica.api.dto.ClienteDTO;
import dev.logistica.api.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
 * Controlador encargado de manejar las operaciones básicas relacionadas con los Clientes.
 * Permite crear, consultar, actualizar y listar clientes.
 */
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Endpoints para gestionar clientes del sistema")
public interface ClienteApi {


    // ===========================================================
    //                     CREAR CLIENTE
    // ===========================================================

    @Operation(
            summary = "Crear un nuevo cliente",
            description = "Recibe los datos del cliente y lo registra en el sistema si es válido.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Cliente creado correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ClienteDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error en la validación de los datos enviados",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)
                            )
                    )
            }
    )
    @PostMapping
public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO);

    // ===========================================================
    //             BUSCAR CLIENTE POR DOCUMENTO/CUIT
    // ===========================================================

    @Operation(
            summary = "Buscar cliente por documento o CUIT",
            description = "Busca un cliente según el valor ingresado (DNI o CUIT).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ClienteDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No existe un cliente con el documento o CUIT indicado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error404.class)
                            )
                    )
            }
    )
    @GetMapping("/{documentoOCuit}")
public ResponseEntity<ClienteDTO> buscarPorDocumentoOCuit(@PathVariable String documentoOCuit);

    // ===========================================================
    //                    ACTUALIZAR CLIENTE
    // ===========================================================

    @Operation(
            summary = "Actualizar un cliente",
            description = "Permite actualizar los datos de un cliente existente.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente actualizado correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ClienteDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos inválidos o cliente no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)
                            )
                    )
            }
    )
    @PutMapping("/{documentoOCuit}")
public ResponseEntity<ClienteDTO> actualizarCliente(
            @PathVariable String documentoOCuit,
@Valid @RequestBody ClienteDTO clienteDTO);

    // ===========================================================
    //                    LISTAR CLIENTES
    // ===========================================================

    @Operation(
            summary = "Listar todos los clientes",
            description = "Devuelve un listado con todos los clientes registrados en el sistema.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado obtenido correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = ClienteDTO.class)
                                    )
                            )
                    )
            }
    )
    @GetMapping
public ResponseEntity<List<ClienteDTO>> listar();
}
