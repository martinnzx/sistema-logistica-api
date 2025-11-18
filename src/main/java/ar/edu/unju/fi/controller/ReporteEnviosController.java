package ar.edu.unju.fi.controller;

import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.service.ReporteEnviosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes/envios")
@RequiredArgsConstructor
@Tag(name = "Reportes de Envíos", description = "Generación de reportes PDF y Excel filtrados por estado y rango de fechas")
public class ReporteEnviosController {

    private final ReporteEnviosService reporteService;

    @Operation(
            summary = "Generar reporte PDF de envíos",
            description = "Devuelve un archivo PDF con todos los envíos que hayan pasado al estado especificado dentro del rango de fechas."
    )
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> pdf(
            @Parameter(description = "Estado final del envío a filtrar", required = true)
            @RequestParam EstadoEnvio estado,

            @Parameter(description = "Fecha inicial (YYYY-MM-DD)", required = true,
                    schema = @Schema(type = "string", format = "date"))
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,

            @Parameter(description = "Fecha final (YYYY-MM-DD)", required = true,
                    schema = @Schema(type = "string", format = "date"))
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        byte[] archivo = reporteService.generarPdf(estado, desde, hasta);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("reporte-envios-" + estado + ".pdf")
                        .build()
        );

        return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
    }

    @Operation(
            summary = "Generar reporte Excel de envíos",
            description = "Devuelve un archivo Excel con todos los envíos que hayan cambiado al estado especificado dentro del rango de fechas."
    )
    @GetMapping("/excel")
    public ResponseEntity<byte[]> excel(
            @Parameter(description = "Estado final del envío a filtrar", required = true)
            @RequestParam EstadoEnvio estado,

            @Parameter(description = "Fecha inicial (YYYY-MM-DD)", required = true,
                    schema = @Schema(type = "string", format = "date"))
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,

            @Parameter(description = "Fecha final (YYYY-MM-DD)", required = true,
                    schema = @Schema(type = "string", format = "date"))
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        byte[] archivo = reporteService.generarExcel(estado, desde, hasta);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("reporte-envios-" + estado + ".xlsx")
                        .build()
        );

        return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
    }
}