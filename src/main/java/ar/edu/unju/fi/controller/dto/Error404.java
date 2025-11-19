package ar.edu.unju.fi.controller.dto;

public class Error404 {
    private String error;
    private String mensaje;

    public Error404(String error, String mensaje) {
        this.error = error;
        this.mensaje = mensaje;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}