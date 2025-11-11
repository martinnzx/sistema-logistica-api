package ar.edu.unju.fi.mapper;

import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.model.Paquete;
import ar.edu.unju.fi.model.PaqueteFragil;
import ar.edu.unju.fi.model.PaqueteRefrigerado;

public class PaqueteMapper {
    public static Paquete toEntity(PaqueteDTO dto){
        if (dto == null || dto.getTipo() == null || dto.getTipo().isBlank()) {
            throw new IllegalArgumentException("El tipo de paquete es obligatorio (Fragil o Refrigerado).");
        }

        if (dto.getTipo().equals("Refrigerado")){
            PaqueteRefrigerado paquete = new PaqueteRefrigerado();
            paquete.setId(dto.getId());
            paquete.setPesoKg(dto.getPesoKg());
            paquete.setCodigo(dto.getCodigo());
            paquete.setVolumenDm3(dto.getVolumenDm3());
            paquete.setTemperaturaObjetivo(dto.getTemperaturaObjetivo());
            paquete.setRangoMin(dto.getRangoMin());
            paquete.setRangoMax(dto.getRangoMax());
            paquete.setHorasMaxFueraDeFrio(dto.getHorasMaxFueraDeFrio());
            return paquete;
        } else {
            PaqueteFragil paquete = new PaqueteFragil();
            paquete.setId(dto.getId());
            paquete.setPesoKg(dto.getPesoKg());
            paquete.setCodigo(dto.getCodigo());
            paquete.setVolumenDm3(dto.getVolumenDm3());
            paquete.setNivelFragilidad(dto.getNivelFragilidad());
            paquete.setSeguroAdicional(dto.getSeguroAdicional());
            return paquete;
        }

    }

    public static PaqueteDTO toDTO(Paquete paquete){
        PaqueteDTO p = new PaqueteDTO();
        p.setId(paquete.getId());
        p.setPesoKg(paquete.getPesoKg());
        p.setVolumenDm3(paquete.getVolumenDm3());
        if(paquete instanceof PaqueteRefrigerado paqueteRefrigerado){
            p.setTipo("Refrigerado");
            p.setRangoMax(paqueteRefrigerado.getRangoMax());
            p.setRangoMin(paqueteRefrigerado.getRangoMin());
            p.setTemperaturaObjetivo(paqueteRefrigerado.getTemperaturaObjetivo());
            p.setHorasMaxFueraDeFrio(paqueteRefrigerado.getHorasMaxFueraDeFrio());
        }else{
            PaqueteFragil pf = new PaqueteFragil();
            p.setTipo("Fragil");
            p.setNivelFragilidad(pf.getNivelFragilidad());
            p.setSeguroAdicional(pf.getSeguroAdicional());
        }
        return p;
    }
}
