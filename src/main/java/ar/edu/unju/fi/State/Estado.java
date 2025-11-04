package ar.edu.unju.fi.State;

import ar.edu.unju.fi.Repository.HistorialEnvioRepository;
import ar.edu.unju.fi.model.Envio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_estado")
public abstract class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    protected DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

    @Transient
    protected DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    protected LocalDateTime getAhora() {
        return LocalDateTime.now();
    }

    public abstract void FlujoEnvio(Envio envio, HistorialEnvioRepository historialRepo);
}