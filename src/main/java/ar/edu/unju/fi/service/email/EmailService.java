package ar.edu.unju.fi.service.email;

import ar.edu.unju.fi.dto.DatosEmailDTO;

public interface EmailService {
    void enviar(String para, String asunto, String cuerpoHtml);

    void enviarEmailEnvioRegistrado(DatosEmailDTO datos);

    void enviarEmailEnvioEntregado(DatosEmailDTO datos);
}