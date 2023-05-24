package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.template.model.TemplateModel;

/**
 * @author Ladislav Husty
 */
public class Print implements TemplateEvaluable {

    private final String variable;

    /**
     * Creates new print command
     *
     * @param variable name of the variable
     */
    public Print(String variable) {
        this.variable = variable;
    }

    @Override
    public String eval(TemplateModel model) {
        return model.getAsString(variable);
    }
}
