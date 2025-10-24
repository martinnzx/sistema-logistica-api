package ar.edu.unju.fi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.List;

public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @PastOrPresent
    private LocalDate fecha;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Envio> envios;
}
