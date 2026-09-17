package dev.logistica.api.controller.doc;

import dev.logistica.api.enums.EstadoEnvio;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

@RequestMapping("/api/reportes")
public interface ReporteEnviosApi {

    @GetMapping("/pdf")
    ResponseEntity<byte[]> pdf(
            @RequestParam EstadoEnvio estado,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    );

    @GetMapping("/excel")
    ResponseEntity<byte[]> excel(
            @RequestParam EstadoEnvio estado,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    );

    @GetMapping("/comprobante/{codigo}")
    ResponseEntity<byte[]> generarComprobante(@PathVariable String codigo);
}
