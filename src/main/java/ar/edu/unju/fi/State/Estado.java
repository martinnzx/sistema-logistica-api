package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
public abstract class Estado {
    protected DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
    protected DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    protected final HistorialEnvioRepository historialEnvioRepository;

    public Estado(HistorialEnvioRepository historialEnvioRepository) {
        this.historialEnvioRepository = historialEnvioRepository;
    }
    protected LocalDateTime getAhora() {
        return LocalDateTime.now();
    }

    public abstract void FlujoEnvio(Envio envio);
}