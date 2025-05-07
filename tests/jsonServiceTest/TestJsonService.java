package tests.jsonServiceTest;

import org.services.jsonService.json.navigator.JsonNavigator;
import org.services.jsonService.json.formatter.JsonFormat;
import org.services.jsonService.json.mapper.ObjectMapper;
import org.services.jsonService.json.parser.JsonParser;
import org.services.jsonService.json.validator.JsonValidator;
import org.services.jsonService.json.types.JsonObject;

import java.util.Scanner;

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

    public static void isJsonValidTest() {

        //init
        JsonParser.init(
                JsonValidator.getInstance(),
                JsonFormat.getInstance(),
                ObjectMapper.getInstance()
        );
        JsonParser jsonParser = JsonParser.getInstance();

        //test

        JsonObject parseObj = (JsonObject) jsonParser.parse(json);

        Scanner scanner = new Scanner(System.in);;
        String input;

        while (true){
            System.out.println("Write a path: ");
            input = scanner.nextLine();
            if(input.equals("stop")){
                scanner.close();
                break;
            }
            System.out.println(JsonNavigator.navigate(parseObj, input));
        }
    }

    public static void main(String[] args) {
        isJsonValidTest();
//        valuesLocationInArrayTest();
    }
}
