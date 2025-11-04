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
                .remitente(ClienteMapper.toDTO(envio.getRemitente()))
                .destinatario(ClienteMapper.toDTO(envio.getDestinatario()))
                .direccionEntrega(envio.getDireccionEntrega())
                .estado(envio.getEstado())
                .comprobanteEntrega(envio.getComprobanteEntrega())
                .paquetes(paquetesDTO)
                .build();
    }

    public static Envio toEntity(EnvioDTO dto) {
        if (dto == null) {
            return null;
        }

        Envio envio = new Envio();
        envio.setRemitente(ClienteMapper.toEntity(dto.getRemitente()));
        envio.setDestinatario(ClienteMapper.toEntity(dto.getDestinatario()));
        envio.setDireccionEntrega(dto.getDireccionEntrega());
        envio.setEstado(dto.getEstado());
        envio.setComprobanteEntrega(dto.getComprobanteEntrega());

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
