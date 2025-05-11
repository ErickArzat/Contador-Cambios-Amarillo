package com.mantenimiento.azul.utils;

import java.nio.file.Paths;
import java.util.List;

import com.mantenimiento.azul.model.FileStats;

public class ProjectReportPrinter {
    public static void print(List<FileStats> results, String projectPath) {
        int totalPhysicalLines = 0;
        int totalLines = 0;

        String projectName = Paths.get(projectPath).getFileName().toString();

        System.out.println("\nPrograma: " + projectName);
        System.out.printf("%-30s | %-10s | %-15s | %-15s | %-15s | %-15s |\n", 
            "Clase", "Metodos", "LOC f Clase", "Lineas Clase", "LOC f Programa", "Lineas Programa");
        System.out.println("=".repeat(117));

        for (FileStats stats : results) {
            totalPhysicalLines += stats.physicalLines();
            totalLines += stats.lines();
            stats.classes().forEach(cls -> {
                System.out.printf("%-30s | %-10s | %-15s | %-15s | %-15s | %-15s |\n", 
                    cls.getName(), cls.getMethodCount(), cls.getPhysicalLOC(), cls.getLines(), "", "");
            });
        }

        System.out.println("=".repeat(117));
        System.out.printf("%-30s | %-10s | %-15s | %-15s | %-15s | %-15s |\n", "", "", "", "", totalPhysicalLines, totalLines);
    }
}
