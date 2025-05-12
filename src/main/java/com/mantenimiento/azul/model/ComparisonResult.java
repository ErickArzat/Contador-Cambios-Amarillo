package com.mantenimiento.azul.model;

import java.util.HashSet;
import java.util.Set;

public class ComparisonResult {
    private Set<String> addedFiles = new HashSet<>();
    private Set<String> removedFiles = new HashSet<>();
    private Set<String> unchangedFiles = new HashSet<>();
    private Set<String> modifiedFiles = new HashSet<>();

    public Set<String> getAddedFiles() {
        return addedFiles;
    }

    public void setAddedFiles(Set<String> addedFiles) {
        this.addedFiles = addedFiles;
    }

    public Set<String> getRemovedFiles() {
        return removedFiles;
    }

    public void setRemovedFiles(Set<String> removedFiles) {
        this.removedFiles = removedFiles;
    }

    public Set<String> getUnchangedFiles() {
        return unchangedFiles;
    }

    public void setUnchangedFiles(Set<String> unchangedFiles) {
        this.unchangedFiles = unchangedFiles;
    }

    public Set<String> getModifiedFiles() {
        return modifiedFiles;
    }

    public void setModifiedFiles(Set<String> modifiedFiles) {
        this.modifiedFiles = modifiedFiles;
    }
}

