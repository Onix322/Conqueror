package org.json.parser_v2;

public class JsonValidator {

    private JsonValidator() {}

    private static class Init {
        public static final JsonValidator INSTANCE = new JsonValidator();
    }

    public static JsonValidator getInstance() {
        return Init.INSTANCE;
    }

    public boolean isValidJsonValue(JsonString json) {
        return hasAllBrackets(json);
    }

    public boolean hasAllBrackets(JsonString json){
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

    public boolean toggleInString(char c, int index, String jsonLine, boolean currentState) {
        if (c == '"' && index > 0) {
            if (jsonLine.charAt(index - 1) != '\\') {
                return !currentState;
            }
        }
        return currentState;
    }
}
