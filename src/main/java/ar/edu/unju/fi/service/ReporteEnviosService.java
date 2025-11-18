package ar.edu.unju.fi.service;

import ar.edu.unju.fi.enums.EstadoEnvio;
import ar.edu.unju.fi.model.Envio;
import ar.edu.unju.fi.model.HistorialEstadoEnvio;
import ar.edu.unju.fi.repository.HistorialEstadoEnvioRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteEnviosService {

    private final HistorialEstadoEnvioRepository historialRepo;

    /** Obtener historial filtrado por estado y rango de fecha */
    private List<HistorialEstadoEnvio> buscarHistorial(EstadoEnvio estado, LocalDate desde, LocalDate hasta) {

        LocalDateTime desdeDT = desde.atStartOfDay();
        LocalDateTime hastaDT = hasta.atTime(23, 59, 59);

        return historialRepo.findByEstadoNuevoAndFechaHoraBetween(estado, desdeDT, hastaDT);
    }

    /* =========================================================================
       =========================     PDF     ==================================
       ========================================================================= */

    public byte[] generarPdf(EstadoEnvio estado, LocalDate desde, LocalDate hasta) {

        List<HistorialEstadoEnvio> lista = buscarHistorial(estado, desde, hasta);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // Título
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titulo = new Paragraph("Reporte de Envíos", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(15);
            document.add(titulo);

            // Subtítulo
            Font subFont = new Font(Font.FontFamily.HELVETICA, 12);
            document.add(new Paragraph("Estado: " + estado, subFont));
            document.add(new Paragraph("Desde: " + desde + "   Hasta: " + hasta, subFont));
            document.add(new Paragraph("\n"));

            // Tabla
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);

            agregarHeaderPdf(table, "Código Envío");
            agregarHeaderPdf(table, "Remitente");
            agregarHeaderPdf(table, "Destinatario");
            agregarHeaderPdf(table, "Dirección Entrega");
            agregarHeaderPdf(table, "Fecha Estado");
            agregarHeaderPdf(table, "Estado");

            for (HistorialEstadoEnvio h : lista) {
                Envio e = h.getEnvio();
                table.addCell(texto(e.getCodigoUnico()));
                table.addCell(texto(e.getRemitente().getNombreRazonSocial()));
                table.addCell(texto(e.getDestinatario().getNombreRazonSocial()));
                table.addCell(texto(e.getDireccionEntrega()));
                table.addCell(texto(h.getFechaHora().toString()));
                table.addCell(texto(h.getEstadoNuevo().name()));
            }

            document.add(table);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }

    private void agregarHeaderPdf(PdfPTable table, String titulo) {
        PdfPCell cell = new PdfPCell(new Phrase(titulo));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }

    private String texto(String s) {
        return s == null ? "" : s;
    }

    /* =========================================================================
       =========================    EXCEL     ==================================
       ========================================================================= */

    public byte[] generarExcel(EstadoEnvio estado, LocalDate desde, LocalDate hasta) {

        List<HistorialEstadoEnvio> lista = buscarHistorial(estado, desde, hasta);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reporte");

            int rowNum = 0;

            // Título
            Row titulo = sheet.createRow(rowNum++);
            titulo.createCell(0).setCellValue("Reporte de Envíos");

            // Info
            Row info = sheet.createRow(rowNum++);
            info.createCell(0).setCellValue("Estado: " + estado);
            info.createCell(1).setCellValue("Desde: " + desde);
            info.createCell(2).setCellValue("Hasta: " + hasta);

            rowNum++;

            // Encabezados
            Row header = sheet.createRow(rowNum++);
            String[] columns = {
                    "Código Envío",
                    "Remitente",
                    "Destinatario",
                    "Dirección Entrega",
                    "Fecha Estado",
                    "Estado"
            };

            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            // Datos
            for (HistorialEstadoEnvio h : lista) {

                Envio e = h.getEnvio();
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(texto(e.getCodigoUnico()));
                row.createCell(1).setCellValue(texto(e.getRemitente().getNombreRazonSocial()));
                row.createCell(2).setCellValue(texto(e.getDestinatario().getNombreRazonSocial()));
                row.createCell(3).setCellValue(texto(e.getDireccionEntrega()));
                row.createCell(4).setCellValue(h.getFechaHora().toString());
                row.createCell(5).setCellValue(h.getEstadoNuevo().name());
            }

            // Auto size
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel: " + e.getMessage(), e);
        }
    }
}