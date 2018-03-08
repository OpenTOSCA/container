package org.opentosca.planbuilder.model.tosca;

public abstract class AbstractPolicyTemplate {

    public abstract String getName();

    public abstract String getId();

    public abstract AbstractPolicyType getType();

    public abstract AbstractProperties getProperties();
}
