package ar.edu.unju.fi.Service;

import ar.edu.unju.fi.Repository.RutaRepository;
import ar.edu.unju.fi.model.Ruta;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RutaService {
    private RutaRepository rutaRepository;

    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }


    public List<Ruta> obtenerEnviosPorRutaYFecha(Long rutaId, LocalDate fecha) {
        return rutaRepository.findByIdAndFecha(rutaId, fecha);
    }
}
