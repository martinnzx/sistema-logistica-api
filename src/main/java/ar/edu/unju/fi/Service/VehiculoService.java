package ar.edu.unju.fi.Service;

import ar.edu.unju.fi.Repository.VehiculoRepository;
import ar.edu.unju.fi.dto.VehiculoDTO;
import ar.edu.unju.fi.mapper.VehiculoMapper;
import ar.edu.unju.fi.model.Vehiculo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public VehiculoDTO crearVehiculo(VehiculoDTO vehiculoDTO) {
        Vehiculo vehiculo = VehiculoMapper.toEntity(vehiculoDTO);
        Vehiculo vehiculoGuardado = vehiculoRepository.save(vehiculo);
        return VehiculoMapper.toDTO(vehiculoGuardado);
    }

    public List<VehiculoDTO> buscarVehiculosRefrigerados(Boolean refrigerado) {
        return vehiculoRepository.findByRefrigerado(refrigerado)
                .stream()
                .map(VehiculoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<VehiculoDTO> buscarVehiculosPorPeso(Double pesoRequerido) {
        return vehiculoRepository.findByCapacidadMaxPesoKgGreaterThanEqual(pesoRequerido)
                .stream()
                .map(VehiculoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<VehiculoDTO> buscarVehiculosPorVolumen(Double volumen) {
        return vehiculoRepository.findByCapacidadMaxVolDm3GreaterThanEqual(volumen)
                .stream()
                .map(VehiculoMapper::toDTO)
                .collect(Collectors.toList());
    }



}
