package org.json.parser_v2;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class JsonServiceImpl implements JsonService {

    private final NumberFormat NUMBER_FORMAT;
    private JsonServiceImpl(NumberFormat numberFormat) {
        this.NUMBER_FORMAT = numberFormat;
    }

    private static class Init {
        private static JsonServiceImpl INSTANCE = null;
    }

    public synchronized static void init(NumberFormat numberFormat) {
        if (Init.INSTANCE == null) {
            Init.INSTANCE = new JsonServiceImpl(numberFormat);
        }
    }

    public static JsonServiceImpl getInstance() {
        if (Init.INSTANCE == null) {
            throw new IllegalStateException("JsonParser not initialized! call JsonServiceImpl.init()");
        }
        return Init.INSTANCE;
    }

    @Override
    public String generate(Object object) {
        return "";
    }


    @Override
    public <T> T map(String stringLikeJson, Class<T> clazz) throws IllegalAccessException, ParseException {
        //! de implementat
        return null;
    }

    @Override
    public Map<String, Object> getProperties(String stringLikeJson) {
        return Map.of();
    }

    private <T> Set<Field> getFields(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .collect(Collectors.toSet());
    }

    private Set<String> getFieldsNames(Set<Field> fields) {
        return fields.stream()
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    public List<String> createJsonSchema(String json) {
        String s = json.replaceAll("\s", "")
                .replaceAll("(?!.)\\{", "\n{\n")
                .replaceAll("}", "\n}\n")
                .replaceAll("(?:\s+|),(?!\s+|)", ",\n");

        List<String> schema = Arrays.stream(s.split("\n"))
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());

        System.out.println(s);
        if (this.isJson(schema)) {
            return schema;
        }
        throw new RuntimeException("Not a valid json!");
    }

    public Map<String, Object> pullProperties(List<String> schema, Map<String, Object> properties) {

        for (String line : schema) {
            System.out.println(line);
            if(!line.contains(":")) continue;
            String[] prop = line.split(":");
            String key = prop[0];
            String value = prop[1];
            String noCommaValue = value;
            if (value.charAt(value.length() - 1) == ',') {
                noCommaValue = value.substring(0, value.length() - 1);
            }
            try{
                properties.put(key, NUMBER_FORMAT.parse(noCommaValue));
            } catch (ParseException e){
                properties.put(key, noCommaValue);
            }
        }

        return properties;
    }

    public boolean isJson(List<String> lines) {
        String firstLine = lines.getFirst();
        String lastLine = lines.getLast();

        if (!firstLine.matches("\\{")) {
            throw new RuntimeException("First char should be '{' not '" + lines.getFirst().charAt(0) + "'");
        }

        if (!lastLine.matches("}")) {
            throw new RuntimeException("Last char should be '}' not '" + lines.getLast().charAt(lines.getLast().length() - 1) + "'");
        }

        lines.removeIf(String::isEmpty);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.contains(":")) continue;

            this.hasValidJsonLines(line, i == lines.size() - 2);
        }

        return true;
    }

    private void hasValidJsonLines(String line, boolean isLast) {
        String pattern = "\"\\w+\":(?:\".+\"|\\d+(?:\\.\\d+|)|true|false|null|\\{)";

        if(isLast && !line.matches(pattern + "(?!,)")){
            throw new RuntimeException("Line: " + line + " must NOT have ',' at the end");
        }

        if(!isLast && !line.matches(pattern + ",")){
            throw new RuntimeException("Line: " + line + " must have ',' at the end");
        };
    }
}
