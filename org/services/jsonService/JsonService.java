package org.services.jsonService;


import org.services.jsonService.json.types.JsonObject;

//T is representing the class implementation
public interface JsonService {

    <T> T map(JsonObject jsonObject, Class<T> clazz);

    JsonObject parse(String json);
}
