package dev.logistica.api.controller;

import dev.logistica.api.controller.dto.MensajeError;
import dev.logistica.api.dto.VehiculoDTO;
import dev.logistica.api.service.VehiculoService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/vehiculos")
public class VehiculoController {
    private final VehiculoService vehiculoService;
    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @PostMapping
    public ResponseEntity<VehiculoDTO> crearVehiculo(@Valid @RequestBody VehiculoDTO vehiculoDTO) {
        log.info("Iniciando creación de vehículo con patente: {}", vehiculoDTO.getPatente());

        VehiculoDTO nuevo = vehiculoService.crearVehiculo(vehiculoDTO);
        URI location = URI.create(String.format("/api/vehiculos/%s", nuevo.getPatente()));

        return ResponseEntity.created(location).body(nuevo);
    }

    @GetMapping("/buscar/refrigerados")
    public ResponseEntity<List<VehiculoDTO>> buscarPorRefrigeracion(
            @RequestParam("refrigerado") Boolean refrigerado) {

        List<VehiculoDTO> lista = vehiculoService.buscarVehiculosRefrigerados(refrigerado);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/buscar/por-peso")
    public ResponseEntity<List<VehiculoDTO>> buscarPorPeso(
            @RequestParam("pesoMinimo") Double pesoRequerido) {

        List<VehiculoDTO> lista = vehiculoService.buscarVehiculosPorPeso(pesoRequerido);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/buscar/por-volumen")
    public ResponseEntity<List<VehiculoDTO>> buscarPorVolumen(
            @RequestParam("volumenMinimo") Double volumen) {

        List<VehiculoDTO> lista = vehiculoService.buscarVehiculosPorVolumen(volumen);
        return ResponseEntity.ok(lista);
    }

    @GetMapping
    public ResponseEntity<List<VehiculoDTO>> listar() {
        log.info("Solicitando listado completo de vehículos...");
        List<VehiculoDTO> lista = vehiculoService.listarVehiculos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/patente/{patente}")
    public ResponseEntity<VehiculoDTO> buscarPorPatente(
            ", example = "AA123BB")
            @PathVariable("patente") String patente) {

        VehiculoDTO vehiculoDTO = vehiculoService.buscarVehiculoPorPatente(patente);
        return ResponseEntity.ok(vehiculoDTO);
    }
}
