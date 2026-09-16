package dev.logistica.api.mapper;

import dev.logistica.api.dto.VehiculoDTO;
import dev.logistica.api.model.Vehiculo;

public class VehiculoMapper {

    private VehiculoMapper() {
        throw new IllegalStateException("Utility class");
    }
    public static VehiculoDTO toDTO(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return null;
        }

        return new VehiculoDTO(
            vehiculo.getPatente(),
            vehiculo.getCapacidadMaxPesoKg(),
            vehiculo.getCapacidadMaxVolDm3(),
            vehiculo.getRefrigerado(),
            vehiculo.getRangoTemperaturaMin(),
            vehiculo.getRangoTemperaturaMax()
        );
    }

    public static Vehiculo toEntity(VehiculoDTO dto) {
        if (dto == null) {
            return null;
        }

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente(dto.getPatente());
        vehiculo.setCapacidadMaxPesoKg(dto.getCapacidadMaxPesoKg());
        vehiculo.setCapacidadMaxVolDm3(dto.getCapacidadMaxVolDm3());
        vehiculo.setRefrigerado(dto.getRefrigerado());
        vehiculo.setRangoTemperaturaMin(dto.getRangoTemperaturaMin());
        vehiculo.setRangoTemperaturaMax(dto.getRangoTemperaturaMax());

        return vehiculo;
    }

}

