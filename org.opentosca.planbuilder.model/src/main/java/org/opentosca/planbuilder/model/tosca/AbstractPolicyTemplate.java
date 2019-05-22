package org.opentosca.planbuilder.model.tosca;

public abstract class AbstractPolicyTemplate {

    public abstract String getName();

    public abstract String getId();

    public abstract AbstractPolicyType getType();

    public abstract AbstractProperties getProperties();

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractPolicyTemplate)) {
            return false;
        }

        AbstractPolicyTemplate template = (AbstractPolicyTemplate) obj;

        if (!template.getId().equals(this.getId())) {
            return false;
        }

        if (!template.getName().equals(this.getName())) {
            return false;
        }

        if (!template.getType().equals(this.getType())) {
            return false;
        }

        if (!template.getProperties().equals(this.getProperties())) {
            return false;
        }

        return true;
    }
}
