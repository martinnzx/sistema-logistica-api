package ar.edu.unju.fi.mapper;

import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.Paquete;

import java.util.List;
import java.util.stream.Collectors;

public class EnvioMapper {

    public static EnvioDTO toDTO(Envio envio) {
        if (envio == null) {
            return null;
        }

        List<PaqueteDTO> paquetesDTO = null;
        if (envio.getPaquetes() != null) {
            paquetesDTO = envio.getPaquetes().stream()
                    .map(PaqueteMapper::toDTO)
                    .collect(Collectors.toList());
        }

        return EnvioDTO.builder()
                .id(envio.getId()) //
                .remitente(ClienteMapper.toDTO(envio.getRemitente()))
                .destinatario(ClienteMapper.toDTO(envio.getDestinatario()))
                .direccionEntrega(envio.getDireccionEntrega())
                .codigoPostal(envio.getCodigoPostal())
                .estado(envio.getEstado())
                .requiereFrio(envio.getRequiereFrio())
                .codigoUnico(envio.getCodigoUnico())
                .comprobanteEntrega(envio.getComprobanteEntrega())
                .paquetes(paquetesDTO)
                .build();
    }

    public static Envio toEntity(EnvioDTO dto) {
        if (dto == null) {
            return null;
        }

        Envio envio = new Envio();

        // Mapea el ID (importante para actualizaciones, será null para creaciones)
        envio.setId(dto.getId());

        envio.setRemitente(ClienteMapper.toEntity(dto.getRemitente()));
        envio.setDestinatario(ClienteMapper.toEntity(dto.getDestinatario()));
        envio.setDireccionEntrega(dto.getDireccionEntrega());
        envio.setCodigoPostal(dto.getCodigoPostal()); // <-- Crítico que esté
        envio.setRequiereFrio(dto.getRequiereFrio()); // <-- Crítico que esté
        envio.setComprobanteEntrega(dto.getComprobanteEntrega());

        // NO MAPEAR:
        // envio.setEstado(dto.getEstado()); // <-- BORRAR ESTO
        // envio.setCodigoUnico(dto.getCodigoUnico()); // <-- BORRAR ESTO

        if (dto.getPaquetes() != null) {
            envio.setPaquetes(
                    dto.getPaquetes().stream()
                            .map(PaqueteMapper::toEntity)
                            .collect(Collectors.toList())
            );
        }

        return envio;
    }

}
