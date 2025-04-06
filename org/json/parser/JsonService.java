package org.json.parser;


import java.text.ParseException;
import java.util.Map;

//T is representing the class implementation
public interface JsonService {

    String generate(Object object);

    Object map(String stringLikeJson, Class<?> clazz) throws IllegalAccessException, ParseException;

    Map<String, Object> getProperties(String stringLikeJson) throws ParseException;
}
