package org.opentosca.container.core.engine.impl.consolidation;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.engine.impl.ToscaReferenceMapper;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Policies;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class PolicyConsolidation {

    private final Logger LOG = LoggerFactory.getLogger(PolicyConsolidation.class);
    private final ToscaReferenceMapper toscaReferenceMapper;
    
    public PolicyConsolidation(ToscaReferenceMapper referenceMapper) {
        this.toscaReferenceMapper = referenceMapper;
    }


    /**
     * Consolidates the Policies of ServiceTemplates and NodeTemplates inside a CSAR.
     *
     * @param csarID the ID of the CSAR.
     * @return true for success, false if an error occured
     */
    public boolean consolidate(final CSARID csarID) {

        this.LOG.info("Consolidate the Policies of ServiceTemplates and NodeTemplates inside the CSAR \"" + csarID
            + "\".");

        for (final QName serviceTemplateID : this.toscaReferenceMapper.getServiceTemplateIDsContainedInCSAR(csarID)) {

            this.LOG.debug("Processing the Service Template \"" + serviceTemplateID + "\".");

            final TServiceTemplate serviceTemplate =
                (TServiceTemplate) this.toscaReferenceMapper.getJAXBReference(csarID, serviceTemplateID);

            // Policies contained in the Service Template itself
            if (null != serviceTemplate.getBoundaryDefinitions()) {
                this.LOG.debug("Search inside of the Boundary Definitions.");
                final Policies policies = serviceTemplate.getBoundaryDefinitions().getPolicies();
                if (null != policies) {
                    this.createAndStorePolicies(csarID, serviceTemplateID, policies.getPolicy());
                }
            }

            // Policies contained in the Node Templates of the Service Template
            if (null != serviceTemplate.getTopologyTemplate()) {

                this.LOG.debug("Process the Node Templates inside of the Topology Template.");

                for (final TEntityTemplate template : serviceTemplate.getTopologyTemplate()
                                                                     .getNodeTemplateOrRelationshipTemplate()) {

                    // NodeTemplates
                    if (template instanceof TNodeTemplate) {

                        final TNodeTemplate nodeTemplate = (TNodeTemplate) template;
                        if (null != nodeTemplate.getPolicies()) {
                            this.createAndStorePolicies(csarID,
                                                        new QName(serviceTemplateID.getNamespaceURI(),
                                                            nodeTemplate.getId()),
                                                        nodeTemplate.getPolicies().getPolicy());
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Creates the Consolidated Policies and stores it due the ToscaReferenceMapper.
     *
     * @param csarID
     * @param objectFactory
     * @param templateID
     * @param policies
     */
    private void createAndStorePolicies(final CSARID csarID, final QName templateID, final List<TPolicy> policies) {

        final Policies pols = new Policies();
        pols.getPolicy().addAll(policies);

        this.LOG.debug("Store the Consolidated Policies for template ID \"" + templateID + "\".");
        this.toscaReferenceMapper.storeConsolidatedPolicies(csarID, templateID, pols);
    }
}
