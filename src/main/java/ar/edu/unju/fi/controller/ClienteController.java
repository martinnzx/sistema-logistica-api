package ar.edu.unju.fi.controller;

import ar.edu.unju.fi.controller.dto.MensajeError;
import ar.edu.unju.fi.dto.ClienteDTO;
import ar.edu.unju.fi.service.ClienteService;
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
 * Controlador REST para manejar operaciones sobre clientes.
 * Expone los endpoints de la API para crear y consultar clientes.
 */
@Slf4j
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "clientes", description = "Operaciones sobre Clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @Operation(
            summary = "Crear un nuevo Cliente",
            description = "Permite registrar un nuevo cliente en el sistema.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Cliente creado correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ClienteDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error de validación en los datos ingresados",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        log.info("Recibida petición para crear cliente: {}", clienteDTO.getNombreRazonSocial());
        ClienteDTO nuevoCliente = clienteService.crearCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    @Operation(
            summary = "Buscar Cliente por documento o CUIT",
            description = "Devuelve los datos del cliente correspondiente al documento o CUIT ingresado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ClienteDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente no encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class))
                    )
            }
    )
    @GetMapping("/{documentoOCuit}")
    public ResponseEntity<ClienteDTO> buscarPorDocumentoOCuit(@PathVariable String documentoOCuit) {
        log.info("Buscando cliente con documento o CUIT: {}", documentoOCuit);
        ClienteDTO cliente = clienteService.buscarPorDocumentoOCuit(documentoOCuit);
        return ResponseEntity.ok(cliente);
    }
    @Operation(
            summary = "Actualizar un cliente",
            description = "Actualizacion de cliente, verifica si existe y lo persiste si todo esta correcto",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente actualizado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error de validación o CUIT/Documento no encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MensajeError.class))
                    )
            }
    )
    @PutMapping("/{documentoOCuit}")
    public ResponseEntity<ClienteDTO> actualizarCliente(@PathVariable String documentoOCuit,@Valid @RequestBody ClienteDTO clienteDTO) {
        log.info("Actualizando un cliente");
        ClienteDTO actualizado = clienteService.actualizarCliente(documentoOCuit,clienteDTO);
        return ResponseEntity.ok().body(actualizado);
    }

    @Operation(summary = "Listar clientes",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listado de clientes",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ClassLoader.class))))
            })
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listar() {
        log.info("Listando usuarios...");
        List<ClienteDTO> lista = clienteService.listarClientes();
        return ResponseEntity.ok(lista);
    }
}