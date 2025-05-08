package org.utils.jsonService.json.formatter;

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

    /**
     * Main method that minifies the JSON input by removing spaces and new lines
     * outside of strings, and adding a space after each ':' that is not within a string.
     */
    public JsonFormatedString format(String string) {

        StringBuilder result = new StringBuilder();
        boolean inString = false;

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

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

            if (inString) {
                result.append(c);
            } else {
                if (Character.isWhitespace(c)) {
                    continue;
                }
                if (c == ':') {
                    result.append(": ");
                } else {
                    result.append(c);
                }
            }
        }

        return new JsonFormatedString(result.toString());
    }
}
