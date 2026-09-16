package dev.logistica.api.repository;

import dev.logistica.api.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    List<Vehiculo> findByRefrigerado(boolean refrigerado);

    List<Vehiculo> findByCapacidadMaxPesoKgGreaterThanEqual(double pesoRequerido);

    List<Vehiculo> findByCapacidadMaxVolDm3GreaterThanEqual(double volumenRequerido);

    Optional<Vehiculo> findByPatenteIgnoreCase(String patente);
}

