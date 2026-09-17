package dev.logistica.api.controller;

import dev.logistica.api.controller.dto.MensajeError;
import dev.logistica.api.controller.dto.Error404;
import dev.logistica.api.dto.EnvioDTO;
import dev.logistica.api.dto.views.*;
import dev.logistica.api.enums.EstadoEnvio;
import dev.logistica.api.model.HistorialEstadoEnvio;
import dev.logistica.api.service.EnvioService;

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
public class EnvioController {

    private final EnvioService envioService;

    // ===========================================================
    //                      1. CREAR ENVÍO
    // ===========================================================

    @PostMapping
    public ResponseEntity<EnvioDTO> crearEnvio(@RequestBody EnvioViewDTO dto) {
        log.info("Solicitud para crear un nuevo envío recibida");
        EnvioDTO nuevo = envioService.crearEnvio(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // ===========================================================
    //                2. LISTAR POR REMITENTE
    // ===========================================================

    @GetMapping("/remitente/{documento}")
    public ResponseEntity<List<EnvioViewRemitenteDTO>> listarPorRemitente(@PathVariable String documento) {
        log.info("Listando envíos del remitente con documento: {}", documento);
        List<EnvioViewRemitenteDTO> envios = envioService.listarPorRemitente(documento);
        return ResponseEntity.ok(envios);
    }

    // ===========================================================
    //               3. LISTAR POR DESTINATARIO
    // ===========================================================

    @GetMapping("/destinatario/{documento}")
    public ResponseEntity<List<EnvioViewDestinatarioDTO>> listarPorDestinatario(@PathVariable String documento) {
        log.info("Listando envíos del destinatario con documento: {}", documento);
        List<EnvioViewDestinatarioDTO> envios = envioService.listarPorDestinatario(documento);
        return ResponseEntity.ok(envios);
    }

    // ===========================================================
    //                    4. LISTAR POR ESTADO
    // ===========================================================

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<EnvioViewEstadoDTO>> listarPorEstado(@PathVariable EstadoEnvio estado) {
        log.info("Listando envíos por estado: {}", estado);
        List<EnvioViewEstadoDTO> envios = envioService.listarPorEstado(estado);
        return ResponseEntity.ok(envios);
    }

    // ===========================================================
    //                   5. AVANZAR ESTADO
    // ===========================================================

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

    @PutMapping("/{codigo}/comprobante")
    public ResponseEntity<MensajeError> adjuntarComprobante(
            @PathVariable String codigo,
            @RequestBody ComprobanteDTO request) {

        envioService.adjuntarComprobante(codigo, request);

        return ResponseEntity.ok(new MensajeError("Comprobante adjuntado correctamente al envío."));
    }

    // ===========================================================
    //                9. OBTENER ENVÍO POR CÓDIGO
    // ===========================================================

    @GetMapping("/codigo/{codigoUnico}")
    public ResponseEntity<EnvioDTO> obtenerEnvioPorCodigo(@PathVariable String codigoUnico) {
        EnvioDTO envio = envioService.obtenerEnvioPorCodigo(codigoUnico);
        return ResponseEntity.ok(envio);
    }

    // ===========================================================
    //              10. OBTENER HISTORIAL POR CÓDIGO
    // ===========================================================

    @GetMapping("/codigo/{codigoUnico}/historial")
    public ResponseEntity<List<HistorialEstadoEnvio>> obtenerHistorialPorCodigo(@PathVariable String codigoUnico) {
        List<HistorialEstadoEnvio> historial = envioService.obtenerHistorialPorCodigo(codigoUnico);
        return ResponseEntity.ok(historial);
    }
}
