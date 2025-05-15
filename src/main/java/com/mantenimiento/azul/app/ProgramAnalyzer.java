package com.mantenimiento.azul.app;

import java.util.Scanner;
import java.util.Set;
import java.util.List;

import com.itextpdf.io.exceptions.IOException;
import com.mantenimiento.azul.checker.Checker;
import com.mantenimiento.azul.checker.CheckerFactory;
import com.mantenimiento.azul.comparator.VersionComparator;
import com.mantenimiento.azul.model.ComparisonResult;
import com.mantenimiento.azul.model.FileStats;
import com.mantenimiento.azul.processor.CodeProcessor;
import com.mantenimiento.azul.report.UnifiedDiffReport;
import com.mantenimiento.azul.utils.FileAnalyzer;
import com.mantenimiento.azul.utils.FileUtils;

public class ProgramAnalyzer {
    private final Scanner scanner = new Scanner(System.in);

    public void run(){
        try {
            while (true) {
            String oldVersionPath = prompt("Introduzca la ruta de la versión anterior: ");
            String newVersionPath = prompt("Introduzca la ruta de la nueva versión: ");

            Set<String> oldFiles = FileUtils.collectJavaFiles(oldVersionPath);
            Set<String> newFiles = FileUtils.collectJavaFiles(newVersionPath);

            ComparisonResult result = VersionComparator.compare(oldFiles, newFiles, oldVersionPath, newVersionPath);

            generateReport(result, oldVersionPath, newVersionPath);
            runCodeChecks(oldVersionPath, "Version_anterior");
            runCodeChecks(newVersionPath, "Version_nueva");
            
            printComparisonResults(result);

            if (!prompt("¿Desea analizar otra ruta? (y/n): ").matches("(?i)y|yes")) break;
        }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            this.scanner.close();
        }
        
    }

    private String prompt(String message) {
        System.out.print(message);
        return this.scanner.nextLine().trim();
    }

    private void generateReport(ComparisonResult result, String oldPath, String newPath) throws java.io.IOException {
        try {
            new UnifiedDiffReport().generate(result, oldPath, newPath, "Reporte_cambios.pdf");
            System.out.println("Reporte de cambios generado: Reporte_cambios.pdf");
        } catch (IOException e) {
            System.err.println("Error generando el PDF unificado: " + e.getMessage());
        }
    }

    private void printComparisonResults(ComparisonResult result) {
        System.out.println("\n=== Comparación de Versiones ===");
        printSection("Archivos nuevos", result.getAddedFiles(), "A");
        printSection("Archivos eliminados", result.getRemovedFiles(), "D");
        printSection("Archivos modificados", result.getModifiedFiles(), "M");
        printSection("Archivos sin cambios", result.getUnchangedFiles(), "=");
    }

    private void printSection(String title, Set<String> files, String marker) {
        System.out.println("\n" + title + " (" + files.size() + "):");
        files.forEach(p -> System.out.println("  [" + marker + "] " + p));
    }

    private void runCodeChecks(String path, String version) throws java.io.IOException {
        Checker checkerChain = CheckerFactory.createCheckerChain();
        CodeProcessor processor = new CodeProcessor(checkerChain);
        List<FileStats> results = FileAnalyzer.analyze(path, processor);
        if (!results.isEmpty()) {
            UnifiedDiffReport report = new UnifiedDiffReport();
            report.printStatsReport(results, path, version);
        }
    }
}
