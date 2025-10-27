package ar.edu.unju.fi.Repository;

import ar.edu.unju.fi.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {

    List<Ruta> findByIdAndFecha(Long id, LocalDate fecha);

}
