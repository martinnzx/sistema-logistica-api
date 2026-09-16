package dev.logistica.api.service;

import dev.logistica.api.dto.views.PDFcomprobanteDTO;
import dev.logistica.api.enums.EstadoEnvio;
import dev.logistica.api.model.Envio;
import dev.logistica.api.model.HistorialEstadoEnvio;
import dev.logistica.api.repository.HistorialEstadoEnvioRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteEnviosService {

    private final HistorialEstadoEnvioRepository historialRepo;

    /**
     * Obtener historial filtrado por estado y rango de fecha
     */
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
            throw new IllegalArgumentException("Error al generar PDF: " + e.getMessage(), e);
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
            throw new IllegalArgumentException("Error al generar Excel: " + e.getMessage(), e);
        }
    }
    public byte[] generarComprobanteEnvio(PDFcomprobanteDTO datos) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // --- DEFINICIÓN DE COLORES ---
            BaseColor colorPrimario = new BaseColor(52, 73, 94);   // Azul Oscuro Profundo
            BaseColor colorFondo = new BaseColor(240, 242, 245);   // Gris muy suave

            // --- DEFINICIÓN DE FUENTES ---
            Font fontLogo = new Font(Font.FontFamily.HELVETICA, 50, Font.BOLD, colorPrimario); // G5 Grande
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, colorPrimario);
            Font fontSubtitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GRAY);
            Font fontEtiqueta = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);
            Font fontValor = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
            Font fontCodigo = new Font(Font.FontFamily.COURIER, 18, Font.BOLD, BaseColor.WHITE); // Courier para efecto "ticket"

            // -----------------------------------------------------
            // 1. LOGO "G5" (Texto en lugar de imagen)
            // -----------------------------------------------------
            Paragraph logo = new Paragraph("G5", fontLogo);
            logo.setAlignment(Element.ALIGN_CENTER);
            logo.setSpacingAfter(0); // Poco espacio para que el título quede pegado
            document.add(logo);

            // 2. Título principal
            Paragraph titulo = new Paragraph("COMPROBANTE DE ENVÍO", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5);
            document.add(titulo);

            // 3. Fecha de Emisión
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaFormateada = datos.getFechaHora() != null ? datos.getFechaHora().format(formatter) : "---";
            Paragraph subtitulo = new Paragraph("Emitido el: " + fechaFormateada, fontSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(25);
            document.add(subtitulo);

            // -----------------------------------------------------
            // 4. BANNER DE CÓDIGO (Destacado)
            // -----------------------------------------------------
            PdfPTable tableCodigo = new PdfPTable(1);
            tableCodigo.setWidthPercentage(90);

            PdfPCell cellCodigo = new PdfPCell(new Phrase(datos.getCodigo(), fontCodigo));
            cellCodigo.setBackgroundColor(colorPrimario);
            cellCodigo.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellCodigo.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellCodigo.setPaddingTop(10);
            cellCodigo.setPaddingBottom(10);
            cellCodigo.setBorder(Rectangle.NO_BORDER);
            // Bordes redondeados simulados (no nativos en iText 5 simple, usamos border radius nulo o imágenes, aquí plano es elegante)

            tableCodigo.addCell(cellCodigo);
            document.add(tableCodigo);
            document.add(new Paragraph("\n")); // Espacio

            // -----------------------------------------------------
            // 5. TABLA DE DETALLES
            // -----------------------------------------------------
            PdfPTable tableDatos = new PdfPTable(2);
            tableDatos.setWidthPercentage(100);
            tableDatos.setSpacingBefore(10);
            float[] widths = {0.35f, 0.65f};
            tableDatos.setWidths(widths);

            // SECCIÓN: ESTADO DEL ENVÍO
            agregarTituloSeccion(tableDatos, "ESTADO DEL ENVÍO", colorFondo, colorPrimario);
            agregarFila(tableDatos, "Estado Actual:", datos.getEstado(), fontEtiqueta, fontValor);
            // Se podría repetir la fecha si se desea, o dejarla solo arriba.

            // SECCIÓN: LOGÍSTICA
            agregarTituloSeccion(tableDatos, "DETALLES LOGÍSTICOS", colorFondo, colorPrimario);
            agregarFila(tableDatos, "Remitente:", datos.getRemitente(), fontEtiqueta, fontValor);
            agregarFila(tableDatos, "Destinatario:", datos.getDestinatario(), fontEtiqueta, fontValor);

            document.add(tableDatos);

            // -----------------------------------------------------
            // 6. OBSERVACIONES
            // -----------------------------------------------------
            if (datos.getObservaciones() != null && !datos.getObservaciones().isEmpty()) {
                document.add(new Paragraph("\n"));
                PdfPTable tableObs = new PdfPTable(1);
                tableObs.setWidthPercentage(100);

                PdfPCell cellObsTitle = new PdfPCell(new Phrase("OBSERVACIONES", fontEtiqueta));
                cellObsTitle.setBorder(Rectangle.BOTTOM);
                cellObsTitle.setBorderColor(BaseColor.LIGHT_GRAY);
                cellObsTitle.setPaddingBottom(5);
                tableObs.addCell(cellObsTitle);

                PdfPCell cellObsContent = new PdfPCell(new Phrase(datos.getObservaciones(), fontValor));
                cellObsContent.setBorder(Rectangle.NO_BORDER);
                cellObsContent.setPaddingTop(5);
                tableObs.addCell(cellObsContent);

                document.add(tableObs);
            }

            // -----------------------------------------------------
            // 7. PIE DE PÁGINA
            // -----------------------------------------------------
            document.add(new Paragraph("\n\n\n"));
            Font fontFooter = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
            Paragraph footer = new Paragraph("G5 Logística y Envíos - Documento generado automáticamente.", fontFooter);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new IllegalArgumentException("Error al generar el PDF: " + e.getMessage());
        }
    }

    // --- HELPERS ---

    private void agregarTituloSeccion(PdfPTable table, String titulo, BaseColor bgColor, BaseColor textColor) {
        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, textColor);
        PdfPCell cell = new PdfPCell(new Phrase(titulo.toUpperCase(), fontHeader));
        cell.setBackgroundColor(bgColor);
        cell.setColspan(2);
        cell.setPadding(6);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        cell.setBorderWidthBottom(1f);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0);
        table.addCell(cell);
    }

    private void agregarFila(PdfPTable table, String etiqueta, String valor, Font fontEtiqueta, Font fontValor) {
        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta, fontEtiqueta));
        celdaEtiqueta.setBorder(Rectangle.NO_BORDER);
        celdaEtiqueta.setPaddingTop(8);
        celdaEtiqueta.setPaddingBottom(8);
        celdaEtiqueta.setPaddingLeft(5);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor == null ? "-" : valor, fontValor));
        celdaValor.setBorder(Rectangle.NO_BORDER);
        celdaValor.setPaddingTop(8);
        celdaValor.setPaddingBottom(8);

        table.addCell(celdaEtiqueta);
        table.addCell(celdaValor);

        // Línea separadora sutil
        PdfPCell linea = new PdfPCell();
        linea.setColspan(2);
        linea.setFixedHeight(1f);
        linea.setBackgroundColor(new BaseColor(245, 245, 245)); // Gris muy muy claro
        linea.setBorder(Rectangle.NO_BORDER);
        table.addCell(linea);
    }
}

