package ar.edu.unju.fi.service;

import ar.edu.unju.fi.dto.ClienteDTO;
import ar.edu.unju.fi.mapper.ClienteMapper;
import ar.edu.unju.fi.model.Cliente;
import ar.edu.unju.fi.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteDTO crearCliente(@Valid ClienteDTO dto) {
        log.info("Creando nuevo cliente: {}", dto.getNombreRazonSocial());

        Cliente cliente = ClienteMapper.toEntity(dto);
        Cliente guardado = clienteRepository.save(cliente);

        log.info("Cliente guardado exitosamente con ID: {}", guardado.getId());
        return ClienteMapper.toDTO(guardado);
    }
    @Transactional
    public ClienteDTO actualizarCliente(@Valid String doc, @Valid ClienteDTO dtoActualizado) {
        log.info("Actualizando cliente con documento/CUIT: {}", doc);

        Cliente clienteExistente = clienteRepository.findByDocumentoOCuitIgnoreCase(doc)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con documento o CUIT: " + doc));

        clienteExistente.setNombreRazonSocial(dtoActualizado.getNombreRazonSocial());
        clienteExistente.setTelefono(dtoActualizado.getTelefono());
        clienteExistente.setEmail(dtoActualizado.getEmail());
        clienteExistente.setDireccionPrincipal(dtoActualizado.getDireccionPrincipal());
        clienteExistente.setCodigoPostal(dtoActualizado.getCodigoPostal());

        Cliente guardado = clienteRepository.save(clienteExistente);

        log.info("Cliente actualizado exitosamente con ID: {}", guardado.getId());
        return ClienteMapper.toDTO(guardado);
    }
    public List<ClienteDTO> listarClientes() {
        log.info("Listando todos los clientes");
        List<Cliente> clientes = clienteRepository.findAll();

        return clientes.stream()
                .map(ClienteMapper::toDTO)
                .toList();
    }
    public ClienteDTO buscarPorDocumentoOCuit(String doc) {
        log.info("Buscando cliente con documento/CUIT: {}", doc);

        Cliente cliente = clienteRepository.findByDocumentoOCuitIgnoreCase(doc)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con documento o CUIT: " + doc));

        return ClienteMapper.toDTO(cliente);
    }
}
