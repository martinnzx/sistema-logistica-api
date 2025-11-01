package ar.edu.unju.fi.State;

import ar.edu.unju.fi.model.Envio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Estado {
    protected LocalDateTime ahora = LocalDateTime.now();
    protected DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
    protected DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public abstract void FlujoEnvio(Envio envio);

}