package ar.edu.unju.fi.Service;

import ar.edu.unju.fi.model.Cliente;
import ar.edu.unju.fi.Repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Cliente getById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }

    public Cliente getByDocumentoOCuit(String doc) {
        return clienteRepository.findByDocumentoOCuit(doc);
    }

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
}
