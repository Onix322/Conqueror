package src.com.server.parsers.json.utils.navigator;

import src.com.server.parsers.json.utils.properties.JsonValue;
import src.com.server.parsers.json.utils.types.JsonArray;
import src.com.server.parsers.json.utils.types.JsonObject;
import src.com.server.parsers.json.utils.types.JsonType;

public class JsonNavigator {

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
