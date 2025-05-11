package com.mantenimiento.azul.report;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.mantenimiento.azul.comparator.VersionComparator.ComparisonResult;
import com.mantenimiento.azul.model.FormattedLine;
import com.mantenimiento.azul.processor.DiffProcessor;
import com.mantenimiento.azul.utils.LineFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class UnifiedDiffReport {

    private final LineFormatter formatter = new LineFormatter();

    public void generate(ComparisonResult result, String oldPath, String newPath, String outputPdf) throws IOException {

        PdfWriter writer = new PdfWriter(outputPdf);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("REPORTE UNIFICADO DE CAMBIOS").setBold().setTextAlignment(TextAlignment.CENTER));

        addRemovedFiles(result.removedFiles, doc, oldPath);
        addAddedFiles(result.addedFiles, doc, newPath);
        addModifiedFiles(result.modifiedFiles, doc, oldPath, newPath);

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
}
