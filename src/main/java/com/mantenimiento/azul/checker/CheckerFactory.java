package com.mantenimiento.azul.checker;

public class CheckerFactory {
    public static Checker createCheckerChain() {
        Checker parenthesesChecker = new ParenthesesChecker();
        Checker leftCurlyBraceChecker = new LeftCurlyBraceChecker();
        Checker multiInstanceChecker = new MultiInstanceChecker();
        Checker endBreakChecker = new EndBreakChecker();

        parenthesesChecker.setNext(leftCurlyBraceChecker);
        leftCurlyBraceChecker.setNext(multiInstanceChecker);
        multiInstanceChecker.setNext(endBreakChecker);
      
        return parenthesesChecker;
    }
}
