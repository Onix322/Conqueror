package tests.jsonServiceTest;

import org.json.parser_v2.JsonServiceImpl;

import java.text.NumberFormat;
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
                   "year": 2017
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
                 "notifications": null
               }
             }
            """;


    public static void isJsonValidTest() {

        //init
        JsonServiceImpl.init(NumberFormat.getInstance());
        JsonServiceImpl jsonService = JsonServiceImpl.getInstance();

        //test
        List<String> lvlOneProperties = jsonService.listTheProperties(json);

        Map<String, String> pair = jsonService.divideKeyValue(lvlOneProperties);
        pair.forEach((k, v) -> System.out.println(k + "=" + v));

    }

    public static void main(String[] args) {
        isJsonValidTest();
    }
}
