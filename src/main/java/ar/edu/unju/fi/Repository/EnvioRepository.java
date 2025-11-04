package ar.edu.unju.fi.Repository;


import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.Enum.EstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EnvioRepository extends JpaRepository<Envio,Long> {
    List<Envio> findByRemitenteIgnoreCase(String remitente);

    List<Envio> findByDestinatarioIgnoreCase(String destinatario);

    List<Envio> findByEstado(EstadoEnvio estado);
}
