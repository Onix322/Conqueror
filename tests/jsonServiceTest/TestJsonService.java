package tests.jsonServiceTest;

import org.json.parser_v2.json.JsonFormat;
import org.json.parser_v2.json.JsonParser;
import org.json.parser_v2.json.JsonValidator;
import org.json.parser_v2.json.properties.JsonValue;
import org.json.parser_v2.json.types.JsonArray;
import org.json.parser_v2.json.types.JsonObject;

import java.text.ParseException;

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

    public static void isJsonValidTest() throws ParseException {

        //init
        JsonParser.init(
                JsonValidator.getInstance(),
                JsonFormat.getInstance()
        );
        JsonParser jsonParser = JsonParser.getInstance();

        //test

        JsonObject parseObj = (JsonObject) jsonParser.parse(json);

        System.out.println(parseObj);
        JsonArray jsonArray = parseObj.getProperty("education")
                .value()
                .get(JsonArray.class);


    }

    public static void main(String[] args) throws ParseException {
        isJsonValidTest();
//        valuesLocationInArrayTest();
    }
}
