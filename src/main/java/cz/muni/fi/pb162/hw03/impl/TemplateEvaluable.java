package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.template.model.TemplateModel;

/**
 * @author Ladislav Husty
 */
public interface TemplateEvaluable {
    /**
     * evaluates template according to the given model
     * @param model
     * @return evaluated string
     */
    String eval(TemplateModel model);
}
