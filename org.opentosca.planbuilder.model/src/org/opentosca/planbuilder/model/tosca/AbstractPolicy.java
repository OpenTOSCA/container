package org.opentosca.planbuilder.model.tosca;

public abstract class AbstractPolicy {

    public abstract String getName();

    public abstract AbstractPolicyType getType();

    public abstract AbstractPolicyTemplate getTemplate();

    public abstract AbstractProperties getProperties();
}
