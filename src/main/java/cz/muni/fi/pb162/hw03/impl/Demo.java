package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.impl.model.MapModel;
import cz.muni.fi.pb162.hw03.template.model.TemplateModel;

import java.util.Map;

/**
 * @author Ladislav Husty
 */
public class Demo {
    public static void main(String[] arg){
        String printCMD = """
                The name is {{ cat }} and my nemesis is {{ mouse }}.""";

        String ifCMD = """
                Can we do inline with if only? {{ #if yes }} Yes we can!{{ #done }}
                                
                Can we do inline with else? {{ #if no }} Yes we can! {{ #else }} Sure we can!{{ #done }}
                                
                                
                BTW: The extra empty line is there on purpose. The next whitespace character following a block command (those with #) is considered part of that command.
                                
                We can also do multiple lines.
                {{ #if yes }}
                Tom is great.
                {{ #else }}
                Jerry is better.
                {{ #done }}
                Now this will be right under.
                """;

        String nested = """
            {{ #if no }}
            First condition is true.
            {{ #else }}
            {{ #if no }}
            Second condition is true.
            {{ #else }}
            Second condition is false.
            {{ #done }}
            First condition is false.
            {{ #done }}
            """;
        Map<String, Object> model = Map.of("cat", "Tom",
                "mouse", "Jerry",
                "yes", true,
                "no", false);
        TemplateModel templateModel = new MapModel(model);

        var engine = new TemplateEngineImpl("name", ifCMD);
        System.out.println(engine.evaluateTemplate("name", templateModel));
    }
}
