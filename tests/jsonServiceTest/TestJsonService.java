package tests.jsonServiceTest;

import org.json.parser_v2.JsonServiceImpl;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestJsonService {




    public static String json = """
            {
               "name": "Alex",
               "age": 30,
               "height": 1.82,
               "isProgrammer": true,
               "certificate": null,
               "address": {
                 "street": "Main Blvd.",
                 "city": "Darmstadt",
                 "country": "Germany",
                 "postalCode": 64285
               },
               "languagesSpoken": [
                 "Romanian",
                 "German",
                 "English"
               ],
               "education": [
                 {
                   "degree": "BSc",
                   "field": "Computer Science",
                   "year": 2017
                 },
                 {
                   "degree": "MSc",
                   "field": "AI",
                   "year": 2019
                 }
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
                 "notifications": null
               }
             }
            """;

    public static String simpleJson ="""
            {
                "name":"Alex",
                "age": 30,
                "height":1.82,
                "isProgrammer": true,
                "certificate":null
            }
            """;

    public static void isJsonValidTest() {

        //init
        JsonServiceImpl.init(NumberFormat.getInstance());
        JsonServiceImpl jsonService = JsonServiceImpl.getInstance();

        //test
        List<String> js = jsonService.createJsonSchema(simpleJson);
        System.out.println(js);

        Map<String, Object> prop =  jsonService.pullProperties(js,new HashMap<>());
        System.out.println(prop);
    }

    public static void main(String[] args) {
        isJsonValidTest();
    }
}
