package org.json.parser_v2.json;

/*
 *Its role is to transform a string into a json format.
 */
public class JsonFormat {

    private JsonFormat() {
    }

    private static class Init {
        private static final JsonFormat INSTANCE = new JsonFormat();
    }

    public static JsonFormat getInstance() {
        return Init.INSTANCE;
    }

    public JsonString format(String string) {

        String lineJson = string.replaceAll("\n\\s+", "")
                .replaceAll("\r\n", "")
                .replaceAll("\n", "");

        return new JsonString(lineJson);
    }
}
