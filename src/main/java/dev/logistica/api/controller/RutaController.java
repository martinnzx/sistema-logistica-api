package dev.logistica.api.controller;

import dev.logistica.api.controller.dto.MensajeError;
import dev.logistica.api.controller.dto.Error404;
import dev.logistica.api.dto.RutaDTO;
import dev.logistica.api.dto.views.RutaViewDTO;
import dev.logistica.api.service.RutaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaService rutaService;

    // ===========================================================
    //                     CREAR RUTA NUEVA
    // ===========================================================

    @PostMapping
    public ResponseEntity<RutaDTO> crearRuta(@Valid @RequestBody RutaViewDTO viewDTO) {

        log.info("Solicitud para crear ruta recibida. Patente del vehículo: {}", viewDTO.getPatenteVehiculo());

        RutaDTO nueva = rutaService.crearRuta(viewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    // ===========================================================
    //           CONSULTAR ENVÍOS DE UNA RUTA EN UNA FECHA
    // ===========================================================

    @GetMapping("/{id}/envios")
    public ResponseEntity<RutaDTO> obtenerEnviosPorRutaYFecha(
            @PathVariable Long id,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        log.info("Consultando envíos de la ruta {} en fecha {}", id, fecha);

        List<RutaDTO> resultado = rutaService.obtenerEnviosPorRutaYFecha(id, fecha);

        if (resultado.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resultado.get(0));
    }
    // ===========================================================
    //                LISTAR TODAS LAS RUTAS
    // ===========================================================

    @GetMapping
    public ResponseEntity<List<RutaDTO>> listarTodas() {
        log.info("Solicitud para listar todas las rutas");

        List<RutaDTO> rutas = rutaService.listarTodas();

        if (rutas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(rutas);
    }

    // ===========================================================
    //                 CONSULTAR RUTA POR ID
    // ===========================================================

    @GetMapping("/{id}")
    public ResponseEntity<RutaDTO> obtenerPorId(@PathVariable Long id) {
        log.info("Solicitud para obtener la ruta con ID: {}", id);

        RutaDTO ruta = rutaService.obtenerPorId(id);

        return ResponseEntity.ok(ruta);
    }
}

