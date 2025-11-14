package ar.edu.unju.fi.mapper.viewsMapper;

import ar.edu.unju.fi.dto.views.EnvioViewEstadoDTO;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.Paquete;

import java.util.ArrayList;

public class EnvioViewEstadoMapper {
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
