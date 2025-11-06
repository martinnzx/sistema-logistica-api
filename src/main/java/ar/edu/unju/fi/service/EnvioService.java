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
import java.util.stream.Collectors;

@Slf4j
@Service
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final HistorialEstadoEnvioRepository historialEstadoEnvioRepository;
    private final ClienteRepository clienteRepository;

    public EnvioService(EnvioRepository envioRepository, HistorialEstadoEnvioRepository historialEstadoEnvioRepository, ClienteRepository clienteRepository) {
        this.envioRepository = envioRepository;
        this.historialEstadoEnvioRepository = historialEstadoEnvioRepository;
        this.clienteRepository = clienteRepository;
    }

    /* =====================
       CREAR ENVIO
       ===================== */
    @Transactional
    public EnvioDTO crearEnvio(EnvioDTO dto) {
        log.info("Iniciando creacion de envio para remitente: {}", dto.getRemitente());
        Envio envio = EnvioMapper.toEntity(dto);

        validarPaquetes(envio);
        detectarRefrigerado(envio);
        inicializarEnvio(envio);

        envio = envioRepository.save(envio);
        log.info("Envio guardado exitosamente con ID: {} y codigo: {}", envio.getId(), envio.getCodigoUnico());

        registrarHistorialInicial(envio);
        log.info("Historial inicial registrado para el envio con codigo: {}", envio.getCodigoUnico());

        return EnvioMapper.toDTO(envio);
    }

    /* =====================
       BUSQUEDAS
       ===================== */

    public List<EnvioDTO> listarPorRemitente(String documentoOCuit) {
        log.info("Listando envios del remitente con documento/CUIT: {}", documentoOCuit);
        return envioRepository.findByRemitente_DocumentoOCuitIgnoreCase(documentoOCuit)
                .stream()
                .map(EnvioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<EnvioDTO> listarPorDestinatario(String documentoOCuit) {
        log.info("Listando envios del destinatario con documento/CUIT: {}", documentoOCuit);
        return envioRepository.findByDestinatario_DocumentoOCuitIgnoreCase(documentoOCuit)
                .stream()
                .map(EnvioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<EnvioDTO> listarPorEstado(EstadoEnvio estado) {
        log.info("Listando envios por estado: {}", estado);
        return envioRepository.findByEstado(estado)
                .stream()
                .map(EnvioMapper::toDTO)
                .collect(Collectors.toList());
    }

    /* =====================
       CAMBIO DE ESTADO
       ===================== */
    @Transactional
    public void avanzarEstado(Long envioId, String observacion) {
        log.info("Intentando avanzar estado del envio con ID: {}", envioId);

        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> {
                    log.warn("Envio con ID {} no encontrado al intentar avanzar estado", envioId);
                    return new RuntimeException("Envio no encontrado");
                });

        EstadoEnvio estadoAnterior = envio.getEstado();

        try {
            EstadoEnvioState estadoActual = EstadoEnvioFactory.getEstado(estadoAnterior);
            estadoActual.avanzar(envio);

            EstadoEnvio nuevoEstado = envio.getEstado();
            envioRepository.save(envio);

            registrarHistorial(envio, estadoAnterior, nuevoEstado, observacion);
            log.info("Envio {} avanzo de {} a {}", envio.getCodigoUnico(), estadoAnterior, nuevoEstado);
        } catch (Exception e) {
            log.error("Error al avanzar estado del Envio {}: {}", envio.getCodigoUnico(), e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void cancelarEnvio(Long envioId, String observacion) {
        log.info("Intentando cancelar el envio con ID: {}", envioId);

        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> {
                    log.warn("Envio con ID {} no encontrado al intentar cancelar", envioId);
                    return new RuntimeException("Envio no encontrado");
                });

        EstadoEnvio estadoAnterior = envio.getEstado();

        try {
            EstadoEnvioState estadoActual = EstadoEnvioFactory.getEstado(estadoAnterior);
            estadoActual.cancelar(envio);

            EstadoEnvio nuevoEstado = envio.getEstado();
            envioRepository.save(envio);

            registrarHistorial(envio, estadoAnterior, nuevoEstado, observacion);
            log.info("Envio {} cancelado correctamente. Estado previo: {}, estado nuevo: {}", envio.getCodigoUnico(), estadoAnterior, nuevoEstado);
        } catch (Exception e) {
            log.error("Error al cancelar envio {}: {}", envio.getCodigoUnico(), e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void devolverEnvio(Long envioId, String observacion) {
        log.info("Intentando devolver el envio con ID: {}", envioId);

        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> {
                    log.warn("Envio con ID {} no encontrado al intentar devolver", envioId);
                    return new RuntimeException("Envio no encontrado");
                });

        EstadoEnvio estadoAnterior = envio.getEstado();

        try {
            EstadoEnvioState estadoActual = EstadoEnvioFactory.getEstado(estadoAnterior);
            estadoActual.devolver(envio);

            EstadoEnvio nuevoEstado = envio.getEstado();
            envioRepository.save(envio);

            registrarHistorial(envio, estadoAnterior, nuevoEstado, observacion);
            log.info("Envio {} devuelto correctamente. Estado previo: {}, estado nuevo: {}", envio.getCodigoUnico(), estadoAnterior, nuevoEstado);
        } catch (Exception e) {
            log.error("Error al devolver envio {}: {}", envio.getCodigoUnico(), e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void adjuntarComprobante(Long envioId, String comprobante) {
        log.info("Adjuntando comprobante al envio con ID: {}", envioId);
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> {
                    log.error("No se encontro envio con ID: {}", envioId);
                    return new RuntimeException("Envio no encontrado");
                });

        envio.setComprobanteEntrega(comprobante);
        envioRepository.save(envio);
        log.info("Comprobante adjuntado correctamente al envio {}", envio.getCodigoUnico());
    }

    /* =====================
       VALIDACIONES
       ===================== */

    private void registrarHistorial(Envio envio, EstadoEnvio anterior, EstadoEnvio nuevo, String observacion) {
        log.debug("Registrando historial para envio {}. Estado anterior: {}, nuevo estado: {}", envio.getCodigoUnico(), anterior, nuevo);

        HistorialEstadoEnvio historial = HistorialEstadoEnvio.builder()
                .envio(envio)
                .estadoAnterior(anterior)
                .estadoNuevo(nuevo)
                .fechaHora(LocalDateTime.now())
                .observacion(observacion)
                .build();

        historialEstadoEnvioRepository.save(historial);
        log.info("Historial registrado para envio {} con nuevo estado {}", envio.getCodigoUnico(), nuevo);
    }

    private void validarPaquetes(Envio envio) {
        for (Paquete paquete : envio.getPaquetes()) {
            if (paquete.getPesoKg() == null || paquete.getPesoKg() <= 0) {
                log.error("Peso invalido para paquete {}", paquete.getCodigo());
                throw new IllegalArgumentException("Peso invalido para el paquete: " + paquete.getCodigo());
            }
            if (paquete.getVolumenDm3() == null || paquete.getVolumenDm3() <= 0) {
                log.error("Volumen invalido para paquete {}", paquete.getCodigo());
                throw new IllegalArgumentException("Volumen invalido para el paquete: " + paquete.getCodigo());
            }
        }
        log.info("Todos los paquetes del envio {} son validos", envio.getCodigoUnico());
    }

    private void detectarRefrigerado(Envio envio) {
        boolean requiereFrio = false;

        for (Paquete paquete : envio.getPaquetes()) {
            if (paquete instanceof PaqueteRefrigerado) {
                requiereFrio = true;
                break;
            }
        }
        envio.setRequiereFrio(requiereFrio);
        log.info("El envio {} {}", envio.getCodigoUnico(), requiereFrio ? "requiere refrigeracion" : "no requiere refrigeracion");
    }

    private void inicializarEnvio(Envio envio) {
        envio.setEstado(EstadoEnvio.GENERADO);
        envio.setCodigoUnico(UUID.randomUUID().toString());
        log.debug("Envio inicializado con estado GENERADO y codigo: {}", envio.getCodigoUnico());
    }

    private void registrarHistorialInicial(Envio envio) {
        historialEstadoEnvioRepository.save(HistorialEstadoEnvio.builder()
                .envio(envio)
                .estadoAnterior(null)
                .estadoNuevo(EstadoEnvio.GENERADO)
                .fechaHora(LocalDateTime.now())
                .observacion("Envio creado")
                .build());

        log.info("Historial inicial creado para envio con codigo: {}", envio.getCodigoUnico());
    }

}
