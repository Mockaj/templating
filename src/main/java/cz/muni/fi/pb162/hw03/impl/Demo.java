package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.impl.model.MapModel;
import cz.muni.fi.pb162.hw03.template.FSTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
        data = new HashMap<>();
        data.put("cat", "Tom");
        data.put("mouse", "Jerry");
        data.put("yes", true);
        data.put("no", false);
        data.put("name", "Nibbles");
        data.put("surname", "Disney");
        data.put("names", List.of("Butch", "Toodles", "Quacker"));
        model = new MapModel(data);
        Path examples = Path.of("examples");
        Path file = examples.resolve("complex.txt.tpl");
        Path absolutePathFile = file.toAbsolutePath();
        String fileName = file.getFileName().toString();
        String templateName = fileName.substring(0, fileName.lastIndexOf("." + "tpl"));
        Path path1 = Path.of("/Users/mockaj/examples/complex.txt.tpl");
        Path path2 = Path.of("/Users/mockaj/examples/for.txt.tpl");
        Path path3 = Path.of("/Users/mockaj/examples/print.txt.tpl");
        Path pathExamples = Path.of("/Users/mockaj/examples");

        FSTemplateEngine fsTemplateEngine = new FSTemplateEngineImpl();
//        fsTemplateEngine.loadTemplate(path1, StandardCharsets.UTF_8, "tpl");
//        fsTemplateEngine.loadTemplate(path2, StandardCharsets.UTF_8, "tpl");
//        fsTemplateEngine.loadTemplate(path3, StandardCharsets.UTF_8, "tpl");
        fsTemplateEngine.loadTemplateDir(pathExamples, StandardCharsets.UTF_8, "tpl");

//        templateEngine1.loadTemplate(file, StandardCharsets.UTF_8, "tpl");
//        System.out.println(templateEngine3.getTemplateNames());

//        System.out.println(templateEngine.evaluateTemplate(simpleName, model));
    }
}