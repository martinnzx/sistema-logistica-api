package dev.logistica.api.controller;

import dev.logistica.api.dto.views.PDFcomprobanteDTO;
import dev.logistica.api.enums.EstadoEnvio;
import dev.logistica.api.service.EnvioService;
import dev.logistica.api.service.ReporteEnviosService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes/envios")
@RequiredArgsConstructor
public class ReporteEnviosController {

    private final ReporteEnviosService reporteService;
    private final EnvioService envioService;

        @GetMapping("/pdf")
    public ResponseEntity<byte[]> pdf(
                        @RequestParam EstadoEnvio estado,

                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,

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

        @GetMapping("/excel")
    public ResponseEntity<byte[]> excel(
                        @RequestParam EstadoEnvio estado,

                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,

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
        @GetMapping("/comprobante/{codigo}")
    public ResponseEntity<byte[]> generarComprobante(
                        @PathVariable String codigo
    ) {
        PDFcomprobanteDTO pdfDetails = envioService.obtenerCodigo(codigo);

        byte[] archivo = reporteService.generarComprobanteEnvio(pdfDetails);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF.toString())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("comprobante-" + codigo + ".pdf")
                                .build().toString())
                .body(archivo);
    }
}
