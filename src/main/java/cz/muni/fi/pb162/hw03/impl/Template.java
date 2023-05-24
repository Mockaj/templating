package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.template.model.TemplateModel;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * @author Ladislav Husty
 */
public class Template implements TemplateEvaluable {

    private final List<TemplateEvaluable> commands;

    /**
     * Creates new template
     *
     * @param commands list of commands forming this template
     */
    public Template(List<TemplateEvaluable> commands) {
        this.commands = List.copyOf(commands);
    }

    @Override
    public String eval(TemplateModel model) {
        return commands.stream()
                .map( e -> e.eval(model))
                .collect(joining());
    }
}