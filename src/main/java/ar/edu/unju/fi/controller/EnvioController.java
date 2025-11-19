package ar.edu.unju.fi.controller;

import ar.edu.unju.fi.controller.dto.MensajeError;
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
@Tag(name = "envíos", description = "Operaciones sobre Envíos y su ciclo de vida")
public class EnvioController {
    private final EnvioService envioService;

    /* =======================================================
       1. CREAR ENVÍO
    ======================================================= */
    @Operation(
            summary = "Crear un nuevo Envío",
            description = "Permite crear un nuevo envío con remitente, destinatario y lista de paquetes.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Envío creado exitosamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EnvioDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o remitente/destinatario inexistente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)))
            }
    )
    @PostMapping
    public ResponseEntity<EnvioDTO> crearEnvio(@RequestBody EnvioViewDTO dto) {
        log.info("Solicitud para crear un nuevo envío recibida");
        EnvioDTO nuevo = envioService.crearEnvio(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    /* =======================================================
       2. LISTAR POR REMITENTE
    ======================================================= */
    @Operation(
            summary = "Listar envíos por remitente",
            description = "Devuelve todos los envíos realizados por un cliente según su documento o CUIT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de envíos obtenida correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = EnvioViewRemitenteDTO.class)))
            }
    )
    @GetMapping("/remitente/{documento}")
    public ResponseEntity<List<EnvioViewRemitenteDTO>> listarPorRemitente(@PathVariable String documento) {
        log.info("Listando envíos del remitente con documento: {}", documento);
        List<EnvioViewRemitenteDTO> envios = envioService.listarPorRemitente(documento);
        return ResponseEntity.ok(envios);
    }

    /* =======================================================
       3. LISTAR POR DESTINATARIO
    ======================================================= */
    @Operation(
            summary = "Listar envíos por destinatario",
            description = "Devuelve todos los envíos recibidos por un cliente según su documento o CUIT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de envíos obtenida correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = EnvioViewDestinatarioDTO.class)))
            }
    )
    @GetMapping("/destinatario/{documento}")
    public ResponseEntity<List<EnvioViewDestinatarioDTO>> listarPorDestinatario(@PathVariable String documento) {
        log.info("Listando envíos del destinatario con documento: {}", documento);
        List<EnvioViewDestinatarioDTO> envios = envioService.listarPorDestinatario(documento);
        return ResponseEntity.ok(envios);
    }

    /* =======================================================
       4. LISTAR POR ESTADO
    ======================================================= */
    @Operation(
            summary = "Listar envíos por estado",
            description = "Filtra los envíos según su estado actual (GENERADO, EN_ALMACEN, EN_RUTA, ENTREGADO, etc).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de envíos obtenida correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = EnvioViewEstadoDTO.class)))
            }
    )
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<EnvioViewEstadoDTO>> listarPorEstado(@PathVariable EstadoEnvio estado) {
        log.info("Listando envíos por estado: {}", estado);
        List<EnvioViewEstadoDTO> envios = envioService.listarPorEstado(estado);
        return ResponseEntity.ok(envios);
    }

    /* =======================================================
       5. AVANZAR ESTADO
    ======================================================= */
    @Operation(
            summary = "Avanzar el estado de un envío por CÓDIGO", // <-- Actualizado
            description = "Avanza el estado del envío (buscado por su código único) según la lógica del patrón State.", // <-- Actualizado
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estado avanzado correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class))),
                    @ApiResponse(responseCode = "404", description = "Envío no encontrado con ese código", // <-- Añadido
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class))),
                    @ApiResponse(responseCode = "400", description = "Error al avanzar estado (por ejemplo, estado no válido)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)))
            }
    )
    @PutMapping("/{codigo}/avanzar")
    public ResponseEntity<MensajeError> avanzarEstado(
            @PathVariable String codigo,
            @RequestBody(required = false) Map<String, String> body) {

        String observacion = (body != null) ? body.getOrDefault("observacion", "") : "";

        // 3. CAMBIO EN LA LLAMADA AL SERVICIO: de id a codigo
        envioService.avanzarEstado(codigo, observacion);

        return ResponseEntity.ok(new MensajeError("El estado del envío se avanzó correctamente."));
    }

    /* =======================================================
       6. CANCELAR ENVÍO
    ======================================================= */
    @Operation(
            summary = "Cancelar un envío por CÓDIGO", // <-- Actualizado
            description = "Cambia el estado del envío (buscado por código único) a CANCELADO.", // <-- Actualizado
            responses = {
                    @ApiResponse(responseCode = "200", description = "Envío cancelado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Envío no encontrado con ese código",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)))
            }
    )
