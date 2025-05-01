package tests.jsonServiceTest;

import org.json.parser_v2.*;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestJsonService {


    public static String json = """
            {
               "name": "Alex",
               "address": {
                 "street": "Main Blvd.",
                 "city": "Darmstadt",
                 "country": "Germany",
                 "postalCode": 64285
               },
               "age": 30,
               "height": 1.82,
               "isProgrammer": true,
               "certificate": null,
               "education": [
                 {
                   "degree": "BSc",
                   "field": "Computer Science",
                   "year": 2017,
                   "preferences": {
                         "darkMode": true,
                         "fontSize": 14,
                         "language": "en",
                         "notifications": null
                    }
                 },
                 {
                   "degree": "MSc",
                   "field": "AI",
                   "year": 2019
                 }
               ],
               "languagesSpoken": [
                 "Romanian",
                 "German",
                 "English"
               ],
               "projects": [
                 "website",
                 {
                   "title": "app",
                   "completed": false
                 },
                 42,
                 null
               ],
               "preferences": {
                 "darkMode": true,
                 "fontSize": 14,
                 "language": "en",
                 "notifications": null,
                 "projects": [
                     "website",
                     {
                       "title": "a{}[]\\"pp",
                       "completed": false
                     },
                     42,
                     null
                ]
               }
             }
            """;

    public static String jsonSimple = """
            {
                "preferences": {
                    "darkMode": true,
                    "fontSize": 14,
                    "language": "en",
                    "notifications": null,
                    "address": {
                         "street": "Main Blvd.",
                         "city": "Darmstadt",
                         "country": "Germa{}[]\\"ny",
                         "postalCode": 64285
                   }
                }
            }
            """;

    public static void isJsonValidTest() {

        //init

        JsonParser.init(
                NumberFormat.getInstance(),
                JsonValidator.getInstance(),
                JsonFormat.getInstance()
        );
        JsonParser jsonParser = JsonParser.getInstance();

        //test

        Map<String, String> objs = jsonParser.parse(json);

        objs.forEach((k, v) -> System.out.println(k + "=" + v));

    }

    public static void main(String[] args) {
        isJsonValidTest();
    }
}
