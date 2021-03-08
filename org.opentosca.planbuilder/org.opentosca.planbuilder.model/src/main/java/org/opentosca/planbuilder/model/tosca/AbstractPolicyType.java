package org.opentosca.planbuilder.model.tosca;

import javax.xml.namespace.QName;

public abstract class AbstractPolicyType {

    public abstract String getName();

    public abstract QName getId();

    public abstract String getTargetNamespace();

    public abstract String getPolicyLanguage();

    public abstract AbstractProperties getProperties();

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractPolicyType)) {
            return false;
        }

        AbstractPolicyType policyType = (AbstractPolicyType) obj;

        if (!policyType.getId().equals(this.getId())) {
            return false;
        }

        if (!policyType.getName().equals(this.getName())) {
            return false;
        }

        if (!policyType.getTargetNamespace().equals(this.getTargetNamespace())) {
            return false;
        }

        if (!policyType.getPolicyLanguage().equals(this.getPolicyLanguage())) {
            return false;
        }

        return policyType.getProperties().equals(this.getProperties());
    }
}
