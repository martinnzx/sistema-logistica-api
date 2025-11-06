package ar.edu.unju.fi.service;

import ar.edu.unju.fi.repository.VehiculoRepository;
import ar.edu.unju.fi.dto.VehiculoDTO;
import ar.edu.unju.fi.mapper.VehiculoMapper;
import ar.edu.unju.fi.model.Vehiculo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    @Transactional
    public VehiculoDTO crearVehiculo(VehiculoDTO vehiculoDTO) {
        if (vehiculoDTO == null) {
            log.warn("Intento de crear vehículo con DTO nulo.");
            return null;
        }
        log.info("Creando vehículo con patente: {}, capacidad: {} kg / {} dm3, refrigerado: {}",
                vehiculoDTO.getPatente(),
                vehiculoDTO.getCapacidadMaxPesoKg(),
                vehiculoDTO.getCapacidadMaxVolDm3(),
                vehiculoDTO.getRefrigerado());

        Vehiculo vehiculo = VehiculoMapper.toEntity(vehiculoDTO);
        Vehiculo vehiculoGuardado = vehiculoRepository.save(vehiculo);

        log.info("Vehículo creado exitosamente con ID: {}", vehiculoGuardado.getId());
        return VehiculoMapper.toDTO(vehiculoGuardado);
    }

    public List<VehiculoDTO> buscarVehiculosRefrigerados(Boolean refrigerado) {
        log.info("Buscando vehículos con sistema de refrigeración: {}", refrigerado);
        return vehiculoRepository.findByRefrigerado(refrigerado)
                .stream()
                .map(VehiculoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<VehiculoDTO> buscarVehiculosPorPeso(Double pesoRequerido) {
        log.info("Buscando vehículos con capacidad mínima de peso: {} kg", pesoRequerido);
        return vehiculoRepository.findByCapacidadMaxPesoKgGreaterThanEqual(pesoRequerido)
                .stream()
                .map(VehiculoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<VehiculoDTO> buscarVehiculosPorVolumen(Double volumen) {
        log.info("Buscando vehículos con capacidad mínima de volumen: {} dm3", volumen);
        return vehiculoRepository.findByCapacidadMaxVolDm3GreaterThanEqual(volumen)
                .stream()
                .map(VehiculoMapper::toDTO)
                .collect(Collectors.toList());
    }

}
