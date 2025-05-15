package com.mantenimiento.azul.checker;

import java.util.List;
import com.mantenimiento.azul.exception.InvalidLineFormatException;
import com.mantenimiento.azul.utils.Regex;

public class ParenthesesChecker extends Checker {
    @Override
    public void pass(List<String> codeStream) throws InvalidLineFormatException {

        boolean pass = codeStream.stream().anyMatch(line -> Regex.ENDS_WITH_PARENTHESES.matcher(line).matches());
        if (pass) {
            throw new InvalidLineFormatException("No se permite terminar la línea de código con paréntesis");
        } else if (this.next != null) {
            this.next.pass(codeStream);
        }
    }
}
