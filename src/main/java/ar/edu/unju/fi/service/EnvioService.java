package ar.edu.unju.fi.service;

import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.repository.ClienteRepository;
import ar.edu.unju.fi.repository.EnvioRepository;
import ar.edu.unju.fi.repository.HistorialEstadoEnvioRepository;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.mapper.EnvioMapper;
import ar.edu.unju.fi.model.*;
import ar.edu.unju.fi.state.EstadoEnvioFactory;
import ar.edu.unju.fi.state.EstadoEnvioState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final HistorialEstadoEnvioRepository historialEstadoEnvioRepository;
    private final ClienteRepository clienteRepository;

    public EnvioService(EnvioRepository envioRepository,
                        HistorialEstadoEnvioRepository historialEstadoEnvioRepository,
                        ClienteRepository clienteRepository) {
        this.envioRepository = envioRepository;
        this.historialEstadoEnvioRepository = historialEstadoEnvioRepository;
        this.clienteRepository = clienteRepository;
    }

    /* =====================
       CREAR ENVÍO
       ===================== */
    @Transactional
    public EnvioDTO crearEnvio(EnvioDTO dto) {
        log.info("Iniciando creación de envío para remitente: {}", dto.getRemitente());
        Envio envio = ensamblarEnvio(dto);

        validarPaquetes(envio);
        detectarRefrigerado(envio);
        inicializarEnvio(envio);
        envio = envioRepository.save(envio);

        registrarHistorialInicial(envio);

        log.info("Envío creado correctamente con código: {}", envio.getCodigoUnico());
        return EnvioMapper.toDTO(envio);
    }

    /* =====================
       BÚSQUEDAS
       ===================== */
    public List<EnvioDTO> listarPorRemitente(String documentoOCuit) {
        log.info("Buscando envíos del remitente con documento/CUIT: {}", documentoOCuit);

        List<EnvioDTO> envios = envioRepository.findByRemitente_DocumentoOCuitIgnoreCase(documentoOCuit)
                .stream().map(EnvioMapper::toDTO).toList();

        if (envios.isEmpty()) {
            log.warn("No se encontraron envíos para el remitente con documento/CUIT: {}", documentoOCuit);
        } else {
            log.info("Se encontraron {} envíos para el remitente con documento/CUIT: {}", envios.size(), documentoOCuit);
        }

        return envios;
    }

    public List<EnvioDTO> listarPorDestinatario(String documentoOCuit) {
        log.info("Buscando envíos del destinatario con documento/CUIT: {}", documentoOCuit);

        List<EnvioDTO> envios = envioRepository.findByDestinatario_DocumentoOCuitIgnoreCase(documentoOCuit)
                .stream().map(EnvioMapper::toDTO).toList();

        if (envios.isEmpty()) {
            log.warn("No se encontraron envíos para el destinatario con documento/CUIT: {}", documentoOCuit);
        } else {
            log.info("Se encontraron {} envíos para el destinatario con documento/CUIT: {}", envios.size(), documentoOCuit);
        }

        return envios;
    }

    public List<EnvioDTO> listarPorEstado(EstadoEnvio estado) {
        log.info("Listando envíos con estado: {}", estado);

        List<EnvioDTO> envios = envioRepository.findByEstado(estado)
                .stream().map(EnvioMapper::toDTO).toList();

        if (envios.isEmpty()) {
            log.warn("No se encontraron envíos con el estado: {}", estado);
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
    public void avanzarEstado(Long envioId, String observacion) {
        Envio envio = obtenerEnvioPorId(envioId);
        EstadoEnvioState estado = EstadoEnvioFactory.getEstado(envio.getEstado());
        estado.avanzar(envio, historialEstadoEnvioRepository, observacion);
        envioRepository.save(envio);
        log.info("Envío {} avanzó correctamente al estado {}", envio.getCodigoUnico(), envio.getEstado());
    }

    @Transactional
    public void cancelarEnvio(Long envioId, String observacion) {
        Envio envio = obtenerEnvioPorId(envioId);
        EstadoEnvioState estado = EstadoEnvioFactory.getEstado(envio.getEstado());
        estado.cancelar(envio, historialEstadoEnvioRepository, observacion);
        envioRepository.save(envio);
        log.info("Envío {} fue cancelado. Estado final: {}", envio.getCodigoUnico(), envio.getEstado());
    }

    @Transactional
    public void devolverEnvio(Long envioId, String observacion) {
        Envio envio = obtenerEnvioPorId(envioId);
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
        envio.setCodigoUnico(UUID.randomUUID().toString());
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
    private Envio ensamblarEnvio(EnvioDTO dto) {
        Cliente remitente = clienteRepository.findByDocumentoOCuitIgnoreCase(dto.getRemitente().getDocumentoOCuit())
                .orElseThrow(() -> new IllegalArgumentException("Remitente no encontrado"));
        Cliente destinatario = clienteRepository.findByDocumentoOCuitIgnoreCase(dto.getDestinatario().getDocumentoOCuit())
                .orElseThrow(() -> new IllegalArgumentException("Destinatario no encontrado"));

        Envio envio = EnvioMapper.toEntity(dto);
        envio.setRemitente(remitente);
        envio.setDestinatario(destinatario);

        return envio;
    }
}
