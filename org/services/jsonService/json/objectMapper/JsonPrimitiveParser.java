package org.services.jsonService.json.objectMapper;

public class JsonPrimitiveParser {
    public static Object parse(String input) {

        if (input.startsWith("\"") && input.endsWith("\"")) {
            input = input.substring(1, input.length() - 1);
        }
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
        return input;
    }

    public static boolean isJsonPrimitive(String input) {
        return input.matches("-?\\d+|(-?\\d*\\.\\d+([eE][-+]?\\d+)?)|true|false|null");
    }
}