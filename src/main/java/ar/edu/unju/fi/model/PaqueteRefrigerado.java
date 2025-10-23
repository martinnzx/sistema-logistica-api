package ar.edu.unju.fi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("REFRIGERADO")
public class PaqueteRefrigerado extends Paquete {

    @NotNull
    private Double temperaturaObjetivo;

    @NotNull
    private Double rangoMin;

    @NotNull
    private Double rangoMax;

    @PositiveOrZero
    private Integer horasMaxFueraDeFrio;
}
