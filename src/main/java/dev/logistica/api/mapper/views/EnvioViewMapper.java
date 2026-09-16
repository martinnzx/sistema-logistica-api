package dev.logistica.api.mapper.views;

import dev.logistica.api.dto.views.EnvioViewDTO;
import dev.logistica.api.model.Envio;

public class EnvioViewMapper {
    private EnvioViewMapper(){
        throw new IllegalStateException("Utility class");
    }
    public static Envio toEntity(EnvioViewDTO dto) {
        return Envio.builder()
                .direccionEntrega(dto.getDireccionEntrega())
                .codigoPostal(dto.getCodigoPostal())
                .requiereFrio(dto.getRequiereFrio())
                .build();
    }
}

