package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

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
//import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.planbuilder.AbstractBPMN4TOSCAPlanBuilder;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.bpmn4toscaInputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.ServiceTemplateBoundaryPropertyMappingsToOutputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
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
	private final PropertyVariableHandler propertyInitializer;
	private final ServiceTemplateBoundaryPropertyMappingsToOutputHandler propertyOutputInitializer;
	private SimplePlanBuilderServiceInstanceHandler serviceInstanceInitializer;
	private final bpmn4toscaInputHandler propertyInit = new bpmn4toscaInputHandler();
	private final BPELFinalizer finalizer;

	public BPMN4TOSCABuilder() {
		try {
			this.planHandler = new BPELPlanHandler();
			this.serviceInstanceVarsHandler = new SimplePlanBuilderServiceInstanceHandler();
			this.serviceInstanceInitializer = new SimplePlanBuilderServiceInstanceHandler();
			this.nodeRelationInstanceHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
		this.propertyOutputInitializer = new ServiceTemplateBoundaryPropertyMappingsToOutputHandler();
		this.finalizer = new BPELFinalizer();
	}

	public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
			final AbstractServiceTemplate serviceTemplate, final String name,
			final List<BPMN4TOSCATemplate> bpmnWorkflow) {
		setInstanceData(bpmnWorkflow);
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

			final AbstractPlan BPMN4TOSCAPlan = this.generatePlan(new QName(processNamespace, processName).toString(),
					definitions, serviceTemplate, bpmnWorkflow);

			LOG.debug("Generated the following abstract prov plan: ");
			LOG.debug(BPMN4TOSCAPlan.toString());

			final BPELPlan newPlan = this.planHandler.createEmptyBPELPlan(processNamespace, processName, BPMN4TOSCAPlan,
					"initiate");

			newPlan.setTOSCAInterfaceName("BPMN4TOSCA Plans");
			newPlan.setTOSCAOperationname("start");

			this.planHandler.initializeBPELSkeleton(newPlan, csarName);

			this.nodeRelationInstanceHandler.addInstanceURLVarToTemplatePlans(newPlan, serviceTemplate);
			this.nodeRelationInstanceHandler.addInstanceIDVarToTemplatePlans(newPlan, serviceTemplate);
			
			//TODO: inputs aus BPMN
			final Property2VariableMapping propMap = this.propertyInitializer.initializePropertiesAsVariables(newPlan,
					serviceTemplate);

			this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
					newPlan);
			if (newInstance) {
				//TODO: outputs aus BPMN
				this.propertyOutputInitializer.initializeBuildPlanOutput(definitions, newPlan, propMap,
						serviceTemplate);
				this.serviceInstanceInitializer
						.appendCreateServiceInstanceVarsAndAnitializeWithInstanceDataAPI(newPlan);
				String serviceInstanceUrl = this.serviceInstanceInitializer.findServiceInstanceUrlVariableName(newPlan);
				String serviceInstanceID = this.serviceInstanceInitializer.findServiceInstanceIdVarName(newPlan);
				String serviceTemplateUrl = this.serviceInstanceInitializer.findServiceTemplateUrlVariableName(newPlan);
				this.propertyInit.initializePropertiesFromWorkflow(newPlan, propMap, serviceInstanceUrl,
						serviceInstanceID, serviceTemplateUrl, serviceTemplate, csarName, bpmnWorkflow);
			} else {
				this.serviceInstanceVarsHandler.addServiceInstanceHandlingFromInput(newPlan);
			}
			runPlugins(newPlan);
			this.finalizer.finalize(newPlan);
			return newPlan;
		}
		BPELBuildProcessBuilder.LOG.warn(
				"Couldn't create BPMN4TOSCAPlan for ServiceTemplate {} in Definitions {} of CSAR {}",
				serviceTemplate.getQName().toString(), definitions.getId(), csarName);
		return null;

	}

	@Override
	public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
		final List<AbstractPlan> plans = new ArrayList<>();
		for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
			for (Entry<String, List<BPMN4TOSCATemplate>> template : serviceTemplate.getBPMN4TOSCAPlans().entrySet()) {
				final BPELPlan newPlan = buildPlan(csarName, definitions, serviceTemplate, template.getKey(),
						template.getValue());
				if (newPlan != null) {
					BPELBuildProcessBuilder.LOG
							.debug("Created BuildPlan " + newPlan.getBpelProcessElement().getAttribute("name"));
					plans.add(newPlan);
				}

			}
		}
		return plans;
	}

	@Override
	public AbstractPlan buildPlan(String csarName, AbstractDefinitions definitions,
			AbstractServiceTemplate serviceTemplateId) {
		// TODO Auto-generated method stub
		return null;
	}

	private void runPlugins(final BPELPlan plan) {
		for (final BPELScope bpelScope : plan.getTemplateBuildPlans()) {
			
		}

	}

	private void setInstanceData(final List<BPMN4TOSCATemplate> bpmnWorkflow) {
		BPMN4TOSCATemplate StartEvent = bpmnWorkflow.stream().filter(node -> node.getType().equals("StartEvent"))
				.findAny().orElse(null);

		if (StartEvent.getInstanceType().equals("newInstance")) {
			newInstance = true;
		} else if (StartEvent.getInstanceType().equals("selectInstance")) {
			newInstance = false;
		}
	}
}
