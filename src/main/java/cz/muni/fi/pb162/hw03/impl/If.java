package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.template.model.TemplateModel;

/**
 * @author Ladislav Husty
 */
public class If implements TemplateEvaluable {

    private final String variable;
    private final TemplateEvaluable ifBlock;
    private final TemplateEvaluable elseBlock;

    /**
     * Creates new conditional command
     *
     * @param variable variable used as the conditional
     * @param ifBlock template evaluated when the {code variable} evaluated to true
     * @param elseBlock template evaluated when the {code variable} evaluated to false
     */
    public If(String variable, TemplateEvaluable ifBlock, TemplateEvaluable elseBlock) {
        this.variable = variable;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public String eval(TemplateModel model) {
        var value = model.getAsBoolean(variable);
        var block = value ? ifBlock : elseBlock;

        return (block != null) ? block.eval(model) : "";
    }
}