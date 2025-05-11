package com.mantenimiento.azul.processor;

import java.util.*;

import com.mantenimiento.azul.model.FormattedLine;

public class DiffProcessor {
    private final List<String> oldLines;
    private final List<String> newLines;

    public DiffProcessor(List<String> oldLines, List<String> newLines) {
        this.oldLines = oldLines;
        this.newLines = newLines;
    }

    public List<FormattedLine> generateDiff() {
        List<FormattedLine> result = new ArrayList<>();
        int lineNumber = 1;
        int oldIndex = 0, newIndex = 0;

        while (oldIndex < oldLines.size() || newIndex < newLines.size()) {
            String oldLine = oldIndex < oldLines.size() ? oldLines.get(oldIndex) : null;
            String newLine = newIndex < newLines.size() ? newLines.get(newIndex) : null;

            if (Objects.equals(oldLine, newLine)) {
                result.add(new FormattedLine(lineNumber++, oldLine, null, false));
                oldIndex++;
                newIndex++;
            } else {
                boolean handled = false;

                
                if (oldLine != null && (newLine == null || !newLines.contains(oldLine))) {
                    result.add(new FormattedLine(lineNumber, oldLine, "borrada", false));
                    oldIndex++;
                    handled = true;
                }

                if (newLine != null && (oldLine == null || !oldLines.contains(newLine))) {
                    result.add(new FormattedLine(lineNumber, newLine, "añadida", false));
                    newIndex++;
                    handled = true;
                }

                if (handled) {
                    lineNumber++;
                } else {
                    oldIndex++;
                    newIndex++;
                    lineNumber++;
                }
            }
        }

        return result;
    }
}

