package ar.edu.unju.fi.mapper;

import ar.edu.unju.fi.dto.VehiculoDTO;
import ar.edu.unju.fi.model.Vehiculo;

public class VehiculoMapper {

    public static VehiculoDTO toDTO(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return null;
        }

        return new VehiculoDTO(
            vehiculo.getPatente(),
            vehiculo.getCapacidadMaxPesoKg(),
            vehiculo.getCapacidadMaxVolDm3(),
            vehiculo.getRefrigerado()
        );
    }
}