// 1. CAMBIO EN LA RUTA
    @PutMapping("/{codigo}/cancelar")
    public ResponseEntity<MensajeError> cancelarEnvio(
            @PathVariable String codigo,
            @RequestBody(required = false) Map<String, String> body) {

        String observacion = (body != null) ? body.getOrDefault("observacion", "") : "";
        envioService.cancelarEnvio(codigo, observacion);

        return ResponseEntity.ok(new MensajeError("El envío fue cancelado correctamente."));
    }
    /* =======================================================
       7. DEVOLVER ENVÍO
    ======================================================= */
    @Operation(
            summary = "Devolver un envío por CÓDIGO", // <-- Actualizado
            description = "Marca el envío (buscado por código único) como devuelto.", // <-- Actualizado
            responses = {
                    @ApiResponse(responseCode = "200", description = "Envío devuelto correctamente"),
                    @ApiResponse(responseCode = "404", description = "Envío no encontrado con ese código",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)))
            }
    )
// 1. CAMBIO EN LA RUTA
    @PutMapping("/{codigo}/devolver")
    public ResponseEntity<MensajeError> devolverEnvio(
            @PathVariable String codigo,
            @RequestBody(required = false) Map<String, String> body) {

        String observacion = (body != null) ? body.getOrDefault("observacion", "") : "";

        envioService.devolverEnvio(codigo, observacion);

        return ResponseEntity.ok(new MensajeError("El envío fue devuelto correctamente."));
    }
    /* =======================================================
       8. ADJUNTAR COMPROBANTE
    ======================================================= */
    @Operation(
            summary = "Adjuntar comprobante de entrega",
            description = "Adjunta un comprobante al envío antes de ser entregado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Comprobante adjuntado correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class))),
                    @ApiResponse(responseCode = "400", description = "No se puede adjuntar comprobante en estados inválidos",
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

    /* =======================================================
       9. OBTENER ENVÍO POR CÓDIGO
    ======================================================= */
    @Operation(
            summary = "Obtener envío por código único",
            description = "Busca un envío por su código único (UUID generado al crearlo).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Envío encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EnvioDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Envío no encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)))
            }
    )
    @GetMapping("/codigo/{codigoUnico}")
    public ResponseEntity<?> obtenerEnvioPorCodigo(@PathVariable String codigoUnico) {
        EnvioDTO envio = envioService.obtenerEnvioPorCodigo(codigoUnico);
        return ResponseEntity.ok(envio);
    }

    /* =======================================================
       10. OBTENER HISTORIAL POR CÓDIGO
    ======================================================= */
    @Operation(
            summary = "Consultar historial de estados por código de envío",
            description = "Devuelve todos los cambios de estado registrados para un envío determinado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = HistorialEstadoEnvio.class))),
                    @ApiResponse(responseCode = "404", description = "Envío no encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeError.class)))
            }
    )
    @GetMapping("/codigo/{codigoUnico}/historial")
    public ResponseEntity<?> obtenerHistorialPorCodigo(@PathVariable String codigoUnico) {
        List<HistorialEstadoEnvio> historial = envioService.obtenerHistorialPorCodigo(codigoUnico);
        return ResponseEntity.ok(historial);
    }
}