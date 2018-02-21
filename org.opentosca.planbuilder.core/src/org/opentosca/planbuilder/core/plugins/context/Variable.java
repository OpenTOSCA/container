package org.opentosca.planbuilder.core.plugins.context;

/**
 * <p>
 * This is a Wrapper class for Template Id to Property variable name
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class Variable {

    private final String templateId;
    private final String variableName;

    /**
     * Contructor
     *
     * @param templateId a TemplateId
     * @param variableName a property variable name
     */
    public Variable(final String templateId, final String variableName) {
        this.templateId = templateId;
        this.variableName = variableName;
    }

    /**
     * Returns the property variable name of this wrapper
     *
     * @return a String containing the property variable name
     */
    public String getName() {
        return this.variableName;
    }

    /**
     * Returns the template id of this wrapper
     *
     * @return a String containing the TemplateId
     */
    public String getTemplateId() {
        return this.templateId;
    }
}
