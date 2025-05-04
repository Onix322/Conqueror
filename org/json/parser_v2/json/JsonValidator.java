package org.json.parser_v2.json;

import org.exepltions.JsonNotValid;
import org.json.parser_v2.json.formatter.JsonFormatedString;

public class JsonValidator {

    private JsonValidator() {}

    private static class Init {
        public static final JsonValidator INSTANCE = new JsonValidator();
    }

    public static JsonValidator getInstance() {
        return Init.INSTANCE;
    }

    public boolean isValidJsonValue(JsonFormatedString json) {

        if(!this.hasAllBrackets(json)){
            throw new JsonNotValid("A bracket is missing or a key has invalid word characters!");
        }
        return true;
    }

    public boolean hasAllBrackets(JsonFormatedString json){
        int open = 0;
        int close = 0;
        boolean inStringChar = false;
        for (int i = 0; i < json.getString().length(); i++) {
            char c = json.getString().charAt(i);

            inStringChar = this.toggleInString(c, i, json.getString(), inStringChar);
            if (inStringChar) continue;

            switch (c) {
                case '{', '[' -> open++;
                case '}', ']' -> close++;
            }
        }

        return open == close;
    }

    public boolean toggleInString(char c, int i, String string, boolean inString) {
        if (c == '"') {
            boolean escaped = false;
            int j = i - 1;
            while (j >= 0 && string.charAt(j) == '\\') {
                escaped = !escaped;
                j--;
            }
            if (!escaped) {
                inString = !inString;
            }
        }
        if (!inString && (c == '{' || c == '}' || c == '[' || c == ']' || c == ',' || c == ':')) {
            return inString;
        }

        return inString;
    }
}
