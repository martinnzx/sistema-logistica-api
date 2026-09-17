package dev.logistica.api.repository;

import dev.logistica.api.model.Envio;
import dev.logistica.api.enums.EstadoEnvio;
import dev.logistica.api.model.Paquete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<Envio,Long> {
    List<Envio> findByRemitente_DocumentoOCuitIgnoreCase(String documentoOCuit);

    List<Envio> findByDestinatario_DocumentoOCuitIgnoreCase(String documentoOCuit);

    List<Envio> findByEstado(EstadoEnvio estado);

    Optional<Envio> findByCodigoUnico(String codigoUnico);
    boolean existsByPaquetes(Paquete paquete);
    List<Envio> findByCodigoUnicoIn(List<String> codigos);
}

