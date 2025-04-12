package org.json.parser_v2;


import java.text.ParseException;
import java.util.Map;

//T is representing the class implementation
public interface JsonService {

    String generate(Object object);

    <T> T map(String stringLikeJson, Class<T> clazz) throws IllegalAccessException, ParseException;

    Map<String, Object> getProperties(String stringLikeJson) throws ParseException;
}
