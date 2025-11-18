package ar.edu.unju.fi.service.email;

import ar.edu.unju.fi.dto.DatosEmailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void enviar(String para, String asunto, String cuerpoHtml) {

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(cuerpoHtml, true);
            helper.setFrom("martinnnxz@gmail.com");

            mailSender.send(mensaje);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo: " + e.getMessage());
        }
    }
    @Override
    public void enviarEmailEnvioRegistrado(DatosEmailDTO datos) {
        String asunto = "¡Tu envío " + datos.getCodigo() + " ha sido registrado!";
        String cuerpoHtml = this.plantillaEnvioRegistrado(datos); // Usa la plantilla

        this.enviar(datos.getEmailPara(), asunto, cuerpoHtml);
    }

    @Override
    public void enviarEmailEnvioEntregado(DatosEmailDTO datos) {
        String asunto = "¡Tu pedido " + datos.getCodigo() + " ha sido entregado!";
        String cuerpoHtml = this.plantillaEnvioEntregado(datos); // Usa la plantilla

        this.enviar(datos.getEmailPara(), asunto, cuerpoHtml);
    }

    private String plantillaEnvioRegistrado(DatosEmailDTO datos) {
        String html = """
        <div style="font-family: Arial; max-width: 600px; margin:auto; border:1px solid #ddd;">
            <div style="background:#007bff; padding:20px; text-align:center; color:white;">
                <img src="https://cdn-icons-png.flaticon.com/512/2962/2962288.png" width="80"/>
                <h2>¡Envío Registrado!</h2>
                <p>Tu envío ha sido registrado exitosamente.</p>
            </div>

            <div style="padding:20px;">
                <div style="background:#ffd633; padding:10px; text-align:center;
                            font-weight:bold; border-radius:5px;">
                    ESTADO: GENERADO
                </div>

                <table style="width:100%%; margin-top:20px;">
                    <tr><td><b>Código de Envío:</b></td><td>{{codigo}}</td></tr>
                    <tr><td><b>Remitente:</b></td><td>{{remitente}}</td></tr>
                    <tr><td><b>Destinatario:</b></td><td>{{destinatario}}</td></tr>
                </table>

                <div style="text-align:center; margin-top:30px;">
                    <a href="http://localhost:8080/api/envios/codigo/{{codigo}}"
                       style="padding:10px 20px; background:#28a745; color:white;
                              text-decoration:none; border-radius:5px;">
                       VER SEGUIMIENTO
                    </a>
                </div>
            </div>

            <div style="background:#f1f1f1; padding:10px; text-align:center; font-size:12px;">
                Este es un correo automático. No respondas este mensaje.
            </div>
        </div>
        """;

        return html
                .replace("{{codigo}}", datos.getCodigo())
                .replace("{{remitente}}", datos.getRemitente())
                .replace("{{destinatario}}", datos.getDestinatario());
    }
    private String plantillaEnvioEntregado(DatosEmailDTO datos) {
        String html = """
        <div style="font-family: Arial; max-width: 600px; margin:auto; border:1px solid #ddd;">
            <div style="background:#28a745; padding:20px; text-align:center; color:white;">
                <img src="https://cdn-icons-png.flaticon.com/512/2962/2962288.png" width="80"/>
                <h2>¡Pedido Entregado!</h2>
                <p>Tu pedido ha llegado a destino.</p>
            </div>

            <div style="padding:20px;">
                <div style="background:#1e7e34; padding:10px; text-align:center;
                            font-weight:bold; border-radius:5px; color:white;">
                    ESTADO: ENTREGADO
                </div>

                <p>Hola <b>{{cliente}}</b>,</p>
                <p>
                    Confirmamos que tu pedido N° <b>{{codigo}}</b> fue entregado correctamente.
                </p>

                <h4>Detalles:</h4>
                <p><b>Dirección de Entrega:</b> {{direccion}}</p>
                <p><b>Código de Seguimiento:</b> {{codigo}}</p>

                <div style="text-align:center; margin-top:30px;">
                    <a href="#" style="padding:10px 20px; background:#007bff; color:white;
                                       text-decoration:none; border-radius:5px;">
                        DEJAR MI OPINIÓN
                    </a>
                </div>
            </div>

            <div style="background:#f1f1f1; padding:10px; text-align:center; font-size:12px;">
                Gracias por utilizar nuestro servicio.
            </div>
        </div>
        """;
        return html
                .replace("{{cliente}}", datos.getDestinatario())
                .replace("{{codigo}}", datos.getCodigo())
                .replace("{{direccion}}", datos.getDireccion());
    }
}