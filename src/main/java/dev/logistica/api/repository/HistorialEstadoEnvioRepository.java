package dev.logistica.api.repository;

import dev.logistica.api.enums.EstadoEnvio;
import dev.logistica.api.model.Envio;
import dev.logistica.api.model.HistorialEstadoEnvio;
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

