package dev.logistica.api.mapper.views;

import dev.logistica.api.dto.views.EnvioViewDestinatarioDTO;
import dev.logistica.api.model.Envio;
import dev.logistica.api.model.Paquete;

import java.util.ArrayList;

public class EnvioViewDestinatarioMapper {
    EnvioViewDestinatarioMapper(){throw new IllegalStateException("Utility class");}
    public static EnvioViewDestinatarioDTO toDTO(Envio envio){
        EnvioViewDestinatarioDTO eV = new EnvioViewDestinatarioDTO();

        eV.setCodigoUnico(envio.getCodigoUnico());
        eV.setRemitenteNombre(envio.getRemitente().getNombreRazonSocial());
        eV.setDireccionEntrega(envio.getDireccionEntrega());
        eV.setCodigoPostal(envio.getCodigoPostal());
        eV.setEstado(envio.getEstado());
        eV.setCorreoDestinatario(envio.getDestinatario().getEmail());
        eV.setDestinatarioNombre(envio.getDestinatario().getNombreRazonSocial());

        eV.setCodigoPaquetes(new ArrayList<>());

        for(Paquete p : envio.getPaquetes()){
            eV.getCodigoPaquetes().add(p.getCodigo());
        }
        return eV;
    }
}

