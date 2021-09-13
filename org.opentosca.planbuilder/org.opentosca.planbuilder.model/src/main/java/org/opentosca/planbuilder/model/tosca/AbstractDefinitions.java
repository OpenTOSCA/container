package org.opentosca.planbuilder.model.tosca;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;

/**
 * <p>
 * This class represents a TOSCA Definitions Document
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class AbstractDefinitions {

    /**
     * Returns the id of this TOSCA Definitions
     *
     * @return a String containing the id
     */
    public abstract String getId();

    /**
     * Returns the name of this TOSCA Definitions
     *
     * @return a String containing the name, if not set null
     */
    public abstract String getName();

    /**
     * Returns a List of the ServiceTemplates this TOSCA Definitions has declared
     *
     * @return a List of AbstractServiceTemplates
     */
    public abstract Collection<AbstractServiceTemplate> getServiceTemplates();

    /**
     * Returns a List of the NodeTypes this TOSCA Definitions has declared
     *
     * @return a List of TNodeType
     */
    public abstract Collection<TNodeType> getNodeTypes();

    /**
     * Returns a List of the NodeTypeImplementation this TOSCA Definitions has declared
     *
     * @return a List of TNodeTypeImplementation
     */
    public abstract Collection<TNodeTypeImplementation> getNodeTypeImplementations();

    /**
     * Returns a List of the RelationshipTypes this TOSCA Definitions has declared
     *
     * @return a List of TRelationshipType
     */
    public abstract Collection<TRelationshipType> getRelationshipTypes();

    /**
     * Return a List of the ArtifactTemplates this TOSCA Definitions has declared
     *
     * @return a List of AbstractArtifactTemplates
     */
    public abstract Collection<TArtifactTemplate> getArtifactTemplates();

    /**
     * Returns the targetNamespace of this TOSCA Definitions
     *
     * @return a String containing the targetNamespace
     */
    public abstract String getTargetNamespace();

    /**
     * Returns a List of all Definitions this TOSCA Definitions has imported
     *
     * @return a List of AbstractDefinitions
     */
    public abstract Collection<? extends AbstractDefinitions> getImportedDefinitions();

    /**
     * Returns a RelationshipType for the given QName. This method looks trough the whole Definitions space, which means
     * the search looks trough the imported Definitions of this Definitions.
     *
     * @param relationshipTypeId a QName
     * @return an AbstractRelationshipType, if nothing was found null
     */
    public TRelationshipType getRelationshipType(final QName relationshipTypeId) {
        for (final TRelationshipType relationshipType : getRelationshipTypes()) {
            // info: at this moment i have no idea why it doesn't work using the
            // QName.equals() method..
            if (relationshipType.getQName().equals(relationshipTypeId)) {
                return relationshipType;
            }
        }
        return null;
    }

    /**
     * Returns a NodeType for the given QName, This method looks trough the whole Definitions space, which means the
     * search looks trough the imported Definitions of this Definitions
     *
     * @param nodeTypeId a QName
     * @return an TNodeType, if nothing was found null
     */
    public TNodeType getNodeType(final QName nodeTypeId) {
        for (final TNodeType nodeType : getNodeTypes()) {
            if (nodeType.getQName().equals(nodeTypeId)) {
                return nodeType;
            }
        }
        return null;
    }

    /**
     * Returns a ArtifactTemplate for the given QName, This method looks trough the whole Definitions space, which means
     * the search looks trough the imported Definitions of this Definitions
     *
     * @param qname a QName
     * @return an AbstractArtifactTemplate, if nothing was found null
     */
    public TArtifactTemplate getArtifactTemplate(final QName qname) {
        for (final TArtifactTemplate template : getArtifactTemplates()) {
            if (template.getId().equals(qname.getLocalPart())) {
                return template;
            }
        }
        for (final AbstractDefinitions def : getImportedDefinitions()) {
            if (def.getArtifactTemplate(qname) != null) {
                return def.getArtifactTemplate(qname);
            }
        }
        return null;
    }

    public TOperation findOperation(String interfaceName, String operationName) {
        for (TNodeType nodeType : this.getNodeTypes()) {
            for (TInterface iface : nodeType.getInterfaces()) {
                if (iface.getName().equals(interfaceName)) {
                    for (TOperation op : iface.getOperations()) {
                        if (op.getName().equals(operationName)) {
                            return op;
                        }
                    }
                }
            }
        }
        for (AbstractDefinitions defs : this.getImportedDefinitions()) {
            TOperation op = defs.findOperation(interfaceName, operationName);
            if (op != null) {
                return op;
            }
        }
        return null;
    }

    /**
     * Returns a List of all RelationshipTypeImplemenations this TOSCA Definitions has defined
     *
     * @return a List of AbstractRelationshipTypeImplementation
     */
    public abstract Collection<TRelationshipTypeImplementation> getRelationshipTypeImplementations();

    /**
     * Returns all {@link TArtifactType} objects of this {@link AbstractDefinitions} obj.
     *
     * @return a {@link List} of {@link TArtifactType}
     */
    public abstract Collection<TArtifactType> getArtifactTypes();

    /**
     * Returns all {@link TPolicyType} objects of this {@link AbstractDefinitions} object.
     *
     * @return a {@link List} of {@link TPolicyType}
     */
    public abstract Collection<TPolicyType> getPolicyTypes();

    /**
     * Returns all {@link TPolicyTemplate} objects of this {@link AbstractDefinitions} object.
     *
     * @return a {@link List} of {@link TPolicyTemplate}
     */
    public abstract Collection<TPolicyTemplate> getPolicyTemplates();
}
