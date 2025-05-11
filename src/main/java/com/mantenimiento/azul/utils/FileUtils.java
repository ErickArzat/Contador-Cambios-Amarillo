package com.mantenimiento.azul.utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;


public class FileUtils {
    public static Set<String> collectJavaFiles(String projectPath) throws IOException {
        Set<String> javaFiles = new HashSet<>();
        Path root = Paths.get(projectPath).toAbsolutePath().normalize();

        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().toLowerCase().endsWith(".java")) {
                        javaFiles.add(root.relativize(file).toString());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return javaFiles;
    }
}

