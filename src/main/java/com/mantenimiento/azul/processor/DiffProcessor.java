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

                if (oldLine != null && newLine != null) {
                    double sim = similarity(oldLine.trim(), newLine.trim());
                    if (sim > 0.6) {
                        result.add(new FormattedLine(lineNumber++, newLine, "modificada", false));
                        oldIndex++;
                        newIndex++;
                        handled = true;
                    }
                }

                if (!handled) {
                    if (oldLine != null) {
                        result.add(new FormattedLine(lineNumber, oldLine, "borrada", false));
                        oldIndex++;
                        handled = true;
                    }
                    if (newLine != null) {
                        result.add(new FormattedLine(lineNumber, newLine, "añadida", false));
                        newIndex++;
                        handled = true;
                    }
                    if (handled) {
                        lineNumber++;
                    }
                }
            }
        }

        return result;
    }

    private double similarity(String a, String b) {
        int maxLength = Math.max(a.length(), b.length());
        if (maxLength == 0) return 1.0;
        int distance = levenshtein(a, b);
        return 1.0 - (double) distance / maxLength;
    }

    private int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        return dp[a.length()][b.length()];
    }
}

