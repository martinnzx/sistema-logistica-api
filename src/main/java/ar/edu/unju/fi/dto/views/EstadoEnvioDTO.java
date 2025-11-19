package ar.edu.unju.fi.dto.views;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Data
public class EstadoEnvioDTO {
    @Schema(description = "Nota opcional sobre el cambio de estado", example = "El cliente no estaba en casa")
    private String observacion;
}
