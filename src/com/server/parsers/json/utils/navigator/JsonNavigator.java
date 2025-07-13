package src.com.server.parsers.json.utils.navigator;

import src.com.server.parsers.json.utils.properties.JsonValue;
import src.com.server.parsers.json.utils.types.JsonArray;
import src.com.server.parsers.json.utils.types.JsonObject;
import src.com.server.parsers.json.utils.types.JsonType;

/**
 * JsonNavigator is a utility class that provides methods to navigate through
 * JSON structures using a dot-separated path. It allows for easy access to
 * nested properties within JSON objects and arrays.
 */
public class JsonNavigator {

    /**
     * Navigates through a JSON structure based on the provided path.
     * The path is a dot-separated string where each segment can represent
     * either a property in a JSON object or an index in a JSON array.
     *
     * @param jsonType the root JSON type to start navigation from
     * @param path     the dot-separated path to navigate through the JSON structure
     * @return the JsonValue found at the specified path, or null if not found
     */
    public static JsonValue navigate(JsonType jsonType, String path){

        String[] tokens = path.split("\\.");
        JsonValue jsonValue = null;

        for(String token : tokens){
            String key = tokenKey(token);

            if(!(jsonType instanceof JsonObject jsonObject)) continue;
            jsonValue = jsonObject.getProperty(key).value();

            if(!(jsonValue.get() instanceof JsonType)) return jsonValue;
            jsonType = jsonValue.get(JsonType.class);

            if(!(jsonType instanceof JsonArray jsonArray)) continue;
            int index = tokenId(token);

            if(index == -1) continue;
            jsonValue = jsonArray.get()[index];

            if(!(jsonValue.get() instanceof JsonValue secondValue)) return jsonValue;
            if(!(secondValue.get() instanceof JsonType)){
                jsonValue = secondValue.get(JsonValue.class);
            }
            jsonType = secondValue.get(JsonType.class);
        }

        return jsonValue;
    }
    private static int tokenId(String token){
        String numberString = token.replaceAll("\\D+", "");
        if(numberString.isBlank()) return -1;
        return Integer.parseInt(numberString);
    }
    private static String tokenKey(String token){
        return token.replaceAll("\\W+|\\d+", "");
    }
}
