package ar.edu.unju.fi.Service;

import ar.edu.unju.fi.Repository.EnvioRepository;
import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.mapper.EnvioMapper;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.EstadoEnvio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;

    public EnvioService(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
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

    public EnvioDTO crearEnvio(EnvioDTO dto) {
        Envio envio = EnvioMapper.toEntity(dto);
        envio = envioRepository.save(envio);
        return EnvioMapper.toDTO(envio);
    }

}
