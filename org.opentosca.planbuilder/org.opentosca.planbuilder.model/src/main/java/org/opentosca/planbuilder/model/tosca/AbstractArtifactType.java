package org.opentosca.planbuilder.model.tosca;

import javax.xml.namespace.QName;

public abstract class AbstractArtifactType {

    public abstract QName getId();

    public abstract QName getRef();

    public abstract AbstractArtifactType getTypeRef();
}
