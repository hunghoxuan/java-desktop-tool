package com.rs2.core.components.evalex;

public class TokenizerException extends Expression.ExpressionException {
    public TokenizerException(String message, int characterPosition) {
        super(message, characterPosition);
    }
}
