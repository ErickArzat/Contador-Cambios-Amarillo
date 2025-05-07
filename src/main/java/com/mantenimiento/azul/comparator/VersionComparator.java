package com.mantenimiento.azul.comparator;

import java.util.HashSet;
import java.util.Set;

public class VersionComparator {
    
    public static class ComparisonResult {
        public Set<String> addedFiles = new HashSet<>();
        public Set<String> removedFiles = new HashSet<>();
        public Set<String> unchangedFiles = new HashSet<>();
    }

    public static ComparisonResult compare(Set<String> version1, Set<String> version2) {
        ComparisonResult result = new ComparisonResult();
        
        Set<String> v1 = normalizePaths(version1);
        Set<String> v2 = normalizePaths(version2);
        
        result.removedFiles.addAll(v1);
        result.removedFiles.removeAll(v2);
        
        result.addedFiles.addAll(v2);
        result.addedFiles.removeAll(v1);
        
        result.unchangedFiles.addAll(v1);
        result.unchangedFiles.retainAll(v2);
        
        return result;
    }

    private static Set<String> normalizePaths(Set<String> paths) {
        Set<String> normalized = new HashSet<>();
        for (String path : paths) {
            normalized.add(path.replace("\\", "/")); 
        }
        return normalized;
    }
}