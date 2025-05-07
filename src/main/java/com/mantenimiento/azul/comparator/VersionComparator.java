package com.mantenimiento.azul.comparator;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class VersionComparator {
    public static class ComparisonResult{
        public Set<Path> addedFiles = new HashSet<>();
        public Set<Path> removedFiles = new HashSet<>();
        public Set<Path> unchangedFiles = new HashSet<>();
    }

    public static ComparisonResult compare(Set<Path> version1, Set<Path> version2){
        ComparisonResult result = new ComparisonResult();

        result.removedFiles.addAll(version1);
        result.removedFiles.removeAll(version2);

        result.addedFiles.addAll(version2);
        result.addedFiles.removeAll(version1);

        result.unchangedFiles.addAll(version1);
        result.unchangedFiles.retainAll(version2);

        return result;
    }
}
