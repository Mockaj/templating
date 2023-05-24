package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.template.model.TemplateModel;

/**
 * @author Ladislav Husty
 */
public class Text implements TemplateEvaluable{
    private final String variable;

    /**
     * Creates new print command
     *
     * @param variable name of the variable
     */
    public Text(String variable) {
        this.variable = variable;
    }
    @Override
    public String eval(TemplateModel model) {
        return variable;
    }
}
