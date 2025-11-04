package ar.edu.unju.fi.mapper;

import ar.edu.unju.fi.dto.ClienteDTO;
import ar.edu.unju.fi.model.Cliente;

public class ClienteMapper {

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
        return Cliente.builder()
                .id(dto.getId())
                .nombreRazonSocial(dto.getNombreRazonSocial())
                .documentoOCuit(dto.getDocumentoOCuit())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .direccionPrincipal(dto.getDireccionPrincipal())
                .codigoPostal(dto.getCodigoPostal())
                .build();
    }
}
