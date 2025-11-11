package ar.edu.unju.fi.controller;

import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.service.PaqueteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones sobre paquetes.
 * Permite crear y listar paquetes por peso o volumen.
 */
@Slf4j
@RestController
@RequestMapping("/api/paquetes")
@RequiredArgsConstructor
public class PaqueteController {

    private final PaqueteService paqueteService;

    /**
     * Endpoint: POST /api/paquetes
     * Crea un nuevo paquete (fragil o refrigerado).
     */
    @PostMapping
    public ResponseEntity<PaqueteDTO> crearPaquete(@Valid @RequestBody PaqueteDTO paqueteDTO) {
        log.info("Creando paquete tipo: {}", paqueteDTO.getTipo());

        PaqueteDTO nuevo = paqueteService.crearPaquete(paqueteDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    /**
     * Endpoint: GET /api/paquetes/por-peso
     * Lista los paquetes cuyo peso esté dentro del rango indicado.
     * Ejemplo: /api/paquetes/por-peso?min=5&max=20
     */
    @GetMapping("/por-peso")
    public ResponseEntity<List<PaqueteDTO>> listarPorPeso(@RequestParam Double min, @RequestParam Double max) {
        log.info("Listando paquetes con peso entre {} kg y {} kg", min, max);

        List<PaqueteDTO> lista = paqueteService.listarPorPeso(min, max);

        return ResponseEntity.ok(lista);
    }

    /**
     * Endpoint: GET /api/paquetes/por-volumen
     * Lista los paquetes cuyo volumen este dentro del rango indicado.
     * Ejemplo: /api/paquetes/por-volumen?min=100&max=500
     */
    @GetMapping("/por-volumen")
    public ResponseEntity<List<PaqueteDTO>> listarPorVolumen(@RequestParam Double min, @RequestParam Double max) {
        log.info("Listando paquetes con volumen entre {} dm3 y {} dm3", min, max);

        List<PaqueteDTO> lista = paqueteService.listarPorVolumen(min, max);

        return ResponseEntity.ok(lista);
    }
}