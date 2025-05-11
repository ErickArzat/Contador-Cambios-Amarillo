package com.mantenimiento.azul.comparator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VersionComparator {
    
    public static class ComparisonResult {
        public Set<String> addedFiles = new HashSet<>();
        public Set<String> removedFiles = new HashSet<>();
        public Set<String> unchangedFiles = new HashSet<>();
        public Set<String> modifiedFiles = new HashSet<>();
    }

    public static ComparisonResult compare(Set<String> version1, Set<String> version2,
                                       String oldBasePath, String newBasePath) {
        ComparisonResult result = new ComparisonResult();

        Set<String> v1 = normalizePaths(version1);
        Set<String> v2 = normalizePaths(version2);

        result.removedFiles.addAll(v1);
        result.removedFiles.removeAll(v2);

        result.addedFiles.addAll(v2);
        result.addedFiles.removeAll(v1);

        Set<String> possiblyUnchanged = new HashSet<>(v1);
        possiblyUnchanged.retainAll(v2);

        getModifedPaths(possiblyUnchanged, oldBasePath, newBasePath, result);

        return result;
    }

    private static void getModifedPaths(Set<String> possiblyUnchanged, String oldBasePath, String newBasePath, ComparisonResult result){
        for (String relativePath : possiblyUnchanged) {
            Path oldFile = Paths.get(oldBasePath, relativePath);
            Path newFile = Paths.get(newBasePath, relativePath);

            try {
                byte[] oldBytes = Files.readAllBytes(oldFile);
                byte[] newBytes = Files.readAllBytes(newFile);

                if (Arrays.equals(oldBytes, newBytes)) {
                    result.unchangedFiles.add(relativePath);
                } else {
                    result.modifiedFiles.add(relativePath);
                }
            } catch (IOException e) {
                System.err.println("Error comparando archivos: " + relativePath + " - " + e.getMessage());
            }
        }
    }

    private static Set<String> normalizePaths(Set<String> paths) {
        Set<String> normalized = new HashSet<>();
        for (String path : paths) {
            normalized.add(path.replace("\\", "/")); 
        }
        return normalized;
    }
}