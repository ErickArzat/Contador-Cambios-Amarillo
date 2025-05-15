package com.mantenimiento.azul.utils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.element.Text;
import com.mantenimiento.azul.model.FormattedLine;
import com.mantenimiento.azul.model.LineCounter;

import java.util.ArrayList;
import java.util.List;

public class LineFormatter {

    private final int MAX_CONTENT_LENGTH = 80;
    
    public List<Text> formatLine(FormattedLine line, LineCounter AuxCounter) {
        List<Text> formattedTexts = new ArrayList<>();

        String content = line.content.stripLeading();
        String indentation = line.content.replaceAll("^(\\s*).*$", "$1");
        String comment = line.changeType != null ? " // línea " + line.changeType : "";

        int index = 0;
        boolean firstLine = true;

        while (index < content.length()) {
            String prefix = firstLine ? String.format("%4d | ", line.lineNumber) : "     |     ";
            String basePrefix = prefix + indentation;
            int availableLength = MAX_CONTENT_LENGTH - basePrefix.length();

            int remainingLength = content.length() - index;
            String part;

            if (remainingLength > availableLength) {
                int cutIndex = index + availableLength;
                int lastSpace = content.lastIndexOf(" ", cutIndex);
                if (lastSpace <= index) lastSpace = cutIndex;

                part = content.substring(index, Math.min(lastSpace, content.length()));
                index = lastSpace;
            } else {
                part = content.substring(index);
                index = content.length();
            }

            String fullText = basePrefix + part + (firstLine && index == content.length() ? comment : "");
            Text lineText = new Text(fullText);
            if (line.changeType != null) {
                switch (line.changeType) {
                    case "añadida":
                        lineText.setFontColor(ColorConstants.GREEN);
                        if(!prefix.equals("     |     ")) {
                            AuxCounter.incrementAdded();
                        }
                        
                        break;
                    case "borrada":
                        lineText.setFontColor(ColorConstants.RED);
                        if(!prefix.equals("     |     ")) {
                            AuxCounter.incrementAdded();
                        }
                        break;
                    case "modificada":
                        lineText.setFontColor(ColorConstants.ORANGE);
                        break;
                }
            }
            formattedTexts.add(lineText);
            firstLine = false;
        }

        return formattedTexts;
    }
}

