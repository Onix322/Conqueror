package org.json.parser_v2;

public class JsonValidator {

    private JsonValidator() {}

    private static class Init {
        public static final JsonValidator INSTANCE = new JsonValidator();
    }

    public static JsonValidator getInstance() {
        return Init.INSTANCE;
    }

    public boolean isValidJsonValue(String input) {
        return hasAllBrackets(input);
    }

    public boolean hasAllBrackets(String input){
        int open = 0;
        int close = 0;
        boolean inStringCharacter = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            switch (c) {
                case '"' -> inStringCharacter = !inStringCharacter;
                case '{', '[' -> {
                    if (inStringCharacter) continue;
                    open++;
                }
                case '}', ']' -> {
                    if (inStringCharacter) continue;
                    close++;
                }

            }
        }
        return open == close;
    }
}
