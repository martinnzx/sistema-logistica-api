package dev.logistica.api.repository;

import dev.logistica.api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDocumentoOCuitIgnoreCase(String documentoOCuit);
}

