package ar.edu.unju.fi.dto;

import ar.edu.unju.fi.model.EstadoEnvio;

import java.util.List;

public class EnvioDTO {
    private String remitente;
    private String destinatario;
    private String direccionEntrega;
    private EstadoEnvio estado;
    private String comprobanteEntrega;
    private List<PaqueteDTO> paquetes;

    /* CONSTRUCTOR VACIO */

    public EnvioDTO() {}

    /* CONSTRUCTOR CON TODOS LOS CAMPOS */

    public EnvioDTO(String remitente, String destinatario, String direccionEntrega, EstadoEnvio estado, String comprobanteEntrega, List<PaqueteDTO> paquetes) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.direccionEntrega = direccionEntrega;
        this.estado = estado;
        this.comprobanteEntrega = comprobanteEntrega;
        this.paquetes = paquetes;
    }

    /* GETTERS Y SETTERS */

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public EstadoEnvio getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
    }

    public String getComprobanteEntrega() {
        return comprobanteEntrega;
    }

    public void setComprobanteEntrega(String comprobanteEntrega) {
        this.comprobanteEntrega = comprobanteEntrega;
    }

    public List<PaqueteDTO> getPaquetes() {
        return paquetes;
    }

    public void setPaquetes(List<PaqueteDTO> paquetes) {
        this.paquetes = paquetes;
    }
}
