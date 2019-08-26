package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.omg.CORBA.RepositoryIdHelper;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.planbuilder.AbstractBPMN4TOSCAPlanBuilder;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.BPMN4TOSCATemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Property2VariableMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPMN4TOSCABuilder extends AbstractBPMN4TOSCAPlanBuilder {

	final static Logger LOG = LoggerFactory.getLogger(BPMN4TOSCABuilder.class);

	private BPELPlanHandler planHandler;
	private SimplePlanBuilderServiceInstanceHandler serviceInstanceVarsHandler;
	private NodeRelationInstanceVariablesHandler nodeRelationInstanceHandler;
	private final BPELFinalizer finalizer;

	public BPMN4TOSCABuilder() {
		try {
			this.planHandler = new BPELPlanHandler();
			this.serviceInstanceVarsHandler = new SimplePlanBuilderServiceInstanceHandler();
			this.nodeRelationInstanceHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.finalizer = new BPELFinalizer();
	}

	public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
			final AbstractServiceTemplate serviceTemplate, String planName, List<BPMN4TOSCATemplate> template) {

		String namespace;
		if (serviceTemplate.getTargetNamespace() != null) {
			namespace = serviceTemplate.getTargetNamespace();
		} else {
			namespace = definitions.getTargetNamespace();
		}
		if (namespace.equals(serviceTemplate.getQName().getNamespaceURI())
				&& serviceTemplate.getId().equals(serviceTemplate.getQName().getLocalPart())) {

			final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_BPMN4TOSCAPlan");
			final String processNamespace = serviceTemplate.getTargetNamespace() + "_BPMN4TOSCAPlan";

			// TODO
			final AbstractPlan BPMN4TOSCAPlan = this.generatePlan(new QName(processNamespace, processName).toString(),
					definitions, serviceTemplate);

			LOG.debug("Generated the following abstract prov plan: ");
			LOG.debug(BPMN4TOSCAPlan.toString());

			final BPELPlan newPlan = this.planHandler.createEmptyBPELPlan(processNamespace, processName, BPMN4TOSCAPlan,
					"initiate");

			newPlan.setTOSCAInterfaceName("TEST1");
			newPlan.setTOSCAOperationname(planName);

			this.planHandler.initializeBPELSkeleton(newPlan, csarName);

			this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlans(newPlan, serviceTemplate);
			this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlans(newPlan, serviceTemplate);

			this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
					newPlan);
			this.finalizer.finalize(newPlan);
			return newPlan;
		}
		return null;

	}

	@Override
	public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
		final List<AbstractPlan> plans = new ArrayList<>();
		for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
			for (Entry<String, List<BPMN4TOSCATemplate>> template : serviceTemplate
					.getBPMN4TOSCAPlans(new CSARID(csarName)).entrySet()) {
				final BPELPlan newPlan = buildPlan(csarName, definitions, serviceTemplate, template.getKey(),
						template.getValue());
				if (newPlan != null) {

					// BPELBuildProcessBuilder.LOG .debug("Created BuildPlan " +
					// newBuildPlan.getBpelProcessElement().getAttribute("name"));

					plans.add(newPlan);
				}

			}
		}
		System.out.println("TEST");
		return plans;
	}

}
