package src.com.server.parsers.json.utils.formatter;

import src.com.server.annotations.component.Component;

/**
 * JsonFormat is a utility class that provides methods to format JSON strings.
 * It minifies the JSON input by removing unnecessary spaces and new lines,
 * while ensuring that the structure of the JSON remains intact.
 */
@Component
public final class JsonFormat {

    private JsonFormat() {
    }

    /**
     * Main method that minifies the JSON input by removing spaces and new lines
     * outside strings, and adding a space after each ':' that is not within a string.
     * * @param string the JSON string to be formatted
     * @return a JsonFormatedString object containing the formatted JSON string
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
