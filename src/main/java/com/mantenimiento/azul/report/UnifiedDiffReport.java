package com.mantenimiento.azul.report;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.element.Table;
import com.mantenimiento.azul.model.ComparisonResult;
import com.mantenimiento.azul.model.FileStats;
import com.mantenimiento.azul.model.FormattedLine;
import com.mantenimiento.azul.processor.DiffProcessor;
import com.mantenimiento.azul.utils.LineFormatter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class UnifiedDiffReport {

    private final LineFormatter formatter = new LineFormatter();
    String desktopPath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Reporte_Equipo_8";

    public void generate(ComparisonResult result, String oldPath, String newPath, String outputPdf) throws IOException {
        File directory = new File(desktopPath);
        if (!directory.exists()) directory.mkdirs();
        
        File outputFile = new File(directory, outputPdf);
        PdfWriter writer = new PdfWriter(outputFile.getAbsolutePath());
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("REPORTE UNIFICADO DE CAMBIOS").setBold().setTextAlignment(TextAlignment.CENTER));

        addRemovedFiles(result.getRemovedFiles(), doc, oldPath);
        addAddedFiles(result.getAddedFiles(), doc, newPath);
        addModifiedFiles(result.getModifiedFiles(), doc, oldPath, newPath);

        doc.close();
    }

    private void addRemovedFiles(Set<String> removedFiles, Document doc, String oldPath) throws IOException {
        for (String file : removedFiles) {
            Path path = Path.of(oldPath, file);

            if (!Files.exists(path)) continue;

            doc.add(new Paragraph("Archivo eliminado: " + file).setBold());
            List<String> lines = Files.readAllLines(path);

            for (int i = 0; i < lines.size(); i++) {
                Text t = new Text(String.format("%4d | %s", i + 1, lines.get(i))).setFontColor(ColorConstants.RED);
                doc.add(new Paragraph().add(t));
            }
            doc.add(new Paragraph("\n"));
        }
    }

    private void addAddedFiles(Set<String> addedFiles, Document doc, String newPath) throws IOException {
        for (String file : addedFiles) {
            Path path = Path.of(newPath, file);
            if (!Files.exists(path)) continue;
            doc.add(new Paragraph("Archivo añadido: " + file).setBold());
            List<String> lines = Files.readAllLines(path);
            for (int i = 0; i < lines.size(); i++) {
                Text t = new Text(String.format("%4d | %s", i + 1, lines.get(i))).setFontColor(ColorConstants.GREEN);
                doc.add(new Paragraph().add(t));
            }
            doc.add(new Paragraph("\n"));
        }
    }

    private void addModifiedFiles(Set<String> modifiedFiles, Document doc, String oldPath, String newPath) throws IOException {
        for (String file : modifiedFiles) {
            Path oldF = Path.of(oldPath, file);
            Path newF = Path.of(newPath, file);
            if (!Files.exists(oldF) || !Files.exists(newF)) continue;

            doc.add(new Paragraph("Archivo modificado: " + file).setBold());

            List<String> oldLines = Files.readAllLines(oldF);
            List<String> newLines = Files.readAllLines(newF);

            DiffProcessor processor = new DiffProcessor(oldLines, newLines);
            List<FormattedLine> diffs = processor.generateDiff();

            for (FormattedLine line : diffs) {
                for (Text t : formatter.formatLine(line)) {
                    doc.add(new Paragraph().add(t));
                }
            }

            doc.add(new Paragraph("\n"));
        }
    }

    public void printStatsReport(List<FileStats> results, String projectPath, String version) throws IOException {
        int totalPhysicalLines = 0;
        int totalLines = 0;

        String projectName = Path.of(projectPath).getFileName().toString();
        String folderName = "Reporte_estadisticas_" + projectName + "_" + version + ".pdf";

        File directory = new File(desktopPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        

        File outputFile = new File(directory, folderName);
        PdfWriter writer = new PdfWriter(outputFile.getAbsolutePath());
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("REPORTE DE ESTADÍSTICAS DE PROYECTO").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Programa: " + projectName).setBold().setFontSize(12));

        // Definimos la tabla con 6 columnas
        float[] columnWidths = {160f, 60f, 80f, 80f, 90f, 90f};
        Table table = new Table(columnWidths);
        table.setWidth(100);

        // Encabezado
        table.addHeaderCell(new Cell().add(new Paragraph("Clase").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Métodos").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("LOC físicas Clase").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Líneas Clase").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("LOC físicas Programa").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Líneas Programa").setBold()));

        // Datos
        for (FileStats stats : results) {
            totalPhysicalLines += stats.physicalLines();
            totalLines += stats.lines();

            for (var cls : stats.classes()) {
                table.addCell(cls.getName());
                table.addCell(String.valueOf(cls.getMethodCount()));
                table.addCell(String.valueOf(cls.getPhysicalLOC()));
                table.addCell(String.valueOf(cls.getLines()));
                table.addCell(""); // espacio en blanco
                table.addCell("");
            }
        }

        // Fila de totales
        table.addCell(new Cell(1, 4).add(new Paragraph("Totales").setBold()));
        table.addCell(new Paragraph(String.valueOf(totalPhysicalLines)).setBold());
        table.addCell(new Paragraph(String.valueOf(totalLines)).setBold());

        doc.add(table);
        doc.close();

        System.out.println("Reporte generado: " + folderName);
    }

}
