package ar.edu.unju.fi.repository;


import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.enums.EstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EnvioRepository extends JpaRepository<Envio,Long> {
    List<Envio> findByRemitente_DocumentoOCuitIgnoreCase(String documentoOCuit);

    List<Envio> findByDestinatario_DocumentoOCuitIgnoreCase(String documentoOCuit);

    List<Envio> findByEstado(EstadoEnvio estado);
}
