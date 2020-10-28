package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractArtifactType;

public class ArtifactTypeImpl extends AbstractArtifactType {

    private final DefinitionsImpl defs;
    private final org.eclipse.winery.model.tosca.TArtifactType artifactTypeDef;
    private final QName qname;
    private final QName ref;

    protected ArtifactTypeImpl(final org.eclipse.winery.model.tosca.TArtifactType element, final DefinitionsImpl defs) {
        this.defs = defs;
        this.artifactTypeDef = element;
        this.qname = this.initQName();
        if (this.artifactTypeDef.getDerivedFrom() != null) {
            this.ref = this.artifactTypeDef.getDerivedFrom().getTypeRef();
        } else {
            this.ref = null;
        }
    }

    private QName initQName() {
        String namespace = this.artifactTypeDef.getTargetNamespace();

        if (namespace == null || namespace.isEmpty()) {
            namespace = this.defs.getTargetNamespace();
        }
        return new QName(namespace, this.artifactTypeDef.getName());
    }

    @Override
    public QName getId() {
        return this.qname;
    }

    @Override
    public QName getRef() {
        return this.ref;
    }

    @Override
    public AbstractArtifactType getTypeRef() {
        if (this.ref == null) {
            return null;
        }

        for (final AbstractArtifactType artType : this.defs.getAllArtifactTypes()) {
            if (artType.getId().equals(this.getRef())) {
                return artType;
            }
        }
        return null;
    }
}
