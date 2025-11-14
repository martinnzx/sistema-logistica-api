package ar.edu.unju.fi.repository;

import ar.edu.unju.fi.model.Paquete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Long> {
    List<Paquete> findByPesoKgBetween(Double p, Double p2);
    List<Paquete> findByVolumenDm3Between(Double v, Double v2);
    List<Paquete> findByCodigoIn(List<String> codigos);
}
