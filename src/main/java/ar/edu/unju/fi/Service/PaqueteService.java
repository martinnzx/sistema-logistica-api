package ar.edu.unju.fi.Service;

import ar.edu.unju.fi.Repository.PaqueteRepository;
import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.mapper.PaqueteMapper;
import ar.edu.unju.fi.model.Paquete;
import ar.edu.unju.fi.model.PaqueteRefrigerado;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaqueteService {

    private final PaqueteRepository paqueteRepository;

    public PaqueteService(PaqueteRepository paqueteRepository) {
        this.paqueteRepository = paqueteRepository;
    }

    public PaqueteDTO crearPaquete(PaqueteDTO dto) {
        Paquete paquete = PaqueteMapper.toEntity(dto);
        validarTemperaturaPaquete(paquete);
        Paquete guardado = paqueteRepository.save(paquete);
        return PaqueteMapper.toDTO(guardado);
    }

    public List<PaqueteDTO> listarPorPeso(Double pesoKg, Double pesoKg2) {
        List<Paquete> p = paqueteRepository.findByPesoKgBetween(pesoKg, pesoKg2);
        List<PaqueteDTO> pDTO = new ArrayList<>();
        for (Paquete paquete : p) {
            PaqueteDTO dto = PaqueteMapper.toDTO(paquete);
            pDTO.add(dto);
        }
        return pDTO;
    }

    public List<PaqueteDTO> listarPorVolumen(double v1,double v2) {
        List<Paquete> p = paqueteRepository.findByVolumenDm3Between(v1, v2);
        List<PaqueteDTO> pDTO = new ArrayList<>();
        for (Paquete paquete : p) {
            PaqueteDTO dto = PaqueteMapper.toDTO(paquete);
            pDTO.add(dto);
        }
        return pDTO;

    }
    private void validarTemperaturaPaquete(Paquete paquete) {
        if (paquete instanceof PaqueteRefrigerado) {
            PaqueteRefrigerado pRef = (PaqueteRefrigerado) paquete;

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
        }
    }
}
