package dev.logistica.api.mapper;

import dev.logistica.api.dto.EnvioDTO;
import dev.logistica.api.dto.PaqueteDTO;
import dev.logistica.api.model.Envio;

import java.util.List;


public class EnvioMapper {

    private EnvioMapper() {
        throw new IllegalStateException("Utility class");
    }
    public static EnvioDTO toDTO(Envio envio) {
        if (envio == null) {
            return null;
        }

        List<PaqueteDTO> paquetesDTO = null;
        if (envio.getPaquetes() != null) {
            paquetesDTO = envio.getPaquetes().stream()
                    .map(PaqueteMapper::toDTO)
                    .toList();
        }

        return EnvioDTO.builder()
                .id(envio.getId())
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

        envio.setId(dto.getId());

        envio.setRemitente(ClienteMapper.toEntity(dto.getRemitente()));
        envio.setDestinatario(ClienteMapper.toEntity(dto.getDestinatario()));
        envio.setDireccionEntrega(dto.getDireccionEntrega());
        envio.setCodigoPostal(dto.getCodigoPostal());
        envio.setRequiereFrio(dto.getRequiereFrio());
        envio.setComprobanteEntrega(dto.getComprobanteEntrega());

        if (dto.getPaquetes() != null) {
            envio.setPaquetes(
                    dto.getPaquetes().stream()
                            .map(PaqueteMapper::toEntity)
                            .toList()
            );
        }

        return envio;
    }

}

