package ar.edu.unju.fi.service;

import ar.edu.unju.fi.dto.ClienteDTO;
import ar.edu.unju.fi.dto.DatosEmailDTO;
import ar.edu.unju.fi.dto.views.EnvioViewDTO;
import ar.edu.unju.fi.dto.views.EnvioViewDestinatarioDTO;
import ar.edu.unju.fi.dto.views.EnvioViewEstadoDTO;
import ar.edu.unju.fi.dto.views.EnvioViewRemitenteDTO;
import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.exceptions.ResourceNotFoundException;
import ar.edu.unju.fi.mapper.ClienteMapper;
import ar.edu.unju.fi.mapper.viewsMapper.EnvioViewDestinatarioMapper;
import ar.edu.unju.fi.mapper.viewsMapper.EnvioViewEstadoMapper;
import ar.edu.unju.fi.mapper.viewsMapper.EnvioViewMapper;
import ar.edu.unju.fi.mapper.viewsMapper.EnvioViewRemitenteMapper;
import ar.edu.unju.fi.repository.EnvioRepository;
import ar.edu.unju.fi.repository.HistorialEstadoEnvioRepository;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.mapper.EnvioMapper;
import ar.edu.unju.fi.model.*;
import ar.edu.unju.fi.service.email.EmailService;
import ar.edu.unju.fi.state.EstadoEnvioFactory;
import ar.edu.unju.fi.state.EstadoEnvioState;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class EnvioService {
    private final EnvioRepository envioRepository;
    private final HistorialEstadoEnvioRepository historialEstadoEnvioRepository;
    private final ClienteService clienteService;
    private final PaqueteService paqueteService;
    private final EmailService emailService;

    public EnvioService(EnvioRepository envioRepository,
                        HistorialEstadoEnvioRepository historialEstadoEnvioRepository,
                        ClienteService clienteService,PaqueteService paqueteService,
                        EmailService emailService) {
        this.envioRepository = envioRepository;
        this.historialEstadoEnvioRepository = historialEstadoEnvioRepository;
        this.emailService = emailService;
        this.clienteService = clienteService;
        this.paqueteService = paqueteService;
    }

    /* =====================
       CREAR ENVÍO
       ===================== */
    @Transactional
    public EnvioDTO crearEnvio(@Valid EnvioViewDTO viewDTO) {
        log.info("Iniciando creación de envío desde ViewDTO...");
        Envio envio = ensamblarEnvio(viewDTO);
        inicializarEnvio(envio);
        validarPaquetes(envio);
        detectarRefrigerado(envio);

        envioRepository.save(envio);

        registrarHistorialInicial(envio);

        // ========= MAIL: ENVIO REGISTRADO ==========

        DatosEmailDTO emailDTO = DatosEmailDTO.builder()
                .codigo(envio.getCodigoUnico())
                .remitente(envio.getRemitente().getNombreRazonSocial())
                .destinatario(envio.getDestinatario().getNombreRazonSocial())
                .build();

        // 6. Enviar email al REMITENTE
        if (envio.getRemitente().getEmail() != null) {
            emailDTO.setEmailPara(envio.getRemitente().getEmail());
            emailService.enviarEmailEnvioRegistrado(emailDTO);
        }

        // 7. Enviar email al DESTINATARIO
        if (envio.getDestinatario().getEmail() != null) {
            emailDTO.setEmailPara(envio.getDestinatario().getEmail());
            emailService.enviarEmailEnvioRegistrado(emailDTO);
        }
        log.info("Envío creado exitosamente con código: {}", envio.getCodigoUnico());
        return EnvioMapper.toDTO(envio);
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
    public List<Envio> buscarEnviosValidados(List<String> codigos) {
        if (codigos == null || codigos.isEmpty()) {
            throw new IllegalArgumentException("La ruta debe contener al menos un código de envío.");
        }

        List<Envio> envios = envioRepository.findByCodigoUnicoIn(codigos);

        if (envios.size() != codigos.size()) {
            throw new ResourceNotFoundException("No se encontraron todos los envíos solicitados. Verifique los códigos.");
        }

        for (Envio envio : envios) {
            if (envio.getEstado() != EstadoEnvio.EN_ALMACEN) {
                throw new IllegalArgumentException("El envío " + envio.getCodigoUnico() +
                        " no puede ser asignado a una ruta. Estado actual: " + envio.getEstado());
            }
        }
        return envios;
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
        if (envio.getEstado() == EstadoEnvio.ENTREGADO) {

            log.info("Enviando email de 'Envío Entregado' a {}", envio.getDestinatario().getEmail());

            DatosEmailDTO emailDTO = DatosEmailDTO.builder()
                    .emailPara(envio.getDestinatario().getEmail())
                    .codigo(envio.getCodigoUnico())
                    .destinatario(envio.getDestinatario().getNombreRazonSocial())
                    .direccion(envio.getDireccionEntrega())
                    .build();

            emailService.enviarEmailEnvioEntregado(emailDTO);
        }
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
        log.debug("Inicializando estado del envío a GENERADO.");
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
    private Envio ensamblarEnvio(EnvioViewDTO dto) {
        Envio envio = EnvioViewMapper.toEntity(dto);

        //Verificar Cliente
        ClienteDTO remitente = clienteService.buscarPorDocumentoOCuit(dto.getCuilRemitente());
        ClienteDTO destinatario = clienteService.buscarPorDocumentoOCuit(dto.getCuilDestinatario());
        envio.setRemitente(ClienteMapper.toEntity(remitente));
        envio.setDestinatario(ClienteMapper.toEntity(destinatario));

        List<Paquete> paquetes = paqueteService.buscarPaquetesPorCodigos(dto.getPaquetes());
        if (paquetes.size() != dto.getPaquetes().size()) {
            throw new IllegalArgumentException("Uno o más códigos de paquete no se encontraron.");
        }

        // 3. Valida si ya están en otro envío
        for (Paquete paqueteEntity : paquetes) {
            if (envioRepository.existsByPaquetes(paqueteEntity)) {
                throw new IllegalArgumentException("El paquete con código '" + paqueteEntity.getCodigo() + "' ya se encuentra asignado a otro envío.");
            }
        }
        envio.setPaquetes(paquetes);
        String codigo = generarCodigoUnico(dto.getCuilRemitente(),dto.getCuilDestinatario());
        envio.setCodigoUnico(codigo);

        return envio;
    }
    private String generarCodigoUnico(String doc1,String doc2) {//fecha hora num dni remitente destinatario
        String doc1Limpio = doc1.replaceAll("\\D", "");//Deja solo los digitos
        String doc2Limpio = doc2.replaceAll("\\D", "");//en cada string
        String parte1 = doc1Limpio.substring(0, 3); //obtiene los 3 primero digiros de doc1
        String parte3 = doc2Limpio.substring(doc2Limpio.length() - 3);//obtiene los ultimo 3 dig

        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatoMedio = DateTimeFormatter.ofPattern("yyyyHHmm");
        String parte2 = ahora.format(formatoMedio);

        String codigoUnico = String.format("%s-%s-%s", parte1, parte2, parte3);

        log.debug("Código Único generado: {}", codigoUnico);
        return codigoUnico;
    }
}
