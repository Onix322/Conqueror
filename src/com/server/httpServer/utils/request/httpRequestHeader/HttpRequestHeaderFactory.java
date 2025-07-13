package src.com.server.httpServer.utils.request.httpRequestHeader;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Factory class for creating HttpRequestHeader instances from a header string.
 * This class provides a method to parse the header string and create an appropriate HttpRequestHeader object.
 */
public class HttpRequestHeaderFactory {
    public static HttpRequestHeader create(String header) {

        List<String> s = Arrays.stream(header.split(":\\s")).toList();

        String key = s.getFirst();
        String value = s.getLast();

        Pattern patternForValueArray = Pattern.compile(",\\s");

        if(patternForValueArray.matcher(value).find()){
            return new HttpRequestHeader(key, List.of(value.split(patternForValueArray.pattern())));
        }
        return new HttpRequestHeader(key ,value);
    }
}
