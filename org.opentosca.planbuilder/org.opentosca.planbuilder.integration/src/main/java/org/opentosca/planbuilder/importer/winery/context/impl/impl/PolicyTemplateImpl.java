package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import org.opentosca.planbuilder.model.tosca.AbstractPolicyTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyType;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyTemplateImpl extends AbstractPolicyTemplate {

    private static final Logger logger = LoggerFactory.getLogger(PolicyTemplateImpl.class);

    private final org.eclipse.winery.model.tosca.TPolicyTemplate policyTemplate;
    private final DefinitionsImpl defs;

    public PolicyTemplateImpl(final org.eclipse.winery.model.tosca.TPolicyTemplate element, final DefinitionsImpl definitionsImpl) {
        this.policyTemplate = element;
        this.defs = definitionsImpl;
    }

    @Override
    public String getName() {
        return this.policyTemplate.getName();
    }

    @Override
    public String getId() {
        return this.policyTemplate.getId();
    }

    @Override
    public AbstractPolicyType getType() {
        if (this.policyTemplate == null) {
            logger.debug("Internal policyTemplate is null");
        }
        if (this.policyTemplate.getType() == null) {
            logger.debug("Internal policyTemplate type is null");
        }
        for (final AbstractPolicyType policyType : this.defs.getAllPolicyTypes()) {
            if (policyType.getId().equals(this.policyTemplate.getType())) {
                return policyType;
            }
        }
        return null;
    }

    @Override
    public AbstractProperties getProperties() {
        if (this.policyTemplate.getProperties() != null) {
            return new PropertiesImpl(this.policyTemplate.getProperties());
        } else {
            return null;
        }
    }
}
