package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.template.model.ModelException;
import cz.muni.fi.pb162.hw03.template.model.TemplateModel;

/**
 * @author Ladislav Husty
 */
public class For implements TemplateEvaluable{
    private final String iterated;
    private final String iterableString;
    private final TemplateEvaluable block;

    /**
     *
     * @param iterated
     * @param iterable
     * @param block
     */
    public For(String iterated, String iterable, TemplateEvaluable block) {
        this.iterated = iterated;
        this.iterableString = iterable;
        this.block = block;
    }

    @Override
    public String eval(TemplateModel model) {
        Iterable<Object> iterable = model.getAsIterable(iterableString);
        String initialModelValue;
        StringBuilder result = new StringBuilder();
        try {
            initialModelValue = model.getAsString(iterated);
        } catch (ModelException me){
            initialModelValue = "";
        }
        for (Object value: iterable) {
            model.put(iterated, value);
            result.append(block.eval(model));
        }
        model.put(iterated, initialModelValue);
        return result.toString();
    }
}
