package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.template.model.TemplateModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ladislav Husty
 */
public class TemplateModelImpl implements TemplateModel {
    private final Map<String, Object> model;

    public TemplateModelImpl() {
        this.model = new HashMap<>();
    }
    private TemplateModelImpl(Map<String, Object> model) {
        this.model = new HashMap<>(model); // Shallow copy of the map
    }
    /**
     * Add new value to this model
     *
     * @param key   lookup key
     * @param value associated value
     * @return this model
     */
    @Override
    public TemplateModel put(String key, Object value) {
        var val = model.get(key);
        if (val != null){
            throw new IllegalArgumentException("key " + key + " already present in the data model");
        }
        return this;
    }

    /**
     * Creates shallow copy of this model
     *
     * @return copy of this model
     */
    @Override
    public TemplateModel copy() {
        return new TemplateModelImpl(model);
    }

    /**
     * Creates copy of this model with additional entry
     *
     * @param key   new key
     * @param value new value
     * @return extended copy of this model
     */
    @Override
    public TemplateModel extended(String key, Object value) {
        return null;
    }

    /**
     * Lookup value by key in this model
     *
     * @param key lookup key (supports nesting)
     * @return value associated with given key
     * @throws ModelException if key is not present
     */
    @Override
    public Object getAsObject(String key) {
        return null;
    }

    /**
     * Returns String representation of the value
     * associated with given key using {@link Objects#toString(Object)}
     *
     * @param key lookup key (supports nesting)
     * @return value associated with given key as String
     * @throws ModelException if key is not present
     */
    @Override
    public String getAsString(String key) {
        return null;
    }

    /**
     * Returns {@code Truthy} representation of the value
     * associated with given key.
     * <p>
     * Based on data type a Truthy value is
     * <p>
     * - For Boolean: actual ture/false value
     * - For String: true if not empty, false otherwise
     * - For Collection: true if not empty, false otherwise
     * - For Number: true if not zero, false otherwise
     * - For Character: true if not zero value character, false otherwise
     * - For Object: true if not null, false otherwise
     * <p>
     * Note: the rule for Object is applicable only when no other rule matched
     *
     * @param key lookup key (supports nesting)
     * @return value associated with given key as boolean
     * @throws ModelException if key is not present
     */
    @Override
    public boolean getAsBoolean(String key) {
        return false;
    }

    /**
     * Returns instance of {@link Iterable} associated with given key using
     *
     * @param key lookup key (supports nesting)
     * @return value associated with given key as {@code Iterable<Object>}
     * @throws ModelException if key is not present or value is not {@link Iterable}
     */
    @Override
    public Iterable<Object> getAsIterable(String key) {
        return null;
    }
}
