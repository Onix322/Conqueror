package org.json.parser_v2;

import org.exepltions.JsonNotValid;

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

    private List<String> createJsonSchema(String json) {
        String s = json.replaceAll("\s", "")
                .replaceAll("(?!.)\\{", "\n{\n")
                .replaceAll("(?:\s+|),(?!\s+|)", ",\n");

        List<String> schema = Arrays.stream(s.split("\n"))
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toCollection(LinkedList::new));

        System.out.println(s);
        if (this.isJson(schema)) {
            return schema;
        }
        throw new JsonNotValid("Not a valid json!");
    }


    public boolean isJson(List<String> lines) {
        String firstLine = lines.getFirst();
        String lastLine = lines.getLast();

        if (!firstLine.matches("\\{")) {
            throw new JsonNotValid("First char should be '{' not '" + lines.getFirst().charAt(0) + "'");
        }

        if (!lastLine.matches("}")) {
            throw new JsonNotValid("Last char should be '}' not '" + lines.getLast().charAt(lines.getLast().length() - 1) + "'");
        }

        lines.removeIf(String::isEmpty);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.contains(":")) continue;
            this.hasValidJsonLines(line, lines.indexOf("}") - 1 == i || lines.indexOf("},") - 1 == i);
        }

        return true;
    }

    private void hasValidJsonLines(String line, boolean skipComma) {
        String pattern = "\"\\w+\":(?:\".+\"|\\d+(?:\\.\\d+|)|true|false|null|\\{|\"(?:.|)\")";

        if (line.matches(".+\\{,$")) {
            throw new JsonNotValid("Line: " + line + " is NOT valid!");
        }

        if (skipComma && !line.matches(pattern)) {
            throw new JsonNotValid("Line: " + line + " is NOT valid!");
        }

        if(line.matches("}(?<!,)")){
            throw new JsonNotValid("Line: " + line + " must HAVE ',' at the end!");
        }

        if (!skipComma && !line.matches(pattern + ",") && !line.matches(".+\\{$") && line.matches(pattern + "(?<=.)\\n}\\n")) {
            throw new JsonNotValid("Line: " + line + " must HAVE ',' at the end!");
        }
    }

    public Map<String, Object> pullProperties(String json) {
        List<String> schema = this.createJsonSchema(json);
        return this.pullProperties(schema, 1, schema.lastIndexOf("}"), new LinkedHashMap<>(), new LinkedList<>());
    }

    private Map<String, Object> pullProperties(List<String> schema, Integer startIndex, Integer stopIndex, Map<String, Object> properties, List<Integer> skipIndexes) {


        // ! TODO Problems with properties after },
        // TODO verification for comma in },
        // TODO support arrays

        for (int i = startIndex; i < schema.size(); i++) {

            if (i == stopIndex) return properties;
            if (skipIndexes.contains(i)) continue;

            String line = schema.get(i);
            String[] prop = line.split(":");
            skipIndexes.add(i);
            if (prop.length == 1) continue;

            String key = prop[0];
            String value = prop[1];

            if (value.matches("\\{")) {
                properties.put(key, this.pullProperties(schema, i + 1, schema.indexOf("},"), new LinkedHashMap<>(), skipIndexes));
                schema.remove("},");
                continue;
            }

            if (value.charAt(value.length() - 1) == ',') {
                properties.put(key, value.substring(0, value.length() - 1));
                continue;
            }

            properties.put(key, value);
        }

        return properties;
    }
}
