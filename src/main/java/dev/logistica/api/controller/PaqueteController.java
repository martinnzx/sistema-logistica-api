package dev.logistica.api.controller;

import dev.logistica.api.controller.dto.MensajeError;
import dev.logistica.api.controller.dto.Error404;
import dev.logistica.api.dto.PaqueteDTO;
import dev.logistica.api.service.PaqueteService;

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
@Slf4j
@RestController
@RequestMapping("/api/paquetes")
@RequiredArgsConstructor
")
public class PaqueteController {

    private final PaqueteService paqueteService;

    // ===========================================================
    //                       CREAR PAQUETE
    // ===========================================================

    @PostMapping
    public ResponseEntity<PaqueteDTO> crearPaquete(@Valid @RequestBody PaqueteDTO paqueteDTO) {
        log.info("Creando paquete tipo: {}", paqueteDTO.getTipo());
        PaqueteDTO nuevo = paqueteService.crearPaquete(paqueteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // ===========================================================
    //                 LISTAR POR RANGO DE PESO
    // ===========================================================

    @GetMapping("/por-peso")
    public ResponseEntity<List<PaqueteDTO>> listarPorPeso(
            @RequestParam Double min,
            @RequestParam Double max) {

        log.info("Listando paquetes con peso entre {} kg y {} kg", min, max);
        List<PaqueteDTO> lista = paqueteService.listarPorPeso(min, max);
        return ResponseEntity.ok(lista);
    }

    // ===========================================================
    //                 LISTAR POR RANGO DE VOLUMEN
    // ===========================================================

    @GetMapping("/por-volumen")
    public ResponseEntity<List<PaqueteDTO>> listarPorVolumen(
            @RequestParam Double min,
            @RequestParam Double max) {

        log.info("Listando paquetes con volumen entre {} dm3 y {} dm3", min, max);
        List<PaqueteDTO> lista = paqueteService.listarPorVolumen(min, max);
        return ResponseEntity.ok(lista);
    }

    // ===========================================================
    //                      BUSCAR POR CÓDIGO
    // ===========================================================

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PaqueteDTO> buscarPaquetePorCodigo(@PathVariable String codigo) {
        log.info("Buscando paquete con código: {}", codigo);
        PaqueteDTO dto = paqueteService.buscarPaquetePorCodigo(codigo);
        return ResponseEntity.ok(dto);
    }

    // ===========================================================
    //                  LISTAR TODOS LOS PAQUETES
    // ===========================================================

    @GetMapping
    public ResponseEntity<List<PaqueteDTO>> listarPaquetes() {
        log.info("Solicitando listado completo de paquetes");
        List<PaqueteDTO> paquetes = paqueteService.listarPaquetes();
        return ResponseEntity.ok(paquetes);
    }
}
