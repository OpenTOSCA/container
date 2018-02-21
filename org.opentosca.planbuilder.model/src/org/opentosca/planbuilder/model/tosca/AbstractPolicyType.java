package org.opentosca.planbuilder.model.tosca;

import javax.xml.namespace.QName;

public abstract class AbstractPolicyType {

    public abstract String getName();

    public abstract QName getId();

    public abstract String getTargetNamespace();

    public abstract String getPolicyLanguage();

    public abstract AbstractProperties getProperties();
}
