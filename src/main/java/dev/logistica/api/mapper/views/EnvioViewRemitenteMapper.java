package dev.logistica.api.mapper.views;

import dev.logistica.api.dto.views.EnvioViewRemitenteDTO;
import dev.logistica.api.model.Envio;
import dev.logistica.api.model.Paquete;

import java.util.ArrayList;

public class EnvioViewRemitenteMapper {
    private EnvioViewRemitenteMapper(){
        throw new IllegalStateException("Utility class");
    }
    public static EnvioViewRemitenteDTO toDTO(Envio envio){
        EnvioViewRemitenteDTO eV = new EnvioViewRemitenteDTO();

        eV.setCodigoUnico(envio.getCodigoUnico());
        eV.setRemitenteNombre(envio.getRemitente().getNombreRazonSocial());
        eV.setDireccionEntrega(envio.getDireccionEntrega());
        eV.setCodigoPostal(envio.getCodigoPostal());
        eV.setEstado(envio.getEstado());
        eV.setCorreoRemitente(envio.getRemitente().getEmail());
        eV.setDestinatarioNombre(envio.getDestinatario().getNombreRazonSocial());

        eV.setCodigoPaquetes(new ArrayList<>());

        for(Paquete p : envio.getPaquetes()){
            eV.getCodigoPaquetes().add(p.getCodigo());
        }
        return eV;
    }
}

