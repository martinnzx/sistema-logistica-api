package dev.logistica.api.service;

import dev.logistica.api.exceptions.ResourceNotFoundException;
import dev.logistica.api.repository.PaqueteRepository;
import dev.logistica.api.dto.PaqueteDTO;
import dev.logistica.api.mapper.PaqueteMapper;
import dev.logistica.api.model.Paquete;
import dev.logistica.api.model.PaqueteRefrigerado;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PaqueteService {

    private final PaqueteRepository paqueteRepository;

    public PaqueteService(PaqueteRepository paqueteRepository) {
        this.paqueteRepository = paqueteRepository;
    }

    @Transactional
    public PaqueteDTO crearPaquete(@Valid PaqueteDTO dto) {
        if (dto == null) {
            log.error("Intento de crear paquete con DTO nulo.");
            return null;
        }
        log.info("Creando paquete de tipo {} con peso {} kg y volumen {} dm3",
                dto.getTipo(), dto.getPesoKg(), dto.getVolumenDm3());
        Paquete paquete = PaqueteMapper.toEntity(dto);
        validarTemperaturaPaquete(paquete);

        Paquete guardado = paqueteRepository.save(paquete);
        log.info("Paquete creado exitosamente con ID: {}", guardado.getId());

        return PaqueteMapper.toDTO(guardado);
    }

    public List<PaqueteDTO> listarPorPeso(Double pesoKg, Double pesoKg2) {
        log.info("Listando paquetes con peso entre {} kg y {} kg", pesoKg, pesoKg2);
        List<Paquete> p = paqueteRepository.findByPesoKgBetween(pesoKg, pesoKg2);

        List<PaqueteDTO> pDTO = new ArrayList<>();
        for (Paquete paquete : p) {
            PaqueteDTO dto = PaqueteMapper.toDTO(paquete);
            pDTO.add(dto);
        }

        log.debug("Se encontraron paquetes en el rango de peso solicitado.");
        return pDTO;
    }

    public List<PaqueteDTO> listarPorVolumen(double v1,double v2) {
        log.info("Listando paquetes con volumen entre {} dm3 y {} dm3", v1, v2);
        List<Paquete> p = paqueteRepository.findByVolumenDm3Between(v1, v2);

        List<PaqueteDTO> pDTO = new ArrayList<>();
        for (Paquete paquete : p) {
            PaqueteDTO dto = PaqueteMapper.toDTO(paquete);
            pDTO.add(dto);
        }

        log.debug("Se encontraron paquetes en el rango de volumen solicitado.");
        return pDTO;
    }
    public PaqueteDTO buscarPaquetePorCodigo(String codigo) {
        Paquete paquete = paqueteRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Paquete", "codigo", codigo));

        return PaqueteMapper.toDTO(paquete);
    }
    public List<PaqueteDTO> listarPaquetes(){
        List<Paquete> p = paqueteRepository.findAll();
        return p.stream()
                .map(PaqueteMapper::toDTO)
                .toList();
    }
    //---------------VALIDACIONES ---------------//
    private void validarTemperaturaPaquete(Paquete paquete) {
        if (paquete instanceof PaqueteRefrigerado pRef) {
            this.validarRangoTemperaturaRefrigerado(pRef);
        }

    }
    private void validarRangoTemperaturaRefrigerado(PaqueteRefrigerado pRef) {
        log.debug("Validando temperatura de paquete refrigerado...");

        Double tempObj = pRef.getTemperaturaObjetivo();
        Double rangoMin = pRef.getRangoMin();
        Double rangoMax = pRef.getRangoMax();

        if (tempObj == null || rangoMin == null || rangoMax == null) {
            throw new IllegalArgumentException("Faltan datos de temperatura en el paquete refrigerado");
        }

        if (tempObj < rangoMin || tempObj > rangoMax) {
            throw new IllegalArgumentException(
                    "La temperatura objetivo (" + tempObj + "°C) está fuera del rango permitido [" +
                            rangoMin + "°C - " + rangoMax + "°C]"
            );
        }
        log.info("Temperatura del paquete refrigerado validada correctamente ({}) dentro del rango [{} - {}]", tempObj, rangoMin, rangoMax);
    }
    public List<Paquete> buscarPaquetesPorCodigos(List<String> codigos) {
        List<Paquete> paquetesEncontrados = paqueteRepository.findByCodigoIn(codigos);

        if (paquetesEncontrados.size() != codigos.size()) {
            List<String> codigosEncontrados = paquetesEncontrados.stream()
                    .map(Paquete::getCodigo)
                    .toList();
            List<String> codigosFaltantes = codigos.stream()
                    .filter(c -> !codigosEncontrados.contains(c))
                    .toList();

            throw new IllegalArgumentException("Los siguientes códigos de paquete no se encontraron: " + String.join(", ", codigosFaltantes));
        }
        return paquetesEncontrados;
    }
}
