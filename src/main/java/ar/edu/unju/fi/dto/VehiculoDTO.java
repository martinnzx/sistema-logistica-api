package ar.edu.unju.fi.dto;

public class VehiculoDTO {
    private String patente;
    private Double capacidadMaxPesoKg;
    private Double capacidadMaxVolDm3;
    private Boolean refrigerado;

    /* CONSTRUCTOR VACIO */

    public VehiculoDTO() {}

    /* CONSTRUCTOR CON TODOS LOS CAMPOS */

    public VehiculoDTO(String patente, Double capacidadMaxPesoKg, Double capacidadMaxVolDm3, Boolean refrigerado) {
        this.patente = patente;
        this.capacidadMaxPesoKg = capacidadMaxPesoKg;
        this.capacidadMaxVolDm3 = capacidadMaxVolDm3;
        this.refrigerado = refrigerado;
    }

    /* GETTERS Y SETTERS */

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public Double getCapacidadMaxPesoKg() {
        return capacidadMaxPesoKg;
    }

    public void setCapacidadMaxPesoKg(Double capacidadMaxPesoKg) {
        this.capacidadMaxPesoKg = capacidadMaxPesoKg;
    }

    public Double getCapacidadMaxVolDm3() {
        return capacidadMaxVolDm3;
    }

    public void setCapacidadMaxVolDm3(Double capacidadMaxVolDm3) {
        this.capacidadMaxVolDm3 = capacidadMaxVolDm3;
    }

    public Boolean getRefrigerado() {
        return refrigerado;
    }

    public void setRefrigerado(Boolean refrigerado) {
        this.refrigerado = refrigerado;
    }
}
