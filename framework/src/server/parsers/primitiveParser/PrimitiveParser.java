package framework.src.server.parsers.primitiveParser;

import framework.src.server.annotations.component.Component;

import java.util.Map;

/**
 * PrimitiveParser is a utility class that provides methods to parse primitive types
 * from strings and check if a class is a wrapper for a primitive type.
 * It supports parsing integers, doubles, booleans, and null values,
 * as well as identifying wrapper classes for primitives.
 */
@Component
public final class PrimitiveParser {

    private final Map<Class<?>, Class<?>> WRAPPERS_AND_PRIMITIVES = Map.ofEntries(
            Map.entry(Integer.class, int.class),
            Map.entry(Byte.class, byte.class),
            Map.entry(Character.class, char.class),
            Map.entry(Boolean.class, boolean.class),
            Map.entry(Double.class, double.class),
            Map.entry(Float.class, float.class),
            Map.entry(Long.class, long.class),
            Map.entry(Short.class, short.class),
            Map.entry(Void.class, void.class)
    );

    private PrimitiveParser() {}

    /**
     * Parses a string input into its corresponding primitive type.
     * Supports parsing integers, doubles, booleans, and null values.
     * If the input does not match any primitive type, it returns the input as a string.
     *
     * @param input the string input to parse
     * @return the parsed primitive type or the input string if no match is found
     */
    public Object parse(String input) {

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

    /**
     * Checks if the input string is a valid representation of a primitive type.
     * Valid types include integers, doubles, booleans, null, and strings.
     *
     * @param input the string input to check
     * @return true if the input matches a primitive type, false otherwise
     */
    public boolean isPrimitive(String input) {
        return input.matches("-?\\d+|(-?\\d*\\.\\d+([eE][-+]?\\d+)?)|true|false|null|\"\\w+\"");
    }

    public boolean isWrapperClass(Class<?> clazz) {
        return this.WRAPPERS_AND_PRIMITIVES.containsKey(clazz);
    }
}