package ar.edu.unju.fi.service;

import ar.edu.unju.fi.dto.ClienteDTO;
import ar.edu.unju.fi.mapper.ClienteMapper;
import ar.edu.unju.fi.model.Cliente;
import ar.edu.unju.fi.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteDTO crearCliente(ClienteDTO dto) {
        log.info("Creando nuevo cliente: {}", dto.getNombreRazonSocial());

        Cliente cliente = ClienteMapper.toEntity(dto);
        Cliente guardado = clienteRepository.save(cliente);

        log.info("Cliente guardado exitosamente con ID: {}", guardado.getId());
        return ClienteMapper.toDTO(guardado);
    }

    public ClienteDTO buscarPorDocumentoOCuit(String doc) {
        log.info("Buscando cliente con documento/CUIT: {}", doc);

        Cliente cliente = clienteRepository.findByDocumentoOCuit(doc);
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado con documento o CUIT: " + doc);
        }

        return ClienteMapper.toDTO(cliente);
    }
}
