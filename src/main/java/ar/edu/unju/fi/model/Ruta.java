package ar.edu.unju.fi.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @PastOrPresent
    private LocalDate fecha;

}
