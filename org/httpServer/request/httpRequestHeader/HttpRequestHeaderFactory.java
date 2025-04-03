package org.httpServer.request.httpRequestHeader;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class HttpRequestHeaderFactory {
    public static HttpRequestHeader<?> create(String header) {

        List<String> s = Arrays.stream(header.split(":\\s")).toList();

        String key = s.getFirst();
        String value = s.getLast();

        Pattern patternForValueArray = Pattern.compile(",\\s");

        if(patternForValueArray.matcher(value).find()){
            System.out.println("1!!!!!!!!!!!! HAS AN ARRAY !!!!!!!!!!!!!");

            return new HttpRequestHeader<List<String>>(key, List.of(value.split(patternForValueArray.pattern())));
        }

        return new HttpRequestHeader<String>(key ,value);
    }
}
