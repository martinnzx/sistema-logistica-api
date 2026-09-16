package dev.logistica.api.mapper.views;

import dev.logistica.api.dto.views.EnvioViewEstadoDTO;
import dev.logistica.api.model.Envio;
import dev.logistica.api.model.Paquete;

import java.util.ArrayList;

public class EnvioViewEstadoMapper {
    private EnvioViewEstadoMapper(){
        throw new IllegalStateException("Utility class");
    }
    public static EnvioViewEstadoDTO toDTO(Envio envio){
        EnvioViewEstadoDTO eV = new EnvioViewEstadoDTO();

        eV.setCodigoUnico(envio.getCodigoUnico());
        eV.setRemitenteNombre(envio.getRemitente().getNombreRazonSocial());
        eV.setDireccionEntrega(envio.getDireccionEntrega());
        eV.setCodigoPostal(envio.getCodigoPostal());
        eV.setEstado(envio.getEstado());
        eV.setDestinatarioNombre(envio.getDestinatario().getNombreRazonSocial());

        eV.setCodigoPaquetes(new ArrayList<>());

        for(Paquete p : envio.getPaquetes()){
            eV.getCodigoPaquetes().add(p.getCodigo());
        }
        return eV;
    }
}

