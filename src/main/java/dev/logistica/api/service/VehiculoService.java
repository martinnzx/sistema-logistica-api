package dev.logistica.api.service;

import dev.logistica.api.exceptions.ResourceNotFoundException;
import dev.logistica.api.repository.VehiculoRepository;
import dev.logistica.api.dto.VehiculoDTO;
import dev.logistica.api.mapper.VehiculoMapper;
import dev.logistica.api.model.Vehiculo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    @Transactional
    public VehiculoDTO crearVehiculo(@Valid VehiculoDTO vehiculoDTO) {
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
                .toList();
    }

    public List<VehiculoDTO> buscarVehiculosPorPeso(Double pesoRequerido) {
        log.info("Buscando vehículos con capacidad mínima de peso: {} kg", pesoRequerido);
        return vehiculoRepository.findByCapacidadMaxPesoKgGreaterThanEqual(pesoRequerido)
                .stream()
                .map(VehiculoMapper::toDTO)
                .toList();
    }

    public List<VehiculoDTO> buscarVehiculosPorVolumen(Double volumen) {
        log.info("Buscando vehículos con capacidad mínima de volumen: {} dm3", volumen);
        return vehiculoRepository.findByCapacidadMaxVolDm3GreaterThanEqual(volumen)
                .stream()
                .map(VehiculoMapper::toDTO)
                .toList();
    }
    public VehiculoDTO buscarVehiculoPorPatente(String patente) {
        Vehiculo vehiculo = vehiculoRepository.findByPatenteIgnoreCase(patente)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo", "patente", patente));

        VehiculoDTO dto = new VehiculoDTO();
        dto.setPatente(patente);
        dto.setCapacidadMaxPesoKg(vehiculo.getCapacidadMaxPesoKg());
        dto.setCapacidadMaxVolDm3(vehiculo.getCapacidadMaxVolDm3());
        dto.setRefrigerado(vehiculo.getRefrigerado());

        dto.setRangoTemperaturaMin(vehiculo.getRangoTemperaturaMin());
        dto.setRangoTemperaturaMax(vehiculo.getRangoTemperaturaMax());

        return dto;
    }
    public List<VehiculoDTO>  listarVehiculos(){
        log.info("Listando todos los vehiculos");
        List<Vehiculo> vehiculos = vehiculoRepository.findAll();
        return vehiculos.stream()
                .map(VehiculoMapper::toDTO)
                .toList();
    }
    public Vehiculo buscarVehiculoPatente(String patente){
        return vehiculoRepository.findByPatenteIgnoreCase(patente)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo", "patente", patente));
    }

}

