package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.impl.parser.tokens.Command;
import cz.muni.fi.pb162.hw03.impl.parser.tokens.Commandizer;
import cz.muni.fi.pb162.hw03.impl.parser.tokens.Token;
import cz.muni.fi.pb162.hw03.impl.parser.tokens.Tokenizer;
import cz.muni.fi.pb162.hw03.template.TemplateEngine;
import cz.muni.fi.pb162.hw03.template.TemplateException;
import cz.muni.fi.pb162.hw03.template.model.TemplateModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Ladislav Husty
 */
public class TemplateEngineImpl implements TemplateEngine {
    private final Map<String, String> templates = new HashMap<>();

    /**
     *
     * @param name
     * @param text
     */
    public TemplateEngineImpl(String name, String text) {
        loadTemplate(name, text);
    }

    /**
     *
     */
    public TemplateEngineImpl(){

    }


    /**
     * Loads template
     *
     * @param name name of the template
     * @param text text representation of raw template
     * @throws TemplateException if template is malformed
     */
    @Override
    public void loadTemplate(String name, String text) {
        if (!validateTemplate(text)){
            throw new TemplateException("Template not valid");
        }
        templates.put(name, text);
    }

    /**
     * @return collection of stored templates
     */
    @Override
    public Collection<String> getTemplateNames() {
        return Collections.unmodifiableSet(templates.keySet());
    }

    /**
     * Evaluates template with given model
     *
     * @param name  template name
     * @param model model used for evaluation
     * @return evaluated text
     * @throws TemplateException if there is no loaded template with given name
     */
    @Override
    public String evaluateTemplate(String name, TemplateModel model) throws TemplateException{
        if (templates.get(name) == null){
            throw new TemplateException("No template found with name: " + name);
        }
        Tokenizer tokenizer = new Tokenizer(templates.get(name));
        List<Token> tokens = new LinkedList<>();
        while(!tokenizer.done()) {
            Token token = tokenizer.consume();
            tokens.add(token);
        }
        List<Command> commands = new Commandizer(tokens).getCommands();

        return evaluateCommands(commands, model);
    }

    private String evaluateCommands(List<Command> originalCommands, TemplateModel model){
        List<Command> commands = new ArrayList<>(originalCommands);
        StringBuilder result = new StringBuilder();
        boolean lastIfResult = false;
        while(!(commands.isEmpty())){
            Command firstInLine = commands.get(0);
            if (firstInLine.getKind().equals(Command.Kind.CMDTEXT)){
                result.append(firstInLine.getTokens().get(0).text());
                commands.remove(0);
            } else if ((firstInLine.getKind().equals(Command.Kind.CMDPRINT))) {
                result.append(model.getAsString(firstInLine.getTokens().get(1).name()));
                commands.remove(0);
            } else if ((firstInLine.getKind().equals(Command.Kind.CMDIF))) {
                lastIfResult = model.getAsBoolean(firstInLine.getTokens().get(2).name());
                if (lastIfResult){
                    result.append(commands.get(1).getTokens().get(0).text());
                }
                commands.remove(0);
                commands.remove(0);
            } else if ((firstInLine.getKind().equals(Command.Kind.CMDELSE))) {
                if (!lastIfResult){
                    result.append(commands.get(1).getTokens().get(0).text());
                }
                commands.remove(0);
                commands.remove(0);
            } else if ((firstInLine.getKind().equals(Command.Kind.CMDDONE))) {
                commands.remove(0);
            } else if ((firstInLine.getKind().equals(Command.Kind.CMDFOR))) {
                result = forCommand(commands, model, result);
            }
        }
        return result.toString();
    }
    private StringBuilder forCommand(List<Command> commands, TemplateModel model, StringBuilder result){
        Command firstInLine = commands.get(0);
        Iterable<Object> iterable = model.getAsIterable(firstInLine.getTokens().get(4).name());
        String key = firstInLine.getTokens().get(2).name();
        int commandsInForLoopCounter = 0;
        for (Command cmd : commands) {
            if (cmd.getKind().equals(Command.Kind.CMDDONE)){
                break;
            }
            commandsInForLoopCounter++;
        }
        List<Command> forLoopCommands = commands.subList(1, commandsInForLoopCounter);
        StringBuilder resultCopy = new StringBuilder(result);
        Iterator<Object> iterator = iterable.iterator();
        while(iterator.hasNext()) {
            Object item = iterator.next();
            System.out.println(item);
            resultCopy.append(evaluateCommands(forLoopCommands, model.extended(key, item)));
            if(!(iterator.hasNext())){
                for (int i = 0; i < commandsInForLoopCounter + 1; i++){
                    commands.remove(0);
                }
            }
        }
        return new StringBuilder(resultCopy);
    }
    // There is room for optimization by saving the computed commands for given valid template
    // and use them for evaluating later on.
    private boolean validateTemplate(String templateText){
        Tokenizer tokenizer = new Tokenizer(templateText);
        List<Token> tokens = new LinkedList<>();
        while(!tokenizer.done()) {
            Token token = tokenizer.consume();
            tokens.add(token);
        }
        List<Command> commands = new Commandizer(tokens).getCommands();
        int ifAndForCounter = 0;
        int doneCounter = 0;
        for (Command command : commands){
            if (command.getKind().equals(Command.Kind.CMDDONE)){
                doneCounter++;
            } else if (command.getKind().equals(Command.Kind.CMDIF) ||
                command.getKind().equals(Command.Kind.CMDFOR)) {
                ifAndForCounter++;
            }
        }
        return doneCounter == ifAndForCounter;
    }
}
