package ar.edu.unju.fi.mapper;

import ar.edu.unju.fi.dto.PaqueteDTO;
import ar.edu.unju.fi.model.Paquete;
import ar.edu.unju.fi.model.PaqueteFragil;
import ar.edu.unju.fi.model.PaqueteRefrigerado;

public class PaqueteMapper {
    private static final String TIPO_REFRIGERADO = "Refrigerado";
    private static final String TIPO_FRAGIL = "Fragil";

    public static Paquete toEntity(PaqueteDTO dto){
        if (TIPO_REFRIGERADO.equalsIgnoreCase(dto.getTipo())){
            PaqueteRefrigerado paquete = new PaqueteRefrigerado();
            paquete.setPesoKg(dto.getPesoKg());
            paquete.setCodigo(dto.getCodigo());
            paquete.setVolumenDm3(dto.getVolumenDm3());
            paquete.setTemperaturaObjetivo(dto.getTemperaturaObjetivo());
            paquete.setRangoMin(dto.getRangoMin());
            paquete.setRangoMax(dto.getRangoMax());
            paquete.setHorasMaxFueraDeFrio(dto.getHorasMaxFueraDeFrio());
            return paquete;

        } else if (TIPO_FRAGIL.equalsIgnoreCase(dto.getTipo())) {
            PaqueteFragil paquete = new PaqueteFragil();
            paquete.setPesoKg(dto.getPesoKg());
            paquete.setCodigo(dto.getCodigo());
            paquete.setVolumenDm3(dto.getVolumenDm3());
            paquete.setNivelFragilidad(dto.getNivelFragilidad());
            paquete.setSeguroAdicional(dto.getSeguroAdicional());
            return paquete;
        } else {
            throw new IllegalArgumentException("Tipo de paquete no reconocido: " + dto.getTipo());
        }
    }

    public static PaqueteDTO toDTO(Paquete paquete){
        PaqueteDTO p = new PaqueteDTO();

        p.setPesoKg(paquete.getPesoKg());
        p.setVolumenDm3(paquete.getVolumenDm3());
        p.setCodigo(paquete.getCodigo());

        if(paquete instanceof PaqueteRefrigerado paqueteRefrigerado){
            p.setTipo(TIPO_REFRIGERADO);
            p.setRangoMax(paqueteRefrigerado.getRangoMax());
            p.setRangoMin(paqueteRefrigerado.getRangoMin());
            p.setTemperaturaObjetivo(paqueteRefrigerado.getTemperaturaObjetivo());
            p.setHorasMaxFueraDeFrio(paqueteRefrigerado.getHorasMaxFueraDeFrio());

        } else if (paquete instanceof PaqueteFragil paqueteFragil) {
            p.setTipo(TIPO_FRAGIL);
            p.setNivelFragilidad(paqueteFragil.getNivelFragilidad());
            p.setSeguroAdicional(paqueteFragil.getSeguroAdicional());
        }
        return p;
    }
}
