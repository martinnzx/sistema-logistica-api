package ar.edu.unju.fi.Service;

import ar.edu.unju.fi.Repository.RutaRepository;
import ar.edu.unju.fi.dto.RutaDTO;
import ar.edu.unju.fi.mapper.RutaMapper;
import ar.edu.unju.fi.model.Ruta;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RutaService {
    private RutaRepository rutaRepository;

    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }


    public List<RutaDTO> obtenerEnviosPorRutaYFecha(Long rutaId, LocalDate fecha) {
        List<Ruta> rutas = rutaRepository.findByIdAndFecha(rutaId, fecha);
        List<RutaDTO> rutasDTO = new ArrayList<>();
        for (Ruta ruta : rutas) {
            RutaDTO dto = RutaMapper.toDto(ruta);
            rutasDTO.add(dto);
        }
        return rutasDTO;
    }
}
