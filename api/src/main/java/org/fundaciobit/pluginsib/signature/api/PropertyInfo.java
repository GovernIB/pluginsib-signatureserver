package org.fundaciobit.pluginsib.signature.api;

/**
 * 
 * @author anadal(u80067)
 *
 */
public class PropertyInfo {

    protected String key;

    protected String description;

    protected boolean optional;

    protected String defaultValue;

    /**
     * Per comentaris usar #
     */
    protected String[] listOfAvailableValues;

    /**
     * Per comentaris usar #
     */
    protected String[] examples;
    
    protected String pattern;

    public PropertyInfo() {
        super();
    }

    public PropertyInfo(String key, String description, boolean optional, String[] listOfAvailableValues,
            String[] examples) {
        super();
        this.key = key;
        this.description = description;
        this.optional = optional;
        this.listOfAvailableValues = listOfAvailableValues;
        this.examples = examples;
    }

    public PropertyInfo(String key, String description, boolean optional, String defaultValue,
            String[] listOfAvailableValues, String[] examples) {
        super();
        this.key = key;
        this.description = description;
        this.optional = optional;
        this.defaultValue = defaultValue;
        this.listOfAvailableValues = listOfAvailableValues;
        this.examples = examples;
    }
    
    public PropertyInfo(String key, String description, boolean optional, String defaultValue,
            String pattern, String[] examples) {
        super();
        this.key = key;
        this.description = description;
        this.optional = optional;
        this.defaultValue = defaultValue;
        this.listOfAvailableValues = null;
        this.pattern = pattern;
        this.examples = examples;
    }
    
    public PropertyInfo(String key, String description, boolean optional, String defaultValue) {
        super();
        this.key = key;
        this.description = description;
        this.optional = optional;
        this.defaultValue = defaultValue;
        this.listOfAvailableValues = null;
        this.pattern = null;
        this.examples = null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String[] getListOfAvailableValues() {
        return listOfAvailableValues;
    }

    public void setListOfAvailableValues(String[] listOfAvailableValues) {
        this.listOfAvailableValues = listOfAvailableValues;
    }

    public String[] getExamples() {
        return examples;
    }

    public void setExamples(String[] examples) {
        this.examples = examples;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

}
