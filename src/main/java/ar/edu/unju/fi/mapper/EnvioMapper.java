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

        List<PaqueteDTO> paquetesDTO = envio.getPaquetes().stream()
                .map(PaqueteMapper::toDTO)
                .collect(Collectors.toList());

        return new EnvioDTO(
                envio.getRemitente(),
                envio.getDestinatario(),
                envio.getDireccionEntrega(),
                envio.getEstado(),
                envio.getComprobanteEntrega(),
                paquetesDTO
        );
    }

    public static Envio toEntity(EnvioDTO dto) {
        if (dto == null) {
            return null;
        }

        Envio envio = new Envio();
        envio.setRemitente(dto.getRemitente());
        envio.setDestinatario(dto.getDestinatario());
        envio.setDireccionEntrega(dto.getDireccionEntrega());
        envio.setEstado(dto.getEstado());
        envio.setComprobanteEntrega(dto.getComprobanteEntrega());

        if (dto.getPaquetes() != null) {
            List<Paquete> paquetes = dto.getPaquetes().stream()
                    .map(PaqueteMapper::toEntity)
                    .collect(Collectors.toList());
            envio.setPaquetes(paquetes);
        }

        return envio;
    }

}
