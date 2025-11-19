package ar.edu.unju.fi.controller;

import ar.edu.unju.fi.controller.dto.MensajeError;
import ar.edu.unju.fi.controller.dto.Error404;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.dto.views.*;
import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.model.HistorialEstadoEnvio;
import ar.edu.unju.fi.service.EnvioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
@Tag(name = "Envíos", description = "Operaciones relacionadas al ciclo de vida de los envíos")
public class EnvioController {

    private final EnvioService envioService;

    // ===========================================================
    //                      1. CREAR ENVÍO
    // ===========================================================

    @Operation(
            summary = "Crear un nuevo envío",
            description = "Permite crear un envío indicando remitente, destinatario y los paquetes asociados.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Envío creado correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EnvioDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos inválidos o remitente/destinatario inexistente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<EnvioDTO> crearEnvio(@RequestBody EnvioViewDTO dto) {
        log.info("Solicitud para crear un nuevo envío recibida");
        EnvioDTO nuevo = envioService.crearEnvio(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // ===========================================================
    //                2. LISTAR POR REMITENTE
    // ===========================================================

    @Operation(
            summary = "Listar envíos por remitente",
            description = "Devuelve todos los envíos realizados por un cliente, según su documento o CUIT.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado obtenido correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = EnvioViewRemitenteDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/remitente/{documento}")
    public ResponseEntity<List<EnvioViewRemitenteDTO>> listarPorRemitente(@PathVariable String documento) {
        log.info("Listando envíos del remitente con documento: {}", documento);
        List<EnvioViewRemitenteDTO> envios = envioService.listarPorRemitente(documento);
        return ResponseEntity.ok(envios);
    }

    // ===========================================================
    //               3. LISTAR POR DESTINATARIO
    // ===========================================================

    @Operation(
            summary = "Listar envíos por destinatario",
            description = "Devuelve todos los envíos recibidos por un cliente, según su documento o CUIT.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado obtenido correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = EnvioViewDestinatarioDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/destinatario/{documento}")
    public ResponseEntity<List<EnvioViewDestinatarioDTO>> listarPorDestinatario(@PathVariable String documento) {
        log.info("Listando envíos del destinatario con documento: {}", documento);
        List<EnvioViewDestinatarioDTO> envios = envioService.listarPorDestinatario(documento);
        return ResponseEntity.ok(envios);
    }

    // ===========================================================
    //                    4. LISTAR POR ESTADO
    // ===========================================================

    @Operation(
            summary = "Listar envíos por estado",
            description = "Filtra los envíos por su estado actual (GENERADO, EN_ALMACEN, EN_RUTA, ENTREGADO, etc).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado obtenido correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = EnvioViewEstadoDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<EnvioViewEstadoDTO>> listarPorEstado(@PathVariable EstadoEnvio estado) {
        log.info("Listando envíos por estado: {}", estado);
        List<EnvioViewEstadoDTO> envios = envioService.listarPorEstado(estado);
        return ResponseEntity.ok(envios);
    }

    // ===========================================================
    //                   5. AVANZAR ESTADO
    // ===========================================================

    @Operation(
            summary = "Avanzar el estado de un envío",
            description = "Avanza el estado del envío buscándolo por su código único.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Estado avanzado correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "No es posible avanzar el estado (validación o negocio)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Envío no encontrado con ese código",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Error404.class))
                    )
            }
    )
    @PutMapping("/{codigo}/avanzar")
    public ResponseEntity<MensajeError> avanzarEstado(
            @PathVariable String codigo,
            @RequestBody(required = false) EstadoEnvioDTO requestBody) {

        String observacion = (requestBody != null) ? requestBody.getObservacion() : "";

        envioService.avanzarEstado(codigo, observacion);

        return ResponseEntity.ok(new MensajeError("El estado del envío se avanzó correctamente."));
    }

    // ===========================================================
    //                    6. CANCELAR ENVÍO
    // ===========================================================

    @Operation(
            summary = "Cancelar un envío",
            description = "Cancela un envío buscándolo por su código único.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Envío cancelado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Envío no encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Error404.class)))
            }
    )
    @PutMapping("/{codigo}/cancelar")
    public ResponseEntity<MensajeError> cancelarEnvio(
            @PathVariable String codigo,
            @RequestBody(required = false) Map<String, String> body) {

        String observacion = (body != null) ? body.getOrDefault("observacion", "") : "";

        envioService.cancelarEnvio(codigo, observacion);

        return ResponseEntity.ok(new MensajeError("El envío fue cancelado correctamente."));
    }

    // ===========================================================
    //                    7. DEVOLVER ENVÍO
    // ===========================================================

    @Operation(
            summary = "Devolver un envío",
            description = "Marca el envío como devuelto buscándolo por su código único.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Envío devuelto correctamente"),
                    @ApiResponse(responseCode = "404", description = "Envío no encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Error404.class)))
            }
    )
    @PutMapping("/{codigo}/devolver")
    public ResponseEntity<MensajeError> devolverEnvio(
            @PathVariable String codigo,
            @RequestBody(required = false) Map<String, String> body) {

        String observacion = (body != null) ? body.getOrDefault("observacion", "") : "";

        envioService.devolverEnvio(codigo, observacion);

        return ResponseEntity.ok(new MensajeError("El envío fue devuelto correctamente."));
    }

    // ===========================================================
    //                8. ADJUNTAR COMPROBANTE
    // ===========================================================

    @Operation(
            summary = "Adjuntar comprobante de entrega",
            description = "Adjunta un comprobante al envío antes de ser entregado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Comprobante adjuntado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Estado inválido para adjuntar comprobante",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)))
            }
    )
    @PutMapping("/{id}/comprobante")
    public ResponseEntity<MensajeError> adjuntarComprobante(
            @PathVariable Long id,
            @RequestBody ComprobanteDTO request) {

        envioService.adjuntarComprobante(id, request);

        return ResponseEntity.ok(new MensajeError("Comprobante adjuntado correctamente al envío."));
    }

    // ===========================================================
    //                9. OBTENER ENVÍO POR CÓDIGO
    // ===========================================================

    @Operation(
            summary = "Obtener envío por código",
            description = "Busca un envío utilizando su código único asignado al crearlo.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Envío encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EnvioDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Envío no encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Error404.class))
                    )
            }
    )
    @GetMapping("/codigo/{codigoUnico}")
    public ResponseEntity<EnvioDTO> obtenerEnvioPorCodigo(@PathVariable String codigoUnico) {
        EnvioDTO envio = envioService.obtenerEnvioPorCodigo(codigoUnico);
        return ResponseEntity.ok(envio);
    }

    // ===========================================================
    //              10. OBTENER HISTORIAL POR CÓDIGO
    // ===========================================================

    @Operation(
            summary = "Consultar historial de estados por código",
            description = "Devuelve los cambios de estado registrados para el envío indicado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Historial obtenido correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = HistorialEstadoEnvio.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Envío no encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Error404.class))
                    )
            }
    )
    @GetMapping("/codigo/{codigoUnico}/historial")
    public ResponseEntity<List<HistorialEstadoEnvio>> obtenerHistorialPorCodigo(@PathVariable String codigoUnico) {
        List<HistorialEstadoEnvio> historial = envioService.obtenerHistorialPorCodigo(codigoUnico);
        return ResponseEntity.ok(historial);
    }
}