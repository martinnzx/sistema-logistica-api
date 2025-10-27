package ar.edu.unju.fi.Service;

import ar.edu.unju.fi.Repository.VehiculoRepository;
import ar.edu.unju.fi.model.Vehiculo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<Vehiculo> buscarVehiculosRefrigerados(Boolean refrigerado) {
        return vehiculoRepository.findByRefrigerado(refrigerado);
    }

    public List<Vehiculo> buscarVehiculosPorPeso(Double pesoRequerido) {
        return vehiculoRepository.findByCapacidadMaxPesoKgGreaterThanEqual(pesoRequerido);
    }

    public List<Vehiculo> buscarVehiculosPorVolumen(Double volumen) {
        return vehiculoRepository.findByCapacidadMaxVolDm3GreaterThanEqual(volumen);
    }
}
