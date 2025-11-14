package ar.edu.unju.fi.service;

import ar.edu.unju.fi.dto.views.EnvioViewDTO;
import ar.edu.unju.fi.dto.views.EnvioViewDestinatarioDTO;
import ar.edu.unju.fi.dto.views.EnvioViewEstadoDTO;
import ar.edu.unju.fi.dto.views.EnvioViewRemitenteDTO;
import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.exceptions.ResourceNotFoundException;
import ar.edu.unju.fi.mapper.viewsMapper.EnvioViewDestinatarioMapper;
import ar.edu.unju.fi.mapper.viewsMapper.EnvioViewEstadoMapper;
import ar.edu.unju.fi.mapper.viewsMapper.EnvioViewMapper;
import ar.edu.unju.fi.mapper.viewsMapper.EnvioViewRemitenteMapper;
import ar.edu.unju.fi.repository.ClienteRepository;
import ar.edu.unju.fi.repository.EnvioRepository;
import ar.edu.unju.fi.repository.HistorialEstadoEnvioRepository;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.mapper.EnvioMapper;
import ar.edu.unju.fi.model.*;
import ar.edu.unju.fi.repository.PaqueteRepository;
import ar.edu.unju.fi.state.EstadoEnvioFactory;
import ar.edu.unju.fi.state.EstadoEnvioState;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EnvioService {
    private final EnvioRepository envioRepository;
    private final HistorialEstadoEnvioRepository historialEstadoEnvioRepository;
    private final ClienteRepository clienteRepository;
    private final PaqueteRepository paqueteRepository;

    public EnvioService(EnvioRepository envioRepository,
                        HistorialEstadoEnvioRepository historialEstadoEnvioRepository,
                        ClienteRepository clienteRepository,PaqueteRepository paqueteRepository) {
        this.envioRepository = envioRepository;
        this.historialEstadoEnvioRepository = historialEstadoEnvioRepository;
        this.clienteRepository = clienteRepository;
        this.paqueteRepository = paqueteRepository;
    }

    /* =====================
       CREAR ENVÍO
       ===================== */
    @Transactional
    public EnvioDTO crearEnvio(@Valid EnvioViewDTO viewDTO) {
        log.info("Iniciando creación de envío desde ViewDTO...");
        // 1. Ensamblaje y Validaciones PRE-PERSISTENCIA
        Envio envio = ensamblarEnvio(viewDTO); // Ya llama a inicializarEnvio internamente
        envio.setCodigoUnico("TEMP-001");
        validarPaquetes(envio);
        detectarRefrigerado(envio);

        // 2. Persistir el Envío (aquí se genera el ID)
        Envio guardado = envioRepository.save(envio);

        // 3. Generar el código único (usando el ID) y guardar de nuevo
        String codigo = generarCodigoUnico(guardado);
        guardado.setCodigoUnico(codigo);
        guardado = envioRepository.save(guardado); // Guardamos el código único

        // 4. Registrar Historial y Mapear a DTO
        registrarHistorialInicial(guardado); // HACER ESTO DESPUÉS DE OBTENER EL ID

        log.info("Envío creado exitosamente con código: {}", codigo);
        return EnvioMapper.toDTO(guardado);
    }
    /* =====================
       BÚSQUEDAS
       ===================== */
    public List<EnvioViewRemitenteDTO> listarPorRemitente(String documentoOCuit) {
        log.info("Buscando envíos del remitente con documento/CUIT: {}", documentoOCuit);

        List<EnvioViewRemitenteDTO> envios = envioRepository.findByRemitente_DocumentoOCuitIgnoreCase(documentoOCuit)
                .stream()
                .map(EnvioViewRemitenteMapper::toDTO)
                .toList();

        if (envios.isEmpty()) {
            log.warn("No se encontraron envíos para el remitente con documento/CUIT: {}", documentoOCuit);
            throw new ResourceNotFoundException(
                    "Envíos",
                    "documento/CUIT",
                    documentoOCuit
            );
        }
        log.info("Se encontraron envíos para el remitente con documento/CUIT: {}", documentoOCuit);
        return envios;
    }

    public List<EnvioViewDestinatarioDTO> listarPorDestinatario(String documentoOCuit) {
        log.info("Buscando envíos del destinatario con documento/CUIT: {}", documentoOCuit);

        List<EnvioViewDestinatarioDTO> envios = envioRepository.findByDestinatario_DocumentoOCuitIgnoreCase(documentoOCuit)
                .stream().map(EnvioViewDestinatarioMapper::toDTO).toList();

        if (envios.isEmpty()) {
            log.warn("No se encontraron envíos para el destinatario con documento/CUIT: {}", documentoOCuit);
            throw new ResourceNotFoundException(
                    "Envíos",
                    "documento/CUIT",
                    documentoOCuit
            );
        } else {
            log.info("Se encontraron {} envíos para el destinatario con documento/CUIT: {}", envios.size(), documentoOCuit);
        }

        return envios;
    }

    public List<EnvioViewEstadoDTO> listarPorEstado(EstadoEnvio estado) {
        log.info("Listando envíos con estado: {}", estado);

        List<EnvioViewEstadoDTO> envios = envioRepository.findByEstado(estado)
                .stream().map(EnvioViewEstadoMapper::toDTO).toList();

        if (envios.isEmpty()) {
            log.warn("No se encontraron envíos con el estado: {}", estado);
            throw new ResourceNotFoundException(
                    "Envíos",
                    "Estado",
                    estado.toString()
            );
        } else {
            log.info("Se encontraron {} envíos con el estado: {}", envios.size(), estado);
        }
        return envios;
    }

    @Transactional(readOnly = true)
    public EnvioDTO obtenerEnvioPorCodigo(String codigoUnico) {
        log.info("Buscando envío con código único: {}", codigoUnico);

        Envio envio = envioRepository.findByCodigoUnico(codigoUnico)
                .orElseThrow(() -> {
                    log.error("No se encontro un envio con el codigo: {}", codigoUnico);
                    return new IllegalArgumentException("No se encontró un envío con el código: " + codigoUnico);
                });

        log.info("Envío encontrado: código={}, estado={}", envio.getCodigoUnico(), envio.getEstado());
        return EnvioMapper.toDTO(envio);
    }

    @Transactional(readOnly = true)
    public List<HistorialEstadoEnvio> obtenerHistorialPorCodigo(String codigoUnico) {
        log.info("Buscando historial de estados para el envío con código: {}", codigoUnico);

        Envio envio = envioRepository.findByCodigoUnico(codigoUnico)
                .orElseThrow(() -> {
                    log.error("No se encontro un envio con el codigo: {}", codigoUnico);
                    return new IllegalArgumentException("No se encontró un envío con el código: " + codigoUnico);
                });

        List<HistorialEstadoEnvio> historial = historialEstadoEnvioRepository.findByEnvio(envio);

        if (historial.isEmpty()) {
            log.warn("El envío con código {} no tiene historial registrado.", codigoUnico);
        } else {
            log.info("Historial recuperado: {} registros encontrados para el envío con código {}", historial.size(), codigoUnico);
        }

        return historial;
    }

    /* =====================
       CAMBIO DE ESTADO (Patrón STATE)
       ===================== */

    @Transactional
    public void avanzarEstado(String codigo, String observacion) {
        Envio envio = envioRepository.findByCodigoUnico(codigo)
                .orElseThrow(() -> new ResourceNotFoundException(codigo));
        EstadoEnvioState estado = EstadoEnvioFactory.getEstado(envio.getEstado());
        estado.avanzar(envio, historialEstadoEnvioRepository, observacion);
        envioRepository.save(envio);
        log.info("Envío {} avanzó correctamente al estado {}", envio.getCodigoUnico(), envio.getEstado());
    }

    @Transactional
    public void cancelarEnvio(String codigo, String observacion) {
        Envio envio = envioRepository.findByCodigoUnico(codigo)
                .orElseThrow(() -> new ResourceNotFoundException(codigo));
        EstadoEnvioState estado = EstadoEnvioFactory.getEstado(envio.getEstado());
        estado.cancelar(envio, historialEstadoEnvioRepository, observacion);
        envioRepository.save(envio);
        log.info("Envío {} fue cancelado. Estado final: {}", envio.getCodigoUnico(), envio.getEstado());
    }

    @Transactional
    public void devolverEnvio(String codigo, String observacion) {
        Envio envio = envioRepository.findByCodigoUnico(codigo)
                .orElseThrow(() -> new ResourceNotFoundException(codigo));
        EstadoEnvioState estado = EstadoEnvioFactory.getEstado(envio.getEstado());
        estado.devolver(envio, historialEstadoEnvioRepository, observacion);
        envioRepository.save(envio);
        log.info("Envío {} fue devuelto. Estado final: {}", envio.getCodigoUnico(), envio.getEstado());
    }

    @Transactional
    public void adjuntarComprobante(Long envioId, String comprobante) {
        Envio envio = obtenerEnvioPorId(envioId);

        if (comprobante == null || comprobante.isBlank()) {
            throw new IllegalArgumentException("El comprobante no puede estar vacío.");
        }

        if (envio.getEstado() == EstadoEnvio.ENTREGADO || envio.getEstado() == EstadoEnvio.CANCELADO) {
            throw new IllegalStateException("No se puede adjuntar comprobante a un envío entregado o cancelado.");
        }

        envio.setComprobanteEntrega(comprobante);
        envioRepository.save(envio);

        log.info("Comprobante adjuntado correctamente al envío {}", envio.getCodigoUnico());
    }

    /* =====================
       VALIDACIONES Y UTILIDADES
       ===================== */

    private Envio obtenerEnvioPorId(Long id) {
        return envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado con ID: " + id));
    }

    private void validarPaquetes(Envio envio) {
        for (Paquete p : envio.getPaquetes()) {
            if (p.getPesoKg() == null || p.getPesoKg() <= 0)
                throw new IllegalArgumentException("Peso inválido en paquete " + p.getCodigo());
            if (p.getVolumenDm3() == null || p.getVolumenDm3() <= 0)
                throw new IllegalArgumentException("Volumen inválido en paquete " + p.getCodigo());
        }
        log.debug("Validando paquetes...");
    }

    private void detectarRefrigerado(Envio envio) {
        boolean requiereFrio = envio.getPaquetes().stream().anyMatch(PaqueteRefrigerado.class::isInstance);
        envio.setRequiereFrio(requiereFrio);
        log.debug("Detectando si es refrigerado...");
    }

    private void inicializarEnvio(Envio envio) {
        envio.setEstado(EstadoEnvio.GENERADO);
        log.debug("Inicializando estado del envío...");
    }

    private void registrarHistorialInicial(Envio envio) {
        historialEstadoEnvioRepository.save(HistorialEstadoEnvio.builder()
                .envio(envio)
                .estadoAnterior(null)
                .estadoNuevo(EstadoEnvio.GENERADO)
                .fechaHora(LocalDateTime.now())
                .observacion("Envío creado")
                .build());

    }
    private Cliente buscarCliente(String documentoOCuit, String rol) {
        return clienteRepository.findByDocumentoOCuitIgnoreCase(documentoOCuit) // ⬅️ CORREGIDO
                .orElseThrow(() -> new IllegalArgumentException(
                        rol + " no encontrado con documento o CUIT: " + documentoOCuit));
    }

    private List<Paquete> buscarPaquetes(List<String> codigos) {
        List<Paquete> paquetesEncontrados = paqueteRepository.findByCodigoIn(codigos);
        if (paquetesEncontrados.size() != codigos.size()) {

            // (Mejora opcional para un mensaje de error más claro)
            // Buscamos cuáles son los códigos que sí encontramos
            List<String> codigosEncontrados = paquetesEncontrados.stream().map(Paquete::getCodigo).toList();

            // Comparamos con la lista original para ver cuáles faltan
            List<String> codigosFaltantes = codigos.stream()
                    .filter(c -> !codigosEncontrados.contains(c))
                    .toList();

            throw new IllegalArgumentException("Los siguientes códigos de paquete no se encontraron: " + String.join(", ", codigosFaltantes));
        }

        // --- Verificación 2: Que los paquetes no estén ya asignados (¡La corrección!) ---
        for (Paquete p : paquetesEncontrados) {

            // Usamos el método que acabamos de agregar al EnvioRepository
            if (envioRepository.existsByPaquetes(p)) {

                // Si 'exists' es true, el paquete ya está en la tabla envios_paquetes
                throw new IllegalArgumentException("El paquete con código '" + p.getCodigo() + "' ya se encuentra asignado a otro envío.");
            }
        }

        // Si pasa ambas verificaciones, la lista es válida
        return paquetesEncontrados;
    }
    private Envio ensamblarEnvio(EnvioViewDTO dto) {
        Envio envio = EnvioViewMapper.toEntity(dto);

        // 2. Buscar y asignar Remitente y Destinatario
        Cliente remitente = buscarCliente(dto.getCuilRemitente(), "Remitente");
        Cliente destinatario = buscarCliente(dto.getCuilDestinatario(), "Destinatario");

        envio.setRemitente(remitente);
        envio.setDestinatario(destinatario);

        // 3. Buscar y asignar Paquetes
        if (dto.getPaquetes() == null || dto.getPaquetes().isEmpty()) {
            throw new IllegalArgumentException("El envío debe contener al menos un paquete.");
        }
        List<Paquete> paquetes = buscarPaquetes(dto.getPaquetes());
        envio.setPaquetes(paquetes);

        // 4. Lógica de inicialización (como la que tenías antes)
        inicializarEnvio(envio);

        return envio;
    }
    private String generarCodigoUnico(Envio envio) {
        Long id = envio.getId();
        int cantidadPaquetes = envio.getPaquetes().size();
        String anioActual = String.valueOf(java.time.Year.now().getValue());

        String paquetesFormateados = String.format("%04d", cantidadPaquetes);

        String codigoUnico = String.format("%d-%s-%s", id, paquetesFormateados, anioActual);

        log.debug("Código Único generado: {}", codigoUnico);

        return codigoUnico;
    }
}
