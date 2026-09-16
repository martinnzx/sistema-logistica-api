package dev.logistica.api.mapper;

import dev.logistica.api.dto.ClienteDTO;
import dev.logistica.api.model.Cliente;

public class ClienteMapper {
    private ClienteMapper() {
        throw new IllegalStateException("Utility class");
    }
    public static ClienteDTO toDTO(Cliente cliente) {
        return ClienteDTO.builder()
                .id(cliente.getId())
                .nombreRazonSocial(cliente.getNombreRazonSocial())
                .documentoOCuit(cliente.getDocumentoOCuit())
                .telefono(cliente.getTelefono())
                .email(cliente.getEmail())
                .direccionPrincipal(cliente.getDireccionPrincipal())
                .codigoPostal(cliente.getCodigoPostal())
                .build();
    }

    public static Cliente toEntity(ClienteDTO dto) {
        if (dto == null) {
            return null;
        }
        Cliente cliente = new Cliente();

        cliente.setId(dto.getId());

        cliente.setNombreRazonSocial(dto.getNombreRazonSocial());
        cliente.setDocumentoOCuit(dto.getDocumentoOCuit());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setDireccionPrincipal(dto.getDireccionPrincipal());
        cliente.setCodigoPostal(dto.getCodigoPostal());

        return cliente;
    }
}

