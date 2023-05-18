package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.impl.model.MapModel;
import cz.muni.fi.pb162.hw03.template.TemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        String simpleName = "simple";
        String printString = """
            The name is {{ cat }} and my nemesis is {{ mouse }}.""";
        String ifString = """
            Can we do inline with if only? {{ #if yes }} Yes we can!{{ #done }}
                        
            Can we do inline with else? {{ #if no }} Yes we can! {{ #else }} Sure we can!{{ #done }}
                        
                        
            BTW: The extra empty line is there on purpose. The next whitespace character following a block command 
            (those with #) is considered part of that command.
                        
            We can also do multiple lines.
            {{ #if yes }}
            Tom is great.
            {{ #else }}
            Jerry is better.
            {{ #done }}
            Now this will be right under.
            """;
        String forString = """
            The name is {{ name }} and the surname is {{ surname }}.
                        
            Other known names:
            {{ #for name : names }}
                - {{ name }} {{ surname }}
            {{ #done }}
                        
            Now the name is {{ name }} again.
            """;
//
//        String nestedIf = """
//            {{ #if no }}
//            First condition is true.
//            {{ #else }}
//            {{ #if no }}
//            Second condition is true.
//            {{ #else }}
//            Second condition is false.
//            {{ #done }}
//            First condition is false.
//            {{ #done }}
//            """;
//        String missingDone = """
//            {{ #if yes }}
//            Condition is true.
//            {{ #else }}
//            Condition is false.
//            No done
//            """;
        Map<String, Object> data;
        MapModel model;
        TemplateEngine templateEngine;
        data = new HashMap<>();
        data.put("cat", "Tom");
        data.put("mouse", "Jerry");
        data.put("yes", true);
        data.put("no", false);
        data.put("name", "Nibbles");
        data.put("surname", "Disney");
        data.put("names", List.of("Butch", "Toodles", "Quacker"));
        model = new MapModel(data);
        templateEngine = new TemplateEngineImpl(simpleName, forString);
        System.out.println(templateEngine.evaluateTemplate(simpleName, model));
    }
}