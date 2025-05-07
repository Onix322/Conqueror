package org.services.jsonService.json.mapper;

public class JsonPrimitiveParser {
    public static Object parse(String input) {

        if (input.matches("-?\\d+")) {
            return Integer.parseInt(input);
        }
        if (input.matches("-?\\d*\\.\\d+([eE][-+]?\\d+)?")) {
            return Double.parseDouble(input);
        }
        if (input.matches("true|false")) {
            return Boolean.parseBoolean(input);
        }
        if (input.matches("null")) {
            return null;
        }

        if (input.charAt(0) == '\"' && input.charAt(input.length() - 1) == '\"') {
            input = input.substring(1, input.length() - 1);
        }

        return input;
    }

    public static boolean isJsonPrimitive(String input) {
        return input.matches("-?\\d+|(-?\\d*\\.\\d+([eE][-+]?\\d+)?)|true|false|null|\"\\w+\"");
    }
}