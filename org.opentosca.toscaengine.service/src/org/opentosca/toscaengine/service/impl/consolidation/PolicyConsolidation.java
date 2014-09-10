package org.opentosca.toscaengine.service.impl.consolidation;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.consolidatedtosca.ConsolidatedPolicies;
import org.opentosca.model.consolidatedtosca.ConsolidatedPolicy;
import org.opentosca.model.consolidatedtosca.ConsolidatedPolicy.PolicyTemplateSpecificContent;
import org.opentosca.model.consolidatedtosca.ConsolidatedPolicy.Properties;
import org.opentosca.model.consolidatedtosca.ObjectFactory;
import org.opentosca.model.tosca.TBoundaryDefinitions.Policies;
import org.opentosca.model.tosca.TEntityTemplate;
import org.opentosca.model.tosca.TNodeTemplate;
import org.opentosca.model.tosca.TPolicy;
import org.opentosca.model.tosca.TPolicyTemplate;
import org.opentosca.model.tosca.TPolicyType;
import org.opentosca.model.tosca.TServiceTemplate;
import org.opentosca.model.tosca.TTopologyTemplate;
import org.opentosca.toscaengine.service.impl.ToscaEngineServiceImpl;
import org.opentosca.toscaengine.service.impl.toscareferencemapping.ToscaReferenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyConsolidation {

	private final Logger LOG = LoggerFactory
			.getLogger(ExportedInterfacesConsolidation.class);

	private ToscaReferenceMapper toscaReferenceMapper = ToscaEngineServiceImpl.toscaReferenceMapper;

	/**
	 * Consolidates the Policies of ServiceTemplates and NodeTemplates inside a
	 * CSAR.
	 * 
	 * @param csarID
	 *            the ID of the CSAR.
	 * @return true for success, false if an error occured
	 */
	public boolean consolidate(CSARID csarID) {

		LOG.info("Consolidate the Policies of ServiceTemplates and NodeTemplates inside the CSAR \""
				+ csarID + "\".");

		for (QName serviceTemplateID : this.toscaReferenceMapper
				.getServiceTemplateIDsContainedInCSAR(csarID)) {
			
			LOG.debug("Processing the Service Template \"" + serviceTemplateID + "\".");

			TServiceTemplate serviceTemplate = (TServiceTemplate) toscaReferenceMapper
					.getJAXBReference(csarID, serviceTemplateID);

			// Policies contained in the Service Template itself
			if (null != serviceTemplate.getBoundaryDefinitions()) {
				LOG.debug("Search inside of the Boundary Definitions.");
				Policies policies = serviceTemplate.getBoundaryDefinitions()
						.getPolicies();
				if (null != policies) {
					createAndStoreConsolidatedPolicies(csarID,
							serviceTemplateID, policies.getPolicy());
				}
			}

			// Policies contained in the Node Templates of the Service Template
			if (null != serviceTemplate.getTopologyTemplate()) {
				
				LOG.debug("Process the Node Templates inside of the Topology Template.");

				for (TEntityTemplate template : serviceTemplate
						.getTopologyTemplate()
						.getNodeTemplateOrRelationshipTemplate()) {

					// NodeTemplates
					if (template instanceof TNodeTemplate) {

						TNodeTemplate nodeTemplate = (TNodeTemplate) template;
						if (null != nodeTemplate.getPolicies()) {
							createAndStoreConsolidatedPolicies(
									csarID,
									new QName(serviceTemplateID
											.getNamespaceURI(), nodeTemplate
											.getId()), nodeTemplate
											.getPolicies().getPolicy());
						}
					}
				}
			}
		}

		return true;
	}

	/**
	 * Creates the Consolidated Policies and stores it due the
	 * ToscaReferenceMapper.
	 * 
	 * @param csarID
	 * @param objectFactory
	 * @param templateID
	 * @param policies
	 */
	private void createAndStoreConsolidatedPolicies(CSARID csarID,
			QName templateID, List<TPolicy> policies) {
		
		LOG.debug("Create Consolidated Policies for CSAR \"" + csarID + " and template ID \"" + templateID + "\"." );

		ObjectFactory objectFactory = new ObjectFactory();

		// there are Policies
		ConsolidatedPolicies conPols = objectFactory
				.createConsolidatedPolicies();
		for (TPolicy policy : policies) {

			ConsolidatedPolicy conPol = objectFactory
					.createConsolidatedPolicy();
			conPols.getConsolidatedPolicy().add(conPol);

			conPol.setName(policy.getName());
			conPol.setType(policy.getPolicyType());

			LOG.debug("Get PolicyTemplate \"" + policy.getPolicyRef() + "\".");
			TPolicyTemplate policyTemplate = (TPolicyTemplate) toscaReferenceMapper
					.getJAXBReference(csarID, policy.getPolicyRef());
			Properties properties = objectFactory
					.createConsolidatedPolicyProperties();
			properties.setAny(policyTemplate.getProperties().getAny());
			conPol.setProperties(properties);

			PolicyTemplateSpecificContent specificContent = objectFactory
					.createConsolidatedPolicyPolicyTemplateSpecificContent();
			specificContent.getAny().addAll(policyTemplate.getAny());
			conPol.setPolicyTemplateSpecificContent(specificContent);

			LOG.debug("Get PolicyType \"" + policy.getPolicyType() + "\".");
			TPolicyType policyType = (TPolicyType) toscaReferenceMapper
					.getJAXBReference(csarID, policy.getPolicyType());
			conPol.setPolicyLanguage(policyType.getPolicyLanguage());
		}

		LOG.debug("Store the Consolidated Policies for template ID \"" + templateID + "\".");
		toscaReferenceMapper.storeConsolidatedPolicies(csarID, templateID,
				conPols);
	}
}
