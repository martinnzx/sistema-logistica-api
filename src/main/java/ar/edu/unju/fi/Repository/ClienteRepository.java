package ar.edu.unju.fi.Repository;

import ar.edu.unju.fi.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByDocumentoOCuit(String documentoOCuit);
}
