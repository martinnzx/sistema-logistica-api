package ar.edu.unju.fi.Service;

import ar.edu.unju.fi.Repository.EnvioRepository;
import ar.edu.unju.fi.State.Estado_Generado;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.mapper.EnvioMapper;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.EstadoEnvio;
import ar.edu.unju.fi.model.Paquete;
import ar.edu.unju.fi.model.PaqueteRefrigerado;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EnvioService {

    private final EnvioRepository envioRepository;

    public EnvioService(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    public EnvioDTO crearEnvio(EnvioDTO dto) {
        Envio envio = EnvioMapper.toEntity(dto);

        validarPaquetes(envio.getPaquetes());
        verificarRequiereFrio(envio);

        /* FIXME: Corregir */
        /* asignarEstadoInicial(envio); */

        generarCodigoUnico(envio);

        envio = envioRepository.save(envio);
        return EnvioMapper.toDTO(envio);
    }

    public List<EnvioDTO> listarPorRemitente(String remitente) {
        return envioRepository.findByRemitenteIgnoreCase(remitente)
                .stream()
                .map(EnvioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<EnvioDTO> listarPorDestinatario(String destinatario) {
        return envioRepository.findByDestinatarioIgnoreCase(destinatario)
                .stream()
                .map(EnvioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<EnvioDTO> listarPorEstado(EstadoEnvio estado) {
        return envioRepository.findByEstado(estado)
                .stream()
                .map(EnvioMapper::toDTO)
                .collect(Collectors.toList());
    }

    /* VALIDACIONES */

    /* Validar peso y volumen de paquetes */

    private void validarPaquetes(List<Paquete> paquetes) {
        for (Paquete p : paquetes) {
            if (p.getPesoKg() == null || p.getPesoKg() <= 0) {
                throw new RuntimeException("El paquete debe tener un peso valido.");
            }
            if (p.getVolumenDm3() == null || p.getVolumenDm3() <= 0) {
                throw new RuntimeException("El paquete debe tener un volumen valido.");
            }
        }
    }

    /* Verificar si requiere frio (si hay al menos un PaqueteRefrigerado) */

    private void verificarRequiereFrio(Envio envio) {
        for (Paquete paquete : envio.getPaquetes()) {
            if (paquete instanceof PaqueteRefrigerado) {
                log.info("El envio requiere refrigeracion.");
                return;
            }
        }
        log.info("El envio NO requiere refrigeracion.");
    }

    /* Asignar estado inicial GENERADO */

    /* FIXME: Arreglar esto */
//    private void asignarEstadoInicial(Envio envio) {
//        envio.setEstadoN(new Estado_Generado());
//    }

    /* Generar un codigo unico con hash (simulando ID especial) */

    private void generarCodigoUnico(Envio envio) {
        String datos = envio.getRemitente().getDocumento() +
                envio.getDireccionEntrega() +
                System.currentTimeMillis();

        int hash = datos.hashCode();
        String codigo = "ENV-" + Math.abs(hash);

        log.info("Codigo unico generado");
    }

}