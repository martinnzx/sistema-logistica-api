package ar.edu.unju.fi.Service;

import ar.edu.unju.fi.Repository.EnvioRepository;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.EstadoEnvio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;

    public EnvioService(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    public List<Envio> listarPorRemitente(String remitente) {
        return envioRepository.findByRemitenteIgnoreCase(remitente);
    }

    public List<Envio> listarPorDestinatario(String destinatario) {
        return envioRepository.findByDestinatarioIgnoreCase(destinatario);
    }

    public List<Envio> listarPorEstado(EstadoEnvio estado) {
        return envioRepository.findByEstado(estado);
    }
}
