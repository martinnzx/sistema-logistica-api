package ar.edu.unju.fi.mapper;

import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.dto.RutaDTO;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.Ruta;

import java.util.ArrayList;
import java.util.List;

public class RutaMapper {
    public static Ruta toEntity(RutaDTO dto) {
        if (dto == null) {
            return null;
        }

        Ruta ruta = new Ruta();
        ruta.setId(dto.getId());
        ruta.setFecha(dto.getFecha());
        /*
        if (dto.getEnvios() != null) {
            List<Envio> envios = new ArrayList<>();
            for (EnvioDTO envioDTO : dto.getEnvios()) {
                Envio envio = EnvioMapper.toEntity(envioDTO);
                envios.add(envio);
            }
            ruta.setEnvios(envios);
        }*/ /* FIXME: EnvioMapper falta ToEntity */

        return ruta;
    }
    public static RutaDTO toDto(Ruta ruta) {
        if (ruta == null) {
            return null;
        }

        RutaDTO dto = new RutaDTO();
        dto.setId(ruta.getId());
        dto.setFecha(ruta.getFecha());
        /*
        if (ruta.getEnvios() != null) {
            List<EnvioDTO> enviosDTO = new ArrayList<>();
            for (Envio envio : ruta.getEnvios()) {
                EnvioDTO envioDTO = EnvioMapper.toDto(envio);
                enviosDTO.add(envioDTO);
            }
            dto.setEnvios(enviosDTO);
        }
         */
        return dto;
    }
}
