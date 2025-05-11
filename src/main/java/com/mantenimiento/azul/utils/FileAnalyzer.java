package com.mantenimiento.azul.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import java.nio.file.Path;

import com.mantenimiento.azul.exception.InvalidLineFormatException;
import com.mantenimiento.azul.model.FileStats;
import com.mantenimiento.azul.processor.CodeProcessor;

public class FileAnalyzer {
    public static List<FileStats> analyze(String path, CodeProcessor processor) {
        List<FileStats> results = new ArrayList<>();

        try {
            Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".java")) {
                        try {
                            results.add(processor.processFile(file));
                        } catch (InvalidLineFormatException e) {
                            System.err.println("Error en " + file + ": " + e.getMessage());
                        } catch (IOException e) {
                            System.err.println("No se pudo leer " + file);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Error al analizar el proyecto: " + e.getMessage());
        }

        return results;
    }
}
