package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.impl.model.MapModel;
import cz.muni.fi.pb162.hw03.template.model.TemplateModel;

import java.util.List;
import java.util.Map;

/**
 * @author Ladislav Husty
 */
public class Demo {
    /**
     *
     * @param arg
     */
    public static void main(String[] arg){
        String printCMD = """
                The name is {{ cat }} and my nemesis is {{ mouse }}.""";

        String simpleIf = """
                Can we do inline with if only? {{ #if yes }} Yes we can!{{ #done }}
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

        String forCMD = """
                The name is {{ name }} and the surname is {{ surname }}.
                                
                Other known names:
                {{ #for name : names }}
                    - {{ name }} {{ surname }}
                {{ #done }}
                                
                Now the name is {{ name }} again.
                """;
        Map<String, Object> model = Map.of("cat", "Tom",
                "mouse", "Jerry",
                "yes", true,
                "no", false,
                "name", "Nibbles",
                "surname", "Disney",
                "names", List.of("Butch", "Toodles", "Quacker"));
        TemplateModel templateModel = new MapModel(model);

        var engine = new TemplateEngineImpl("name", forCMD);
        System.out.println(engine.evaluateTemplate("name", templateModel));
    }
}
