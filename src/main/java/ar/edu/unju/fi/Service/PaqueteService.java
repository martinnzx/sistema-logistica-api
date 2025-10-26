package ar.edu.unju.fi.Service;

import ar.edu.unju.fi.Repository.PaqueteRepository;
import ar.edu.unju.fi.model.Paquete;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaqueteService {

    private final PaqueteRepository paqueteRepository;

    public PaqueteService(PaqueteRepository paqueteRepository) {
        this.paqueteRepository = paqueteRepository;
    }

    public List<Paquete> ListarPorPeso(Double p1, Double p2){
        return paqueteRepository.findByPesoKgBetween(p1, p2);
    }

    public List<Paquete> ListarPorVolumen(Double v1, Double v2){
        return paqueteRepository.findByVolumenDm3Between(v1, v2);
    }
}
