package dev.logistica.api.controller;

import dev.logistica.api.controller.dto.MensajeError;
import dev.logistica.api.controller.dto.Error404;
import dev.logistica.api.dto.ClienteDTO;
import dev.logistica.api.service.ClienteService;
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
@Slf4j
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // ===========================================================
    //                     CREAR CLIENTE
    // ===========================================================

        @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        log.info("Petición para crear cliente: {}", clienteDTO.getNombreRazonSocial());
        ClienteDTO nuevoCliente = clienteService.crearCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    // ===========================================================
    //             BUSCAR CLIENTE POR DOCUMENTO/CUIT
    // ===========================================================

        @GetMapping("/{documentoOCuit}")
    public ResponseEntity<ClienteDTO> buscarPorDocumentoOCuit(@PathVariable String documentoOCuit) {
        log.info("Buscando cliente con documento/CUIT: {}", documentoOCuit);
        ClienteDTO cliente = clienteService.buscarPorDocumentoOCuit(documentoOCuit);
        return ResponseEntity.ok(cliente);
    }

    // ===========================================================
    //                    ACTUALIZAR CLIENTE
    // ===========================================================

        @PutMapping("/{documentoOCuit}")
    public ResponseEntity<ClienteDTO> actualizarCliente(
            @PathVariable String documentoOCuit,
            @Valid @RequestBody ClienteDTO clienteDTO) {

        log.info("Actualizando cliente con documento/CUIT: {}", documentoOCuit);
        ClienteDTO actualizado = clienteService.actualizarCliente(documentoOCuit, clienteDTO);
        return ResponseEntity.ok().body(actualizado);
    }

    // ===========================================================
    //                    LISTAR CLIENTES
    // ===========================================================

        @GetMapping
    public ResponseEntity<List<ClienteDTO>> listar() {
        log.info("Listando clientes...");
        List<ClienteDTO> lista = clienteService.listarClientes();
        return ResponseEntity.ok(lista);
    }
}
