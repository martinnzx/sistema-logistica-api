package ar.edu.unju.fi.mapper;

import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.model.Paquete;
import ar.edu.unju.fi.model.PaqueteFragil;
import ar.edu.unju.fi.model.PaqueteRefrigerado;

public class PaqueteMapper {
    public static Paquete toEntity(PaqueteDTO dto){
        if (dto == null) {
            return null;
        }
        Paquete paquete;
        if ("Refrigerado".equalsIgnoreCase(dto.getTipo())) {
            paquete = new PaqueteRefrigerado();

        } else {
            paquete = new PaqueteFragil();
        }

        paquete.setId(dto.getId());
        paquete.setPesoKg(dto.getPesoKg());
        paquete.setVolumenDm3(dto.getVolumenDm3());

        return paquete;
    }

    public static PaqueteDTO toDTO(Paquete paquete) {
        if (paquete == null) {
            return null;
        }

        PaqueteDTO dto = new PaqueteDTO();
        dto.setId(paquete.getId());
        dto.setPesoKg(paquete.getPesoKg());
        dto.setVolumenDm3(paquete.getVolumenDm3());
        if (paquete instanceof PaqueteRefrigerado) {
            dto.setTipo("Refrigerado");

        } else {
            dto.setTipo("Fragil");
        }
        return dto;
    }
}
