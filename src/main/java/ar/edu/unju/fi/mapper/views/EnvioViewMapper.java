package ar.edu.unju.fi.mapper.views;

import ar.edu.unju.fi.dto.views.EnvioViewDTO;
import ar.edu.unju.fi.model.Envio;

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
