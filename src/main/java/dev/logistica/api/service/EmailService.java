package dev.logistica.api.service;

import dev.logistica.api.dto.DatosEmailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviar(String para, String asunto, String cuerpoHtml) {

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(cuerpoHtml, true);
            helper.setFrom("empresag5envios@gmail.com");

            mailSender.send(mensaje);

        } catch (MessagingException e) {
            throw new IllegalArgumentException("Error al enviar correo: " + e.getMessage());
        }
    }
    public void enviarEmailEnvioRegistrado(DatosEmailDTO datos) {
        String asunto = "¡Tu envío " + datos.getCodigo() + " ha sido registrado!";
        String cuerpoHtml = this.plantillaEnvioRegistrado(datos); // Usa la plantilla

        this.enviar(datos.getEmailPara(), asunto, cuerpoHtml);
    }

    public void enviarEmailEnvioEntregado(DatosEmailDTO datos) {
        String asunto = "¡Tu pedido " + datos.getCodigo() + " ha sido entregado!";
        String cuerpoHtml = this.plantillaEnvioEntregado(datos); // Usa la plantilla

        this.enviar(datos.getEmailPara(), asunto, cuerpoHtml);
    }

    private String plantillaEnvioRegistrado(DatosEmailDTO datos) {
        String html = """
    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden; background-color: #ffffff;">
        <!-- CAMBIO AQUÍ: background: #000000 (Negro Puro) para fusionarse con tu logo -->
        <div style="background: #000000; padding: 25px; text-align: center; color: white;">
            <img src="https://cdn.dribbble.com/userupload/31071214/file/original-097ef1c4344add3ec697f411c924aac0.jpg?resize=752x&vertical=center" width="60" style="display: block; margin: 0 auto 10px auto;"/>
            <h2 style="margin-top: 10px; margin-bottom: 5px; font-weight: 600; color: #ffffff;">¡Envío Registrado!</h2>
            <p style="margin: 0; font-size: 14px; color: #cccccc;">Tu envío ha sido registrado exitosamente.</p>
        </div>

        <div style="padding: 25px;">
            <div style="background: #f8f9fa; color: #000000; padding: 15px; text-align: center;
                        font-weight: bold; border-radius: 5px; border: 1px solid #e9ecef; font-size: 16px; margin-bottom: 25px;">
                ESTADO: GENERADO
            </div>

            <table style="width: 100%%; border-collapse: collapse; color: #333333;">
                <tr>
                    <td style="padding: 10px 0; border-bottom: 1px solid #e9ecef; width: 40%;"><b>Código de Envío:</b></td>
                    <td style="padding: 10px 0; border-bottom: 1px solid #e9ecef;">{{codigo}}</td>
                </tr>
                <tr>
                    <td style="padding: 10px 0; border-bottom: 1px solid #e9ecef;"><b>Remitente:</b></td>
                    <td style="padding: 10px 0; border-bottom: 1px solid #e9ecef;">{{remitente}}</td>
                </tr>
                <tr>
                    <td style="padding: 10px 0;"><b>Destinatario:</b></td>
                    <td style="padding: 10px 0;">{{destinatario}}</td>
                </tr>
            </table>

            <div style="text-align: center; margin-top: 40px;">
                <!-- CORRECCIÓN AQUÍ: Solo una comilla al inicio y al final -->
                <a href="http://localhost:8080/api/reportes/envios/comprobante/{{codigo}}"
                   style="padding: 14px 30px; background: #4a7c59; color: white;
                          text-decoration: none; border-radius: 5px; font-weight: bold;
                          display: inline-block; font-size: 16px; transition: background-color 0.3s ease;">
                   GENERAR COMPROBANTE
                </a>
            </div>
        </div>

        <div style="background: #f8f9fa; padding: 18px; text-align: center; font-size: 12px; color: #6c757d; border-top: 1px solid #e0e0e0;">
            Este es un correo automático. No respondas este mensaje.
            UNJU Facultad de Ingeniería © 2025
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
    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden; background-color: #ffffff;">
        <!-- Encabezado Negro G5 -->
        <div style="background: #000000; padding: 25px; text-align: center; color: white;">
            <img src="https://cdn.dribbble.com/userupload/31071214/file/original-097ef1c4344add3ec697f411c924aac0.jpg?resize=752x&vertical=center" width="60" style="display: block; margin: 0 auto 10px auto;"/>
            <h2 style="margin-top: 10px; margin-bottom: 5px; font-weight: 600; color: #ffffff;">¡Pedido Entregado!</h2>
            <p style="margin: 0; font-size: 14px; color: #cccccc;">Tu pedido ha llegado a destino correctamente.</p>
        </div>

        <div style="padding: 25px;">
            <!-- Badge de Estado Verde Éxito -->
            <div style="background: #d4edda; color: #155724; padding: 15px; text-align: center;
                        font-weight: bold; border-radius: 5px; border: 1px solid #c3e6cb; font-size: 16px; margin-bottom: 25px;">
                ESTADO: ENTREGADO
            </div>

            <p style="color: #333; font-size: 16px;">Hola <b>{{cliente}}</b>,</p>
            <p style="color: #555; line-height: 1.5;">
                Te confirmamos que tu envío ha sido entregado en la dirección pactada.
            </p>

            <table style="width: 100%%; margin-top: 20px; border-collapse: collapse; color: #333;">
                <tr>
                    <td style="padding: 10px 0; border-bottom: 1px solid #eee;"><b>Código de Seguimiento:</b></td>
                    <td style="padding: 10px 0; border-bottom: 1px solid #eee; text-align: right;">{{codigo}}</td>
                </tr>
                <tr>
                    <td style="padding: 10px 0; border-bottom: 1px solid #eee;"><b>Dirección de Entrega:</b></td>
                    <td style="padding: 10px 0; border-bottom: 1px solid #eee; text-align: right;">{{direccion}}</td>
                </tr>
            </table>

            <!-- Mensaje de Agradecimiento (Reemplaza al botón) -->
            <div style="text-align: center; margin-top: 35px; padding: 20px; background-color: #f8f9fa; border-radius: 8px; border: 1px dashed #ced4da;">
                <p style="margin: 0; color: #4a7c59; font-weight: bold; font-size: 18px;">
                    ¡Gracias por confiar en nosotros!
                </p>
                <p style="margin: 5px 0 0 0; color: #6c757d; font-size: 14px;">
                    Esperamos que disfrutes tu pedido.
                </p>
            </div>
        </div>

        <div style="background: #f8f9fa; padding: 18px; text-align: center; font-size: 12px; color: #6c757d; border-top: 1px solid #e0e0e0;">
            G5 Logística - Este es un correo automático.
            UNJU Facultad de Ingeniería © 2025
        </div>
    </div>
    """;

        return html
                .replace("{{cliente}}", datos.getDestinatario())
                .replace("{{codigo}}", datos.getCodigo())
                .replace("{{direccion}}", datos.getDireccion());
    }
}
