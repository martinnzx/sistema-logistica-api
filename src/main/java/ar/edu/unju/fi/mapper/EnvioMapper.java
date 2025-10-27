package ar.edu.unju.fi.mapper;

import ar.edu.unju.fi.dto.EnvioDTO;
import ar.edu.unju.fi.model.Envio;

public class EnvioMapper {

    public static EnvioDTO toDTO(Envio envio) {
        if (envio == null) {
            return null;
        }

        return new EnvioDTO(
                envio.getRemitente(),
                envio.getDestinatario(),
                envio.getDireccionEntrega(),
                envio.getEstado(),
                envio.getComprobanteEntrega()
                // FIXME: Falta mapear la lista de paquetes
        );
    }
}
