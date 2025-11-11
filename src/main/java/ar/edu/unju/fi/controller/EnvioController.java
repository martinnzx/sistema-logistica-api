package ar.edu.unju.fi.controller;

import ar.edu.unju.fi.controller.dto.MensajeError;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.model.HistorialEstadoEnvio;
import ar.edu.unju.fi.service.EnvioService;
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

    /* =======================================================
       1. CREAR ENVIO
    ======================================================= */
    @PostMapping
    public ResponseEntity<EnvioDTO> crearEnvio(@RequestBody EnvioDTO dto) {
        log.info("Solicitud para crear un nuevo envío recibida");
        EnvioDTO nuevo = envioService.crearEnvio(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    /* =======================================================
       2. LISTAR POR REMITENTE
    ======================================================= */
    @GetMapping("/remitente/{documento}")
    public ResponseEntity<List<EnvioDTO>> listarPorRemitente(@PathVariable String documento) {
        log.info("Listando envíos del remitente con documento: {}", documento);
        List<EnvioDTO> envios = envioService.listarPorRemitente(documento);
        return ResponseEntity.ok(envios);
    }

    /* =======================================================
       3. LISTAR POR DESTINATARIO
    ======================================================= */
    @GetMapping("/destinatario/{documento}")
    public ResponseEntity<List<EnvioDTO>> listarPorDestinatario(@PathVariable String documento) {
        log.info("Listando envíos del destinatario con documento: {}", documento);
        List<EnvioDTO> envios = envioService.listarPorDestinatario(documento);
        return ResponseEntity.ok(envios);
    }

    /* =======================================================
       4. LISTAR POR ESTADO
    ======================================================= */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<EnvioDTO>> listarPorEstado(@PathVariable EstadoEnvio estado) {
        log.info("Listando envios por estado: {}", estado);
        List<EnvioDTO> envios = envioService.listarPorEstado(estado);
        return ResponseEntity.ok(envios);
    }

    /* =======================================================
       5. AVANZAR ESTADO
    ======================================================= */
    @PutMapping("/{id}/avanzar")
    public ResponseEntity<MensajeError> avanzarEstado(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {

        String observacion = (body != null) ? body.getOrDefault("observacion", "") : "";
        envioService.avanzarEstado(id, observacion);
        return ResponseEntity.ok(new MensajeError("El estado del envio se avanzo correctamente."));
    }

    /* =======================================================
       6. CANCELAR ENVIO
    ======================================================= */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<MensajeError> cancelarEnvio(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {

        String observacion = (body != null) ? body.getOrDefault("observacion", "") : "";
        envioService.cancelarEnvio(id, observacion);
        return ResponseEntity.ok(new MensajeError("El envío fue cancelado correctamente."));
    }

    /* =======================================================
       7. DEVOLVER ENVIO
    ======================================================= */
    @PutMapping("/{id}/devolver")
    public ResponseEntity<MensajeError> devolverEnvio(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {

        String observacion = (body != null) ? body.getOrDefault("observacion", "") : "";
        envioService.devolverEnvio(id, observacion);
        return ResponseEntity.ok(new MensajeError("El envío fue devuelto correctamente."));
    }

    /* =======================================================
       8. ADJUNTAR COMPROBANTE
    ======================================================= */
    @PutMapping("/{id}/comprobante")
    public ResponseEntity<MensajeError> adjuntarComprobante(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String comprobante = body.get("comprobante");
        envioService.adjuntarComprobante(id, comprobante);
        return ResponseEntity.ok(new MensajeError("Comprobante adjuntado correctamente al envío."));
    }

    /* =======================================================
       9. OBTENER ENVIO POR CODIGO
    ======================================================= */

    @GetMapping("/codigo/{codigoUnico}")
    public ResponseEntity<?> obtenerEnvioPorCodigo(@PathVariable String codigoUnico) {
        EnvioDTO envio = envioService.obtenerEnvioPorCodigo(codigoUnico);
        return ResponseEntity.ok(envio);
    }

    /* =======================================================
       10. OBTENER HISTORIAL POR CODIGO
    ======================================================= */

    @GetMapping("/codigo/{codigoUnico}/historial")
    public ResponseEntity<?> obtenerHistorialPorCodigo(@PathVariable String codigoUnico) {
        List<HistorialEstadoEnvio> historial = envioService.obtenerHistorialPorCodigo(codigoUnico);
        return ResponseEntity.ok(historial);
    }
}