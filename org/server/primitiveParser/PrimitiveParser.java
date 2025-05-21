package org.server.primitiveParser;

import org.server.processors.components.annotations.Singleton;

import java.util.Map;

@Singleton
public class PrimitiveParser {

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

    public boolean isPrimitive(String input) {
        return input.matches("-?\\d+|(-?\\d*\\.\\d+([eE][-+]?\\d+)?)|true|false|null|\"\\w+\"");
    }

    public boolean isWrapperClass(Class<?> clazz) {
        return this.WRAPPERS_AND_PRIMITIVES.containsKey(clazz);
    }
}