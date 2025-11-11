package ar.edu.unju.fi.controller;

import ar.edu.unju.fi.dto.ClienteDTO;
import ar.edu.unju.fi.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejar operaciones sobre clientes.
 * Expone los endpoints de la API para crear y consultar clientes.
 */
@Slf4j
@RestController // Indica que esta clase maneja peticiones REST y devuelve JSON
@RequestMapping("/api/clientes") // URL base: todas las rutas comienzan con /api/clientes
@RequiredArgsConstructor // Inyección automatica del service por constructor
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Endpoint: POST /api/clientes
     * Crea un nuevo cliente a partir de los datos recibidos en el cuerpo de la peticion.
     */
    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        log.info("Recibida petición para crear cliente: {}", clienteDTO.getNombreRazonSocial());

        // Llama al servicio para crear el cliente
        ClienteDTO nuevoCliente = clienteService.crearCliente(clienteDTO);

        // Devuelve una respuesta 201 Created con el cliente creado
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    /**
     * Endpoint: GET /api/clientes/{documentoOCuit}
     * Busca un cliente por su documento o CUIT.
     */
    @GetMapping("/{documentoOCuit}")
    public ResponseEntity<ClienteDTO> buscarPorDocumentoOCuit(@PathVariable String documentoOCuit) {
        log.info("Buscando cliente con documento o CUIT: {}", documentoOCuit);

        ClienteDTO cliente = clienteService.buscarPorDocumentoOCuit(documentoOCuit);

        // Si no se encuentra, el service lanza una excepcion capturada por el GlobalExceptionHandler
        return ResponseEntity.ok(cliente);
    }
}