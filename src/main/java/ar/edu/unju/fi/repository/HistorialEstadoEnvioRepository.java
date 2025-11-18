package ar.edu.unju.fi.repository;

import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.HistorialEstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HistorialEstadoEnvioRepository extends JpaRepository<HistorialEstadoEnvio, Long> {
    List<HistorialEstadoEnvio> findByEnvio(Envio envio);

    List<HistorialEstadoEnvio> findByEstadoNuevoAndFechaHoraBetween(
            EstadoEnvio estadoNuevo,
            LocalDateTime desde,
            LocalDateTime hasta
    );
}
