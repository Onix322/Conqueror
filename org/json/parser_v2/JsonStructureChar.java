package org.json.parser_v2;

import java.util.Arrays;

public enum JsonStructureChar {
    CURLY_BRACKET_OPEN('{'),
    CURLY_BRACKET_CLOSED('}'),
    SQUARED_BRACKET_OPEN('['),
    SQUARED_BRACKET_CLOSED(']'),
    COMMA(','),
    COLON(':'),
    DOUBLE_QUOTE('"'),
    SPACE('\s');

    private final char CHAR;

    JsonStructureChar(char c) {
        this.CHAR = c;
    }

    public char getChar() {
        return CHAR;
    }

    public static boolean isStructureChar(char c){
        return Arrays.stream(JsonStructureChar.values())
                .anyMatch(jsonStructureChar -> jsonStructureChar.getChar() == c);
    }
}
