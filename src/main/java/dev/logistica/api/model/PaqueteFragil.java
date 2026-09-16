package dev.logistica.api.model;

import dev.logistica.api.enums.NivelFragilidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("FRAGIL")
public class PaqueteFragil extends Paquete {

    @NotNull
    @Enumerated(EnumType.STRING)
    private NivelFragilidad nivelFragilidad;

    @NotNull
    private Boolean seguroAdicional;
}

