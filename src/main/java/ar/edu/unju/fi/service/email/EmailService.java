package ar.edu.unju.fi.service.email;

public interface EmailService {
    void enviar(String para, String asunto, String cuerpoHtml);
}