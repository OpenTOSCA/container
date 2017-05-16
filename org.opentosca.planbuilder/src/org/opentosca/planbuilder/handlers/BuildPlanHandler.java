package org.opentosca.planbuilder.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.ode.schemas.dd._2007._03.ObjectFactory;
import org.apache.ode.schemas.dd._2007._03.TDeployment;
import org.apache.ode.schemas.dd._2007._03.TInvoke;
import org.apache.ode.schemas.dd._2007._03.TProcessEvents;
import org.apache.ode.schemas.dd._2007._03.TProvide;
import org.apache.ode.schemas.dd._2007._03.TService;
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.plan.BuildPlan.PlanType;
import org.opentosca.planbuilder.model.plan.Deploy;
import org.opentosca.planbuilder.model.plan.GenericWsdlWrapper;
import org.opentosca.planbuilder.model.plan.TemplateBuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * <p>
 * This class is part of the Facade to work on BuildPlans. This class in
 * particular is responsible for high-level operations on BuildPlans.
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class BuildPlanHandler {
	
	private final static Logger LOG = LoggerFactory.getLogger(BuildPlanHandler.class);
	
	private BPELProcessHandler bpelProcessHandler;
	private BPELTemplateScopeHandler templateHandler;
	private ObjectFactory ddFactory;
	
	
	/**
	 * Constructor
	 * 
	 * @throws ParserConfigurationException is thrown when initializing
	 *             factories failed
	 */
	public BuildPlanHandler() throws ParserConfigurationException {
		this.bpelProcessHandler = new BPELProcessHandler();
		this.templateHandler = new BPELTemplateScopeHandler();
		this.ddFactory = new ObjectFactory();
		
	}
	
	/**
	 * Registers and imports a file on a global level into the given BuildPlan
	 * 
	 * @param file the file with absolute location to add on a global level
	 * @param buildPlan the BuildPlan to add the file to
	 * @return true if adding the file was successful, else false
	 */
	public boolean addImportedFile(File file, BuildPlan buildPlan) {
		return buildPlan.addImportedFile(file);
	}
	
	/**
	 * Creates a Plan with an empty skeleton for the given ServiceTemplate
	 * 
	 * @param serviceTemplate the ServiceTemplate to generate a Plan Skeleton
	 *            for
	 * @return an empty Plan Skeleton
	 */
	public BuildPlan createPlan(AbstractServiceTemplate serviceTemplate, String processName, String processNamespace, int type) {
		BuildPlanHandler.LOG.debug("Creating BuildPlan for ServiceTemplate {}", serviceTemplate.getQName().toString());
		BuildPlan buildPlan = this.createBuildPlan(serviceTemplate.getQName());
		
		switch (type) {
		case 0:
			buildPlan.setType(PlanType.BUILD);
			break;
		case 2:
			buildPlan.setType(PlanType.TERMINATE);
			break;
		// if we don't know what kind of plan this is -> ManagementPlan
		default:
		case 1:
			buildPlan.setType(PlanType.MANAGE);
			break;
		}
		
		// set name of process and wsdl
		this.bpelProcessHandler.setId(processNamespace, processName, buildPlan);
		this.bpelProcessHandler.setWsdlId(processNamespace, processName, buildPlan);
		
		// add import for the process wsdl
		this.bpelProcessHandler.addImports(processNamespace, buildPlan.getWsdl().getFileName(), BuildPlan.ImportType.WSDL, buildPlan);
		
		// add partnerlink to the process. note/FIXME?: the partnerlinktype of
		// the process itself is alread initialized with setting the name of the
		// process wsdl
		//
		// e.g.<bpel:partnerLink name="client"
		// partnerLinkType="tns:bamoodlebuildplan"
		// myRole="bamoodlebuildplanProvider"
		// partnerRole="bamoodlebuildplanRequester" />
		this.bpelProcessHandler.addPartnerLink("client", new QName(processNamespace, processName, "tns"), processName + "Provider", processName + "Requester", true, buildPlan);
		
		// add input and output variables
		//
		// e.g.
		// <!-- Reference to the message passed as input during initiation -->
		// <bpel:variable name="input"
		// messageType="tns:bamoodlebuildplanRequestMessage" />
		//
		// <!-- Reference to the message that will be sent back to the requester
		// during
		// callback -->
		// <bpel:variable name="VmApache_Endpoint" type="ns1:string" />
		// <bpel:variable name="VmMySql_Endpoint" type="ns1:string"/>
		// <bpel:variable name="output"
		// messageType="tns:bamoodlebuildplanResponseMessage" />
		this.bpelProcessHandler.addVariable("input", BuildPlan.VariableType.MESSAGE, new QName(processNamespace, processName + "RequestMessage", "tns"), buildPlan);
		this.bpelProcessHandler.addVariable("output", BuildPlan.VariableType.MESSAGE, new QName(processNamespace, processName + "ResponseMessage", "tns"), buildPlan);
		
		// set the receive and callback invoke elements
		// <bpel:receive name="receiveInput" partnerLink="client"
		// portType="tns:bamoodlebuildplan" operation="initiate"
		// variable="input"
		// createInstance="yes" />
		//
		// <bpel:invoke name="callbackClient"
		// partnerLink="client"
		// portType="tns:bamoodlebuildplanCallback"
		// operation="onResult"
		// inputVariable="output"
		// />
		Element receiveElement = buildPlan.getBpelMainSequenceReceiveElement();
		this.bpelProcessHandler.setAttribute(receiveElement, "name", "receiveInput");
		this.bpelProcessHandler.setAttribute(receiveElement, "operation", "initiate");
		this.bpelProcessHandler.setAttribute(receiveElement, "variable", "input");
		this.bpelProcessHandler.setAttribute(receiveElement, "createInstance", "yes");
		this.bpelProcessHandler.setAttribute(receiveElement, "partnerLink", "client");
		this.bpelProcessHandler.setAttribute(receiveElement, "portType", "tns:" + processName);
		
		Element invokeElement = buildPlan.getBpelMainSequenceCallbackInvokeElement();
		this.bpelProcessHandler.setAttribute(invokeElement, "name", "callbackClient");
		this.bpelProcessHandler.setAttribute(invokeElement, "partnerLink", "client");
		// FIXME serious hack here
		this.bpelProcessHandler.setAttribute(invokeElement, "portType", "tns:" + processName + "Callback");
		this.bpelProcessHandler.setAttribute(invokeElement, "operation", "onResult");
		this.bpelProcessHandler.setAttribute(invokeElement, "inputVariable", "output");
		
		// set deployment deskriptor
		Deploy deployment = buildPlan.getDeploymentDeskriptor();
		List<TDeployment.Process> processes = deployment.getProcess();
		
		// generate process element and set name
		TDeployment.Process process = this.ddFactory.createTDeploymentProcess();
		process.setName(new QName(processNamespace, processName));
		
		TProcessEvents events = this.ddFactory.createTProcessEvents();
		events.setGenerate("all");
		process.setProcessEvents(events);
		
		// get invokes, generate invoke for callback, add to invokes
		List<TInvoke> invokes = process.getInvoke();
		TInvoke callbackInvoke = this.ddFactory.createTInvoke();
		callbackInvoke.setPartnerLink("client");
		// create "callbackservice"
		TService callbackService = this.ddFactory.createTService();
		// example servicename : Wordpress_buildPlanServiceCallback
		callbackService.setName(new QName(processNamespace, processName + "ServiceCallback"));
		callbackService.setPort(processName + "PortCallbackPort");
		callbackInvoke.setService(callbackService);
		invokes.add(callbackInvoke);
		
		// get provides, generate provide element, add to process
		List<TProvide> provides = process.getProvide();
		TProvide provide = this.ddFactory.createTProvide();
		provide.setPartnerLink("client");
		TService provideService = this.ddFactory.createTService();
		provideService.setName(new QName(processNamespace, processName + "Service"));
		provideService.setPort(processName + "Port");
		provide.setService(provideService);
		
		provides.add(provide);
		
		// add process to processes
		processes.add(process);
		
		return buildPlan;
	}
	
	/**
	 * Registers an extension in the given BuildPlan
	 * 
	 * @param namespace the namespace of the extension
	 * @param mustUnderstand sets if the extension must be understood
	 * @param buildPlan the BuildPlan to add to the given BuildPlan
	 * @return true if adding the extension was successful, else false
	 */
	public boolean registerExtension(String namespace, boolean mustUnderstand, BuildPlan buildPlan) {
		return this.bpelProcessHandler.addExtension(namespace, mustUnderstand, buildPlan);
	}
	
	/**
	 * Adds an invoke element to the deployment deskriptor of the given
	 * BuildPlan
	 * 
	 * @param partnerLinkName the name of the partnerLink the invoke will use
	 * @param serviceName the name of the service that will be invoked
	 * @param portName the port of the invoke
	 * @param buildPlan the BuildPlan to add the invoke to
	 * @return true if adding the invoke to the deployment deskriptor was
	 *         successful, else false
	 */
	public boolean addInvokeToDeploy(String partnerLinkName, QName serviceName, String portName, BuildPlan buildPlan) {
		BuildPlanHandler.LOG.debug("Adding invoke with partnerLink {}, service {} and port {} to BuildPlan {}", partnerLinkName, serviceName.toString(), portName, buildPlan.getBpelProcessElement().getAttribute("name"));
		for (TInvoke inv : buildPlan.getDeploymentDeskriptor().getProcess().get(0).getInvoke()) {
			if (inv.getPartnerLink().equals(partnerLinkName)) {
				BuildPlanHandler.LOG.warn("Adding invoke for partnerLink {}, serviceName {} and portName {} failed, there is already a partnerLink with the same Name", partnerLinkName, serviceName.toString(), portName);
				return false;
			}
		}
		// set invoke
		TInvoke invoke = this.ddFactory.createTInvoke();
		invoke.setPartnerLink(partnerLinkName);
		
		// set service
		TService service = this.ddFactory.createTService();
		service.setName(serviceName);
		service.setPort(portName);
		
		invoke.setService(service);
		
		buildPlan.getDeploymentDeskriptor().getProcess().get(0).getInvoke().add(invoke);
		
		BuildPlanHandler.LOG.debug("Adding invoke was successful");
		return true;
	}
	
	/**
	 * Creates a BuildPlan without a skeleton from a ServiceTemplate QName.
	 * 
	 * @param serviceTemplate the QName of the ServiceTemplate
	 * @return a BuildPlan without a Skeleton
	 */
	public BuildPlan createBuildPlan(QName serviceTemplate) {
		BuildPlan newBuildPlan = new BuildPlan();
		newBuildPlan.setServiceTemplate(serviceTemplate);
		
		// init wsdl doc
		try {
			newBuildPlan.setProcessWsdl(new GenericWsdlWrapper());
		} catch (IOException e) {
			BuildPlanHandler.LOG.error("Internal error while initializing WSDL for BuildPlan", e);
		}
		
		this.bpelProcessHandler.initializeXMLElements(newBuildPlan);
		
		// add new deployment deskriptor
		newBuildPlan.setDeploymentDeskriptor(new Deploy());
		
		return newBuildPlan;
	}
	
	/**
	 * Returns all TemplateBuildPlans of the given BuildPlan which handle
	 * RelationshipTemplates
	 * 
	 * @param buildPlan the BuildPlan to get the TemplateBuildPlans from
	 * @return a List of TemplateBuildPlans which handle RelationshipTemplates
	 */
	public List<TemplateBuildPlan> getRelationshipTemplatePlans(BuildPlan buildPlan) {
		List<TemplateBuildPlan> relationshipPlans = new ArrayList<TemplateBuildPlan>();
		for (TemplateBuildPlan template : buildPlan.getTemplateBuildPlans()) {
			if (this.templateHandler.isRelationshipTemplatePlan(template)) {
				relationshipPlans.add(template);
			}
		}
		return relationshipPlans;
	}
	
	/**
	 * Returns a TemplateBuildPlan which handles the Template with the given id
	 * 
	 * @param id the id of template inside a TopologyTemplate
	 * @param buildPlan the BuildPlan to look in
	 * @return a TemplateBuildPlan if it handles a Template with the given id,
	 *         else null
	 */
	public TemplateBuildPlan getTemplateBuildPlanById(String id, BuildPlan buildPlan) {
		for (TemplateBuildPlan template : buildPlan.getTemplateBuildPlans()) {
			// FIXME it looks a bit hacky.. it looks even more hacky if you look
			// at getRelationshipTemplatePlans(..), the ifs
			if ((template.getNodeTemplate() != null) && template.getNodeTemplate().getId().equals(id)) {
				return template;
			}
			if ((template.getRelationshipTemplate() != null) && template.getRelationshipTemplate().getId().equals(id)) {
				return template;
			}
		}
		return null;
	}
	
	/**
	 * Adds an import to the given BuildPlan
	 * 
	 * @param namespace the namespace of the import
	 * @param location the location attribute of the import
	 * @param importType the importType of the import
	 * @param buildPlan the BuildPlan to add the import to
	 * @return true if adding the import was successful, else false
	 */
	public boolean addImportToBpel(String namespace, String location, String importType, BuildPlan buildPlan) {
		BuildPlanHandler.LOG.debug("Adding import with namespace {}, location {} and importType to BuildPlan {}", namespace, location, importType, buildPlan.getBpelProcessElement().getAttribute("name"));
		if (importType.equals(BuildPlan.ImportType.WSDL.toString())) {
			return this.bpelProcessHandler.addImports(namespace, location, BuildPlan.ImportType.WSDL, buildPlan);
		} else if (importType.equals(BuildPlan.ImportType.XSD.toString())) {
			return this.bpelProcessHandler.addImports(namespace, location, BuildPlan.ImportType.XSD, buildPlan);
		} else {
			return false;
		}
	}
	
	/**
	 * Returns if the given import is already imported in the given BuildPlan
	 * 
	 * @param namespace the namespace of the import
	 * @param location the location of the import
	 * @param importType the importType of the import
	 * @param buildPlan the BuildPlan to look inside for the import
	 * @return true if the import is already present in the given BuildPlan,
	 *         else false
	 */
	public boolean hasImport(String namespace, String location, String importType, BuildPlan buildPlan) {
		if (importType.equals(BuildPlan.ImportType.WSDL.toString())) {
			return this.bpelProcessHandler.hasImport(namespace, location, BuildPlan.ImportType.WSDL, buildPlan);
		} else if (importType.equals(BuildPlan.ImportType.XSD.toString())) {
			return this.bpelProcessHandler.hasImport(namespace, location, BuildPlan.ImportType.XSD, buildPlan);
		} else {
			return false;
		}
	}
	
	/**
	 * Adds a link with specified name to the given BuildPlan
	 * 
	 * @param name the name of the link
	 * @param buildPlan the BuildPlan to add the link to
	 * @return true if adding the link was successful, else false
	 */
	public boolean addLink(String name, BuildPlan buildPlan) {
		// did this to keep the planbuilder clean from the xml/dom handlers
		return this.bpelProcessHandler.addLink(name, buildPlan);
	}
	
	/**
	 * Adds a propertyVariable to the buildPlan
	 * 
	 * @param name the name of the propertyVariable
	 * @param plan the BuildPlan to add the propertyVariable to
	 * @return true if adding the PropertyVariable to the BuildPlan, else false
	 */
	public boolean addPropertyVariable(String name, BuildPlan plan) {
		return this.bpelProcessHandler.addVariable("prop_" + name, BuildPlan.VariableType.TYPE, new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"), plan);
	}
	
	/**
	 * Adds an integer variable to the given plan on the global level
	 * 
	 * @param name a name for the variable (no duplicate check)
	 * @param plan the plan to add the variable to
	 * @return true iff adding the variable was successful
	 */
	public boolean addIntegerVariable(String name, BuildPlan plan) {
		return this.bpelProcessHandler.addVariable(name, BuildPlan.VariableType.TYPE, new QName("http://www.w3.org/2001/XMLSchema", "integer", "xsd"), plan);
	}
	
	/**
	 * Adds a copy to the main assign of the given BuildPlan to initialize the
	 * variable
	 * 
	 * @param propertyName the name of the propertyVariable
	 * @param value the value to initialize the variable with
	 * @param buildPlan the BuildPlan to add the copy to
	 * @return true if adding the copy was successful, else false
	 */
	public boolean initializePropertyVariable(String propertyName, String value, BuildPlan buildPlan) {
		return this.bpelProcessHandler.assignVariableStringValue("prop_" + propertyName, value, buildPlan);
	}
	
	/**
	 * Adds a Element of type string to the RequestMessage of the given
	 * BuildPlan
	 * 
	 * @param elementName the localName of the element
	 * @param buildPlan the BuildPlan to add the element to
	 * @return true if adding the element to RequestMessage was successful, else
	 *         false
	 */
	public boolean addStringElementToPlanRequest(String elementName, BuildPlan buildPlan) {
		return buildPlan.getWsdl().addElementToRequestMessage(elementName, new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
	}
	
	/**
	 * Adds a element of type string to the ResponseMessage of the given
	 * BuildPlan
	 * 
	 * @param elementName the localName of the element
	 * @param buildPlan the BuildPlan to add the element to
	 * @return true if adding the element to the ResponseMessage was successful,
	 *         else false
	 */
	public boolean addStringElementToPlanResponse(String elementName, BuildPlan buildPlan) {
		return buildPlan.getWsdl().addElementToResponseMessage(elementName, new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
	}
	
	/**
	 * Adds a provide element to the deployment deskriptor of the given
	 * BuildPlan
	 * 
	 * @param partnerLinkName the name of the partnerlink the provide uses
	 * @param serviceName the service name the provide uses
	 * @param portName the port name the provide uses
	 * @param buildPlan the BuildPlan to add the provide to
	 * @return true if adding the provide to the deployment deskriptor was
	 *         successful, else false
	 */
	public boolean addProvideToDeploy(String partnerLinkName, QName serviceName, String portName, BuildPlan buildPlan) {
		BuildPlanHandler.LOG.debug("Trying to add provide with partnerLink {}, service {} and port {} to BuildPlan {}", partnerLinkName, serviceName.toString(), portName, buildPlan.getBpelProcessElement().getAttribute("name"));
		for (TProvide inv : buildPlan.getDeploymentDeskriptor().getProcess().get(0).getProvide()) {
			if (inv.getPartnerLink().equals(partnerLinkName)) {
				BuildPlanHandler.LOG.warn("Adding provide failed");
				return false;
			}
		}
		// set invoke
		TProvide provide = this.ddFactory.createTProvide();
		provide.setPartnerLink(partnerLinkName);
		
		// set service
		TService service = this.ddFactory.createTService();
		service.setName(serviceName);
		service.setPort(portName);
		
		provide.setService(service);
		
		buildPlan.getDeploymentDeskriptor().getProcess().get(0).getProvide().add(provide);
		BuildPlanHandler.LOG.debug("Adding provide was successful");
		return true;
		
	}
	
	/**
	 * Returns a List of Strings which represent all Links declared in the given
	 * BuildPlan
	 * 
	 * @param buildPlan the BuildPlan whose declared Links should be returned
	 * @return a List of Strings containing all Links of the given BuildPlan
	 */
	public List<String> getAllLinks(BuildPlan buildPlan) {
		Element flowLinks = buildPlan.getBpelMainFlowLinksElement();
		List<String> linkNames = new ArrayList<String>();
		NodeList children = flowLinks.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals("link")) {
				linkNames.add(children.item(i).getAttributes().getNamedItem("name").getNodeValue());
			}
			if (children.item(i).getLocalName().equals("link")) {
				linkNames.add(children.item(i).getAttributes().getNamedItem("name").getNodeValue());
			}
		}
		return linkNames;
	}
	
	/**
	 * Removes a link of the given BuildPlan with the given name
	 * 
	 * @param link the name of the link to remove
	 * @param buildPlan the BuildPlan where the link should be removed
	 */
	public void removeLink(String link, BuildPlan buildPlan) {
		this.bpelProcessHandler.removeLink(link, buildPlan);
		
	}
}
