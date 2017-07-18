package org.opentosca.planbuilder.plugins.context;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.TemplatePlanBuilder;
import org.opentosca.planbuilder.TemplatePlanBuilder.ProvisioningChain;
import org.opentosca.planbuilder.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.handlers.BPELScopeHandler;
import org.opentosca.planbuilder.handlers.PlanHandler;
import org.opentosca.planbuilder.handlers.ScopeHandler;
import org.opentosca.planbuilder.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.model.plan.TOSCAPlan;
import org.opentosca.planbuilder.model.plan.GenericWsdlWrapper;
import org.opentosca.planbuilder.model.plan.TemplateBuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * This class is used for all Plugins. All acitions on TemplateBuildPlans and
 * BuildPlans should be done with the operations of this class. It is basically
 * a Facade to Template and its TemplateBuildPlan
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class TemplatePlanContext {

	private final static Logger LOG = LoggerFactory.getLogger(TemplatePlanContext.class);

	private TemplateBuildPlan templateBuildPlan;
	private QName serviceTemplateId;

	private PlanHandler buildPlanHandler;
	private BPELPlanHandler bpelProcessHandler;
	private ScopeHandler templateHandler;
	private BPELScopeHandler bpelTemplateHandler;
	private Map<String, String> namespaceMap;
	private PropertyMap propertyMap;

	private String planNamespace = "ba.example";

	/**
	 * Constructor
	 *
	 * @param templateBuildPlan
	 *            the TemplateBuildPlan of a Template
	 * @param serviceTemplateName
	 *            the name of the ServiceTemplate where the Template of the
	 *            context originates
	 * @param map
	 *            a PropertyMap containing mappings for all Template properties
	 *            of the TopologyTemplate the ServiceTemplate has
	 */
	public TemplatePlanContext(TemplateBuildPlan templateBuildPlan, PropertyMap map, QName serviceTemplateId) {
		this.templateBuildPlan = templateBuildPlan;
		this.serviceTemplateId = serviceTemplateId;

		try {
			this.buildPlanHandler = new PlanHandler();
			this.bpelProcessHandler = new BPELPlanHandler();
		} catch (ParserConfigurationException e) {
			TemplatePlanContext.LOG.warn("Coulnd't initialize internal handlers", e);
		}
		this.templateHandler = new ScopeHandler();
		this.bpelTemplateHandler = new BPELScopeHandler();
		this.namespaceMap = new HashMap<String, String>();
		this.propertyMap = map;
	}

	public TemplatePlanContext createContext(AbstractNodeTemplate nodeTemplate) {
		for (TemplateBuildPlan plan : this.templateBuildPlan.getBuildPlan().getTemplateBuildPlans()) {
			if (plan.getNodeTemplate() != null && plan.getNodeTemplate().equals(nodeTemplate)) {
				return new TemplatePlanContext(plan, this.propertyMap, this.serviceTemplateId);
			}
		}
		return null;
	}

	/**
	 * Returns the plan type of this context
	 * 
	 * @return a TOSCAPlan.PlanType
	 */
	public TOSCAPlan.PlanType getPlanType() {
		return this.templateBuildPlan.getBuildPlan().getType();
	}

	/**
	 * Returns the name of the TemplateBuildPlan this TemplatePlanContext
	 * belongs to
	 * 
	 * @return a String containing a Name for the TemplateBuildPlan consisting
	 *         of the Id of the NodeTemplate processed in that plan
	 */
	public String getTemplateBuildPlanName() {
		return this.templateBuildPlan.getBpelScopeElement().getAttribute("name");
	}

	/**
	 * Returns the file name of the CSAR in which this Template resides
	 *
	 * @return a String with the file name of the CSAR
	 */
	public String getCSARFileName() {
		return this.templateBuildPlan.getBuildPlan().getCsarName();
	}

	/**
	 * Returns whether this context is for a nodeTemplate
	 *
	 * @return true if this context is for a nodeTemplate, else false
	 */
	public boolean isNodeTemplate() {
		return this.templateBuildPlan.getNodeTemplate() != null ? true : false;
	}

	/**
	 * Returns the name of variable which is the input message of the buildPlan
	 *
	 * @return a String containing the variable name of the inputmessage of the
	 *         BuildPlan
	 */
	public String getPlanRequestMessageName() {
		return "input";
	}

	/**
	 * Returns the name of variable which is the output message of the buildPlan
	 *
	 * @return a String containing the variable name of the outputmessage of the
	 *         BuildPlan
	 */
	public String getPlanResponseMessageName() {
		return "output";
	}

	/**
	 * Returns the NodeTemplate of this TemplatePlanContext
	 *
	 * @return an AbstractNodeTemplate if this TemplatePlanContext handles a
	 *         NodeTemplate, else null
	 */
	public AbstractNodeTemplate getNodeTemplate() {
		return this.templateBuildPlan.getNodeTemplate();
	}

	/**
	 * <p>
	 * Returns all NodeTemplates that are part of the ServiceTemplate this
	 * context belongs to.
	 * </p>
	 *
	 * @return a List of AbstractNodeTemplate
	 */
	public List<AbstractNodeTemplate> getNodeTemplates() {
		// find the serviceTemplate
		for (AbstractServiceTemplate serviceTemplate : this.templateBuildPlan.getBuildPlan().getDefinitions()
				.getServiceTemplates()) {
			if (serviceTemplate.getQName().equals(this.serviceTemplateId)) {
				return serviceTemplate.getTopologyTemplate().getNodeTemplates();
			}
		}
		return null;
	}

	/**
	 * <p>
	 * Returns all RelationshipTemplates that are part of the ServiceTemplate
	 * this context belongs to.
	 * </p>
	 * 
	 * @return a List of AbstractRelationshipTemplate
	 */
	public List<AbstractRelationshipTemplate> getRelationshipTemplates() {
		for (AbstractServiceTemplate serviceTemplate : this.templateBuildPlan.getBuildPlan().getDefinitions()
				.getServiceTemplates()) {
			if (serviceTemplate.getQName().equals(this.serviceTemplateId)) {
				return serviceTemplate.getTopologyTemplate().getRelationshipTemplates();
			}
		}
		return null;
	}

	/**
	 * Returns whether this context is for a relationshipTemplate
	 *
	 * @return true if this context is for a relationshipTemplate, else false
	 */
	public boolean isRelationshipTemplate() {
		return this.templateBuildPlan.getRelationshipTemplate() != null ? true : false;
	}

	/**
	 * Returns the RelationshipTemplate this context handles
	 *
	 * @return an AbstractRelationshipTemplate if this context handle a
	 *         RelationshipTemplate, else null
	 */
	public AbstractRelationshipTemplate getRelationshipTemplate() {
		return this.templateBuildPlan.getRelationshipTemplate();
	}

	/**
	 * Returns an absolute File for the given AbstractArtifactReference
	 *
	 * @param ref
	 *            an AbstractArtifactReference
	 * @return a File with an absolute path to the file
	 */
	public File getFileFromArtifactReference(AbstractArtifactReference ref) {
		return this.templateBuildPlan.getBuildPlan().getDefinitions().getAbsolutePathOfArtifactReference(ref);
	}

	/**
	 * Adds a variable to the TemplateBuildPlan of the template this context
	 * belongs to
	 *
	 * @param name
	 *            the name of the variable
	 * @param variableType
	 *            sets if this variable is a Message variable or simple BPEL
	 *            variable
	 * @param declarationId
	 *            the XSD Type of the variable
	 * @return
	 */
	public boolean addVariable(String name, TOSCAPlan.VariableType variableType, QName declarationId) {
		declarationId = this.importNamespace(declarationId);
		return this.bpelTemplateHandler.addVariable(name, variableType, declarationId, this.templateBuildPlan);
	}

	public boolean addGlobalVariable(String name, TOSCAPlan.VariableType variableType, QName declarationId) {
		declarationId = this.importNamespace(declarationId);
		return this.bpelProcessHandler.addVariable(name, variableType, declarationId,
				this.templateBuildPlan.getBuildPlan());
	}

	/**
	 * Adds a partnerlinkType to the BuildPlan, which can be used for
	 * partnerLinks in the TemplateBuildPlan
	 *
	 * @param partnerLinkTypeName
	 *            the name of the partnerLinkType
	 * @param roleName
	 *            the name of the 1st role
	 * @param portType
	 *            the portType of the partnerLinkType
	 * @return true if adding the partnerLinkType was successful, else false
	 */
	public boolean addPartnerLinkType(String partnerLinkTypeName, String roleName, QName portType) {
		portType = this.importNamespace(portType);
		return this.bpelProcessHandler.addPartnerLinkType(partnerLinkTypeName, roleName, portType,
				this.templateBuildPlan.getBuildPlan());
	}

	/**
	 * Returns an Integer which can be used as variable names etc. So that there
	 * are no collisions with other declarations
	 *
	 * @return an Integer
	 */
	public int getIdForNames() {
		int idToReturn = this.templateBuildPlan.getBuildPlan().getId();
		this.templateBuildPlan.getBuildPlan().setId(idToReturn + 1);
		return idToReturn;
	}

	/**
	 * Adds a partnerLinkType to the BuildPlan which can be used for
	 * partnerLinks in TemplateBuildPlans
	 *
	 * @param partnerLinkTypeName
	 *            the name of the partnerLinkTypes
	 * @param role1Name
	 *            the name of the 1st role
	 * @param portType1
	 *            the 1st portType
	 * @param role2Name
	 *            the name of the 2nd role
	 * @param portType2
	 *            the 2nd porType
	 * @return true if adding the partnerLinkType was successful, else false
	 */
	public boolean addPartnerLinkType(String partnerLinkTypeName, String role1Name, QName portType1, String role2Name,
			QName portType2) {
		portType1 = this.importNamespace(portType1);
		portType2 = this.importNamespace(portType2);
		return this.bpelProcessHandler.addPartnerLinkType(partnerLinkTypeName, role1Name, portType1, role2Name,
				portType2, this.templateBuildPlan.getBuildPlan());
	}

	/**
	 * Adds a partnerLink to the TemplateBuildPlan of the Template this context
	 * handles
	 *
	 * @param partnerLinkName
	 *            the name of the partnerLink
	 * @param partnerLinkType
	 *            the name of the partnerLinkType
	 * @param myRole
	 *            the name of a role inside the partnerLinkType for the myRole
	 * @param partnerRole
	 *            the name of a role inside ther partnerLinkType for the
	 *            partnerRole
	 * @param initializePartnerRole
	 *            whether the partnerRole should be initialized
	 * @return true if adding the partnerLink was successful, else false
	 */
	public boolean addPartnerLinkToTemplateScope(String partnerLinkName, String partnerLinkType, String myRole,
			String partnerRole, boolean initializePartnerRole) {
		boolean check = true;
		// here we set the qname with namespace of the plan "ba.example"
		QName partnerType = new QName(this.planNamespace, partnerLinkType, "tns");
		check &= this.addPLtoDeploy(partnerLinkName, partnerLinkType);
		check &= this.templateHandler.addPartnerLink(partnerLinkName, partnerType, myRole, partnerRole,
				initializePartnerRole, this.templateBuildPlan);
		return check;
	}

	/**
	 * Adds a partnerLink to the deployment deskriptor of the BuildPlan
	 *
	 * @param partnerLinkName
	 *            the name of the partnerLink
	 * @param partnerLinkType
	 *            the name of the partnerLinkType
	 * @return true if adding the partnerLink to the deployment deskriptor was
	 *         successful, else false
	 */
	private boolean addPLtoDeploy(String partnerLinkName, String partnerLinkType) {
		TOSCAPlan buildPlan = this.templateBuildPlan.getBuildPlan();
		GenericWsdlWrapper wsdl = buildPlan.getWsdl();

		// get porttypes inside partnerlinktype
		QName portType1 = wsdl.getPortType1FromPartnerLinkType(partnerLinkType);
		QName portType2 = wsdl.getPortType2FromPartnerLinkType(partnerLinkType);
		// QName portTypeToAdd;
		// if (portType1.getNamespaceURI().equals(this.planNamespace) &&
		// portType1.getLocalPart().equals(this.portName)) {
		// portTypeToAdd = portType2;
		// } else {
		// portTypeToAdd = portType1;
		// }

		// check for port in used wsdl
		// List<File> wsdlFiles = this.getWSDLFiles();
		// for (File wsdlFile : wsdlFiles) {
		// try {
		// if (this.containsPortType(portTypeToAdd, wsdlFile)) {
		// // List<Port> ports =
		// // this.getPortsInWSDLFileForPortType(portTypeToAdd,
		// // wsdlFile);
		// List<Service> services =
		// this.getServiceInWSDLFileForPortType(portTypeToAdd, wsdlFile);
		// List<Port> ports = this.getPortsFromService(services.get(0));
		// this.buildPlanHandler.addInvokeToDeploy(partnerLinkName,
		// services.get(0).getQName(), ports.get(0).getName(), buildPlan);
		// }
		// } catch (WSDLException e) {
		// e.printStackTrace();
		// return false;
		// }
		//
		// }
		List<File> wsdlFiles = this.getWSDLFiles();
		for (File wsdlFile : wsdlFiles) {
			try {
				// TODO: in both if blocks we make huge assumptions with the
				// get(0)'s, as a wsdl file can have multiple services with
				// given portTypes

				// if we only have one portType in the partnerLink, we just add
				// a invoke
				if (((portType1 != null) & (portType2 == null)) && this.containsPortType(portType1, wsdlFile)) {
					// List<Port> ports =
					// this.getPortsInWSDLFileForPortType(portTypeToAdd,
					// wsdlFile);
					List<Service> services = this.getServicesInWSDLFile(wsdlFile, portType1);
					List<Port> ports = this.getPortsFromService(services.get(0), portType1);
					this.buildPlanHandler.addInvokeToDeploy(partnerLinkName, services.get(0).getQName(),
							ports.get(0).getName(), buildPlan);
				}

				// if two porttypes are used in this partnerlink, the first
				// portType is used as provided interface, while the second is
				// invoked
				if (((portType1 != null) & (portType2 != null))
						&& (this.containsPortType(portType1, wsdlFile) & this.containsPortType(portType2, wsdlFile))) {
					// portType1 resembles a service to provide
					List<Service> services = this.getServicesInWSDLFile(wsdlFile, portType1);
					List<Port> ports = this.getPortsFromService(services.get(0), portType1);
					this.buildPlanHandler.addProvideToDeploy(partnerLinkName, services.get(0).getQName(),
							ports.get(0).getName(), buildPlan);

					// portType2 resembles a service to invoke
					List<Service> outboundServices = this.getServicesInWSDLFile(wsdlFile, portType2);
					List<Port> outboundPorts = this.getPortsFromService(outboundServices.get(0), portType2);
					this.buildPlanHandler.addInvokeToDeploy(partnerLinkName, outboundServices.get(0).getQName(),
							outboundPorts.get(0).getName(), buildPlan);
				}
			} catch (WSDLException e) {
				TemplatePlanContext.LOG.error("Error while reading WSDL data", e);
				return false;
			}

		}
		return true;
	}

	/**
	 * Returns the WSDL Ports of the given WSDL Service, that have binding with
	 * the given WSDL PortType
	 *
	 * @param service
	 *            the WSDL Service
	 * @param portType
	 *            the PortType which the Bindings of the Ports implement
	 * @return a List of Port which belong to the service and have a Binding
	 *         with the given PortType
	 */
	private List<Port> getPortsFromService(Service service, QName portType) {
		List<Port> ports = this.getPortsFromService(service);
		List<Port> portsWithPortType = new ArrayList<Port>();
		for (Port port : ports) {
			if (port.getBinding().getPortType().getQName().equals(portType)) {
				portsWithPortType.add(port);
			}
		}

		return portsWithPortType;
	}

	/**
	 * Returns the WSDL Ports of the given WSDL Service
	 *
	 * @param service
	 *            the WSDL Service
	 * @return a List of Port which belong to the service
	 */
	private List<Port> getPortsFromService(Service service) {
		List<Port> portOfService = new ArrayList<Port>();
		Map ports = service.getPorts();
		for (Object key : ports.keySet()) {
			portOfService.add((Port) ports.get(key));
		}
		return portOfService;
	}

	/**
	 * Returns the Services inside the given WSDL file which implement the given
	 * portType
	 *
	 * @param portType
	 *            the portType to search with
	 * @param wsdlFile
	 *            the WSDL file to look in
	 * @return a List of Service which implement the given portType
	 * @throws WSDLException
	 *             is thrown when the WSDLFactory to read the WSDL can't be
	 *             initialized
	 */
	private List<Service> getServicesInWSDLFile(File wsdlFile, QName portType) throws WSDLException {
		List<Service> servicesInWsdl = new ArrayList<Service>();

		WSDLFactory factory = WSDLFactory.newInstance();
		WSDLReader reader = factory.newWSDLReader();
		reader.setFeature("javax.wsdl.verbose", false);
		Definition wsdlInstance = reader.readWSDL(wsdlFile.getAbsolutePath());
		Map services = wsdlInstance.getAllServices();
		for (Object key : services.keySet()) {
			Service service = (Service) services.get(key);
			Map ports = service.getPorts();
			for (Object portKey : ports.keySet()) {
				Port port = (Port) ports.get(portKey);
				if (port.getBinding().getPortType().getQName().getNamespaceURI().equals(portType.getNamespaceURI())
						&& port.getBinding().getPortType().getQName().getLocalPart().equals(portType.getLocalPart())) {
					servicesInWsdl.add(service);
				}
			}
		}

		return servicesInWsdl;
	}

	/**
	 * Returns all files of the BuildPlan which have the ending ".wsdl"
	 *
	 * @return a List of File which have the ending ".wsdl"
	 */
	private List<File> getWSDLFiles() {
		List<File> wsdlFiles = new ArrayList<File>();
		TOSCAPlan buildPlan = this.templateBuildPlan.getBuildPlan();
		for (File file : buildPlan.getImportedFiles()) {
			if (file.getName().endsWith(".wsdl")) {
				wsdlFiles.add(file);
			}
		}
		return wsdlFiles;
	}

	/**
	 * Registers a portType with the associated WSDL File in the BuildPlan
	 *
	 * @param portType
	 *            the portType to register
	 * @param wsdlDefinitionsFile
	 *            the WSDL file where the portType is declared
	 * @return a QName for portType with set prefix etc. after registration
	 *         within the BuildPlan
	 */
	public QName registerPortType(QName portType, File wsdlDefinitionsFile) {
		portType = this.importNamespace(portType);
		boolean check = true;
		// import wsdl into plan wsdl
		check &= this.templateBuildPlan.getBuildPlan().getWsdl().addImportElement("http://schemas.xmlsoap.org/wsdl/",
				portType.getNamespaceURI(), portType.getPrefix(),

				wsdlDefinitionsFile.getAbsolutePath());
		if (!check && this.templateBuildPlan.getBuildPlan().getWsdl().isImported(portType,
				wsdlDefinitionsFile.getAbsolutePath())) {
			// check if already imported
			check = true;
		}
		// import wsdl into bpel plan
		check &= this.buildPlanHandler.addImportToBpel(portType.getNamespaceURI(),
				wsdlDefinitionsFile.getAbsolutePath(), "http://schemas.xmlsoap.org/wsdl/",
				this.templateBuildPlan.getBuildPlan());

		if (!check && this.buildPlanHandler.hasImport(portType.getNamespaceURI(), wsdlDefinitionsFile.getAbsolutePath(),
				"http://schemas.xmlsoap.org/wsdl/", this.templateBuildPlan.getBuildPlan())) {
			check = true;
		}

		// add file to imported files of buildplan
		this.buildPlanHandler.addImportedFile(wsdlDefinitionsFile, this.templateBuildPlan.getBuildPlan());
		return (check) ? portType : null;
	}

	/**
	 * Returns all InfrastructureNodes of the Template this context belongs to
	 *
	 * @return a List of AbstractNodeTemplate which are
	 *         InfrastructureNodeTemplate of the template this context handles
	 */
	public List<AbstractNodeTemplate> getInfrastructureNodes() {
		List<AbstractNodeTemplate> infrastructureNodes = new ArrayList<AbstractNodeTemplate>();
		if (this.templateBuildPlan.getNodeTemplate() != null) {
			Utils.getInfrastructureNodes(this.getNodeTemplate(), infrastructureNodes);
		} else {
			AbstractRelationshipTemplate template = this.templateBuildPlan.getRelationshipTemplate();
			if (Utils.getRelationshipBaseType(template).equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
				Utils.getInfrastructureNodes(template, infrastructureNodes, true);
				Utils.getInfrastructureNodes(template, infrastructureNodes, false);
			} else {
				Utils.getInfrastructureNodes(template, infrastructureNodes, false);
			}

		}
		return infrastructureNodes;
	}

	/**
	 * Returns all InfrastructureNodes of the Template this context belongs to
	 *
	 * @param forSource
	 *            whether to look for InfrastructureNodes along the Source
	 *            relations or Target relations
	 * @return a List of AbstractNodeTemplate which are
	 *         InfrastructureNodeTemplate of the template this context handles
	 */
	public List<AbstractNodeTemplate> getInfrastructureNodes(boolean forSource) {
		List<AbstractNodeTemplate> infrastructureNodes = new ArrayList<AbstractNodeTemplate>();
		if (this.templateBuildPlan.getNodeTemplate() != null) {
			Utils.getInfrastructureNodes(this.getNodeTemplate(), infrastructureNodes);
		} else {
			AbstractRelationshipTemplate template = this.templateBuildPlan.getRelationshipTemplate();
			Utils.getInfrastructureNodes(template, infrastructureNodes, forSource);
		}
		return infrastructureNodes;
	}

	/**
	 * Returns alls InfrastructureEdges of the Template this context belongs to
	 *
	 * @return a List of AbstractRelationshipTemplate which are
	 *         InfrastructureEdges of the template this context handles
	 */
	public List<AbstractRelationshipTemplate> getInfrastructureEdges() {
		List<AbstractRelationshipTemplate> infraEdges = new ArrayList<AbstractRelationshipTemplate>();
		if (this.templateBuildPlan.getNodeTemplate() != null) {
			Utils.getInfrastructureEdges(this.getNodeTemplate(), infraEdges);
		} else {
			AbstractRelationshipTemplate template = this.templateBuildPlan.getRelationshipTemplate();
			if (Utils.getRelationshipBaseType(template).equals(Utils.TOSCABASETYPE_CONNECTSTO)) {
				Utils.getInfrastructureEdges(template, infraEdges, true);
				Utils.getInfrastructureEdges(template, infraEdges, false);
			} else {
				Utils.getInfrastructureEdges(template, infraEdges, false);
			}
		}
		return infraEdges;
	}

	/**
	 * Registers a portType which is declared inside the given
	 * AbstractArtifactReference
	 *
	 * @param portType
	 *            the portType to register
	 * @param ref
	 *            ArtifactReference where the portType is declared
	 * @return a QName for the registered PortType with a set prefix
	 */
	public QName registerPortType(QName portType, AbstractArtifactReference ref) {
		return this.registerPortType(portType,
				this.templateBuildPlan.getBuildPlan().getDefinitions().getAbsolutePathOfArtifactReference(ref));
	}

	/**
	 * Checks whether the given portType is declared in the given WSDL File
	 *
	 * @param portType
	 *            the portType to check with
	 * @param wsdlFile
	 *            the WSDL File to check in
	 * @return true if the portType is declared in the given WSDL file
	 * @throws WSDLException
	 *             is thrown when either the given File is not a WSDL File or
	 *             initializing the WSDL Factory failed
	 */
	public boolean containsPortType(QName portType, File wsdlFile) throws WSDLException {
		WSDLFactory factory = WSDLFactory.newInstance();
		WSDLReader reader = factory.newWSDLReader();
		reader.setFeature("javax.wsdl.verbose", false);
		Definition wsdlInstance = reader.readWSDL(wsdlFile.getAbsolutePath());
		Map portTypes = wsdlInstance.getAllPortTypes();
		for (Object key : portTypes.keySet()) {
			PortType portTypeInWsdl = (PortType) portTypes.get(key);
			if (portTypeInWsdl.getQName().getNamespaceURI().equals(portType.getNamespaceURI())
					&& portTypeInWsdl.getQName().getLocalPart().equals(portType.getLocalPart())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a List of Port which implement the given portType inside the
	 * given WSDL File
	 *
	 * @param portType
	 *            the portType to use
	 * @param wsdlFile
	 *            the WSDL File to look in
	 * @return a List of Port which implement the given PortType
	 * @throws WSDLException
	 *             is thrown when the given File is not a WSDL File or
	 *             initializing the WSDL Factory failed
	 */
	public List<Port> getPortsInWSDLFileForPortType(QName portType, File wsdlFile) throws WSDLException {
		List<Port> wsdlPorts = new ArrayList<Port>();
		// taken from http://www.java.happycodings.com/Other/code24.html
		WSDLFactory factory = WSDLFactory.newInstance();
		WSDLReader reader = factory.newWSDLReader();
		reader.setFeature("javax.wsdl.verbose", false);
		Definition wsdlInstance = reader.readWSDL(wsdlFile.getAbsolutePath());
		Map services = wsdlInstance.getAllServices();
		for (Object key : services.keySet()) {
			Service service = (Service) services.get(key);
			Map ports = service.getPorts();
			for (Object portKey : ports.keySet()) {
				Port port = (Port) ports.get(portKey);
				if (port.getBinding().getPortType().getQName().getNamespaceURI().equals(portType.getNamespaceURI())
						&& port.getBinding().getPortType().getQName().getLocalPart().equals(portType.getLocalPart())) {
					wsdlPorts.add(port);
				}
			}
		}
		return wsdlPorts;
	}

	/**
	 * Returns the name of variable which the given property name belongs to
	 *
	 * @param propertyName
	 *            a LocalName of a Property of the Template this context belongs
	 *            to
	 * @return a String containing the variable name of the property, else null
	 */
	public String getVarNameOfTemplateProperty(String propertyName) {
		Map<String, String> propertyMapping = null;
		if (this.templateBuildPlan.getNodeTemplate() != null) {
			propertyMapping = this.propertyMap.getPropertyMappingMap(this.templateBuildPlan.getNodeTemplate().getId());
		} else {
			propertyMapping = this.propertyMap
					.getPropertyMappingMap(this.templateBuildPlan.getRelationshipTemplate().getId());
		}
		return propertyMapping.get(propertyName);
	}

	/**
	 * Returns the variable name of the given template and property localName
	 *
	 * @param templateId
	 *            the Id of the Template to look in
	 * @param propertyName
	 *            the LocalName of a Template Property
	 * @return a String containing the variable name, else null
	 */
	public String getVariableNameOfProperty(String templateId, String propertyName) {
		if (this.propertyMap.getPropertyMappingMap(templateId) != null) {
			return this.propertyMap.getPropertyMappingMap(templateId).get(propertyName);
		} else {
			return null;
		}
	}

	/**
	 * Returns the variable name of the first occurence of a property with the
	 * given Property name of InfrastructureNodes
	 *
	 * @param propertyName
	 * @return a String containing the variable name, else null
	 */
	public String getVariableNameOfInfraNodeProperty(String propertyName) {
		for (AbstractNodeTemplate infraNode : this.getInfrastructureNodes()) {
			if ((this.propertyMap.getPropertyMappingMap(infraNode.getId()) != null)
					&& this.propertyMap.getPropertyMappingMap(infraNode.getId()).containsKey(propertyName)) {
				return this.propertyMap.getPropertyMappingMap(infraNode.getId()).get(propertyName);
			}
		}
		return null;
	}

	/**
	 * Adds the namespace inside the given QName to the buildPlan
	 *
	 * @param qname
	 *            a QName with set prefix and namespace
	 * @return true if adding the namespace was successful, else false
	 */
	private boolean addNamespaceToBPELDoc(QName qname) {
		return this.bpelProcessHandler.addNamespaceToBPELDoc(qname.getPrefix(), qname.getNamespaceURI(),
				this.templateBuildPlan.getBuildPlan());
	}

	/**
	 * Returns a prefix for the given namespace if it is declared in the
	 * buildPlan
	 *
	 * @param namespace
	 *            the namespace to get the prefix for
	 * @return a String containing the prefix, else null
	 */
	public String getPrefixForNamespace(String namespace) {
		if (this.namespaceMap.containsValue(namespace)) {
			for (String key : this.namespaceMap.keySet()) {
				if (this.namespaceMap.get(key).equals(namespace)) {
					return key;
				}
			}
		}
		return null;
	}

	/**
	 * Imports the given QName into the BuildPlan
	 *
	 * @param qname
	 *            the QName to import
	 * @return the imported QName with set prefix
	 */
	public QName importQName(QName qname) {
		return this.importNamespace(qname);
	}

	/**
	 * Imports the given QName Namespace into the BuildPlan
	 *
	 * @param qname
	 *            a QName to import
	 * @return the QName with set prefix
	 */
	private QName importNamespace(QName qname) {
		String prefix = qname.getPrefix();
		String namespace = qname.getNamespaceURI();
		boolean prefixInUse = false;
		boolean namespaceInUse = false;

		// check if prefix is in use
		if ((prefix != null) && !prefix.isEmpty()) {
			prefixInUse = this.namespaceMap.containsKey(prefix);
		}

		// check if namespace is in use
		if ((namespace != null) && !namespace.isEmpty()) {
			namespaceInUse = this.namespaceMap.containsValue(namespace);
		}

		// TODO refactor this whole thing
		if (prefixInUse & namespaceInUse) {
			// both is already registered, this means we set the prefix of the
			// given qname to the prefix used in the system
			for (String key : this.namespaceMap.keySet()) {
				if (this.namespaceMap.get(key).equals(namespace)) {
					prefix = key;
				}
			}
		} else if (!prefixInUse & namespaceInUse) {
			// the prefix isn't in use, but the namespace is, re-set the prefix
			for (String key : this.namespaceMap.keySet()) {
				if (this.namespaceMap.get(key).equals(namespace)) {
					prefix = key;
				}
			}
		} else if (!prefixInUse & !namespaceInUse) {
			// just add the namespace and prefix to the system
			if ((prefix == null) || prefix.isEmpty()) {
				// generate new prefix
				prefix = "ns" + this.namespaceMap.keySet().size();
			}
			this.namespaceMap.put(prefix, namespace);
			this.addNamespaceToBPELDoc(new QName(namespace, qname.getLocalPart(), prefix));

		} else {
			if ((prefix == null) || prefix.isEmpty()) {
				// generate new prefix
				prefix = "ns" + this.namespaceMap.keySet().size();
			}
			this.namespaceMap.put(prefix, namespace);
			this.addNamespaceToBPELDoc(new QName(namespace, qname.getLocalPart(), prefix));
		}
		return new QName(namespace, qname.getLocalPart(), prefix);
	}

	/**
	 * Registers XML Schema Types in the given BPEL Plan
	 *
	 * @param type
	 *            QName of the XML Schema Type
	 * @param xmlSchemaFile
	 *            file where the type is declared in
	 * @return true if registered type successful, else false
	 */
	public boolean registerType(QName type, File xmlSchemaFile) {
		boolean check = true;
		// add as imported file to plan
		check &= this.buildPlanHandler.addImportedFile(xmlSchemaFile, this.templateBuildPlan.getBuildPlan());
		// import type inside bpel file
		check &= this.buildPlanHandler.addImportToBpel(type.getNamespaceURI(), xmlSchemaFile.getAbsolutePath(),
				"http://www.w3.org/2001/XMLSchema", this.templateBuildPlan.getBuildPlan());
		return true;
	}

	/**
	 * Registers the given namespace as extension inside the BuildPlan
	 *
	 * @param namespace
	 *            the namespace of the extension
	 * @param mustUnderstand
	 *            the mustUnderstand attribute
	 * @return true if adding was successful, else false
	 */
	public boolean registerExtension(String namespace, boolean mustUnderstand) {
		return this.buildPlanHandler.registerExtension(namespace, mustUnderstand,
				this.templateBuildPlan.getBuildPlan());
	}

	/**
	 * Creates an element with given namespace and localName for the BuildPlan
	 * Document
	 *
	 * @param namespace
	 *            the namespace of the element
	 * @param localName
	 *            the localName of the element
	 * @return a new Element created with the BuildPlan document
	 */
	public Element createElement(String namespace, String localName) {
		return this.templateBuildPlan.getBpelDocument().createElementNS(namespace, localName);
	}

	/**
	 * Returns the PrePhas Element of the TemplateBuildPlan this context belongs
	 * to
	 *
	 * @return a Element which is the PrePhase Element
	 */
	public Element getPrePhaseElement() {
		return this.templateBuildPlan.getBpelSequencePrePhaseElement();
	}

	/**
	 * Returns the ProvPhase Element of the TemplateBuildPlan this context
	 * belongs to
	 *
	 * @return a Element which is the ProvPhase Element
	 */
	public Element getProvisioningPhaseElement() {
		return this.templateBuildPlan.getBpelSequenceProvisioningPhaseElement();
	}

	/**
	 * Returns the PostPhase Element of the TemplateBuildPlan this context
	 * belongs to
	 *
	 * @return a Element which is the PostPhase Element
	 */
	public Element getPostPhaseElement() {
		return this.templateBuildPlan.getBpelSequencePostPhaseElement();
	}

	/**
	 * Adds a Element which is a String parameter to the BuildPlan request
	 * message
	 *
	 * @param localName
	 *            the localName of the Element to add
	 * @return true if adding was successful, else false
	 */
	public boolean addStringValueToPlanRequest(String localName) {
		return this.buildPlanHandler.addStringElementToPlanRequest(localName, this.templateBuildPlan.getBuildPlan());
	}

	/**
	 * Adds a copy element to the main assign element of the buildPlan this
	 * context belongs to
	 * 
	 * @param inputRequestLocalName
	 *            the localName inside the input request message
	 * @param internalVariable
	 *            an internalVariable of this buildPlan
	 * @return true iff adding the copy was successful, else false
	 */
	public boolean addAssignFromInput2VariableToMainAssign(String inputRequestLocalName, Variable internalVariable) {
		return this.bpelProcessHandler.assignVariableValueFromInput(internalVariable.getName(), inputRequestLocalName,
				this.templateBuildPlan.getBuildPlan());
	}

	/**
	 * Adds a Element which is a String parameter to the BuildPlan response
	 * message
	 *
	 * @param localName
	 *            the localName of the Element to add
	 * @return true if adding was successful, else false
	 */
	public boolean addStringValueToPlanResponse(String localName) {
		return this.buildPlanHandler.addStringElementToPlanResponse(localName, this.templateBuildPlan.getBuildPlan());
	}

	/**
	 * Imports the given Node into the BuildPlan Document, to be able to append
	 * it to the Phases
	 *
	 * @param node
	 *            the Node to import into the Document
	 * @return the imported Node
	 */
	public Node importNode(Node node) {
		return this.templateBuildPlan.getBuildPlan().getBpelDocument().importNode(node, true);
	}

	/**
	 * Returns a NCName String of the given String
	 *
	 * @param string
	 *            a String to convert
	 * @return the String which is a NCName
	 */
	public String getNCNameFromString(String string) {
		// TODO check if this enough
		return string.replace(" ", "_");
	}

	/**
	 * <p>
	 * This is a Wrapper class for Template Id to Property variable name
	 * <p>
	 * Copyright 2013 IAAS University of Stuttgart <br>
	 * <br>
	 *
	 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
	 *
	 */
	public class Variable {

		private String templateId;
		private String variableName;

		/**
		 * Contructor
		 *
		 * @param templateId
		 *            a TemplateId
		 * @param variableName
		 *            a property variable name
		 */
		private Variable(String templateId, String variableName) {
			this.templateId = templateId;
			this.variableName = variableName;
		}

		/**
		 * Returns the template id of this wrapper
		 *
		 * @return a String containing the TemplateId
		 */
		public String getTemplateId() {
			return this.templateId;
		}

		/**
		 * Returns the property variable name of this wrapper
		 *
		 * @return a String containing the property variable name
		 */
		public String getName() {
			return this.variableName;
		}
	}

	/**
	 * Returns a Variable object that represents a property inside the given
	 * nodeTemplate with the given name
	 * 
	 * @param nodeTemplate
	 *            a nodeTemplate to look for the property in
	 * @param localName
	 *            the name of the searched property
	 * @return a Variable object representing the property
	 */
	public Variable getPropertyVariable(AbstractNodeTemplate nodeTemplate, String localName) {
		if (nodeTemplate.getProperties() == null || nodeTemplate.getProperties().getDOMElement() == null) {
			return null;
		}

		NodeList propertyNodes = nodeTemplate.getProperties().getDOMElement().getChildNodes();
		for (int index = 0; index < propertyNodes.getLength(); index++) {
			Node propertyNode = propertyNodes.item(index);
			if (propertyNode.getNodeType() == Node.ELEMENT_NODE && propertyNode.getLocalName().equals(localName)) {
				return new Variable(nodeTemplate.getId(),
						this.getVariableNameOfProperty(nodeTemplate.getId(), propertyNode.getLocalName()));
			}
		}

		return null;
	}

	/**
	 * Looks for a Property with the same localName as the given toscaParameter.
	 * The search is on the whole TopologyTemplate this TemplateContext belongs
	 * to.
	 *
	 * @param localName
	 *            a String
	 * @return a Variable Object with TemplateId and Name, if null the whole
	 *         Topology has no Property with the specified localName
	 */
	public Variable getPropertyVariable(String localName) {
		// then on everything else
		for (AbstractNodeTemplate infraNode : this.getAllNodeTemplates()) {
			if ((infraNode.getProperties() == null) || (infraNode.getProperties().getDOMElement() == null)) {
				continue;
			}
			NodeList infraNodePropChilde = infraNode.getProperties().getDOMElement().getChildNodes();
			for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
				Node infraNodeProp = infraNodePropChilde.item(index);
				if (localName.equals(infraNodeProp.getLocalName())) {
					return new Variable(infraNode.getId(),
							this.getVariableNameOfProperty(infraNode.getId(), infraNodeProp.getLocalName()));
				}
			}
		}

		for (AbstractRelationshipTemplate infraEdge : this.getAllRelationshipTemplates()) {
			if ((infraEdge.getProperties() == null) || (infraEdge.getProperties().getDOMElement() == null)) {
				continue;
			}
			NodeList infraNodePropChilde = infraEdge.getProperties().getDOMElement().getChildNodes();
			for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
				Node infraEdgeProp = infraNodePropChilde.item(index);
				if (localName.equals(infraEdgeProp.getLocalName())) {
					return new Variable(infraEdge.getId(),
							this.getVariableNameOfProperty(infraEdge.getId(), infraEdgeProp.getLocalName()));
				}
			}
		}

		return null;
	}

	/**
	 *
	 * Looks for a Property with the same localName as the given String. The
	 * search is on either the Infrastructure on the Source or Target of the
	 * Template this TemplateContext belongs to.
	 *
	 * @param localName
	 *            a String
	 * @param forSource
	 *            whether to look in direction of the sinks or sources (If
	 *            Template is NodeTemplate) or to search on the
	 *            Source-/Target-Interface (if template is RelationshipTemplate)
	 * @return a Variable Object with TemplateId and Name, if null the whole
	 *         Infrastructure has no Property with the specified localName
	 */
	public Variable getPropertyVariable(String localName, boolean forSource) {
		List<AbstractNodeTemplate> infraNodes = new ArrayList<AbstractNodeTemplate>();

		if (this.isNodeTemplate()) {
			if (forSource) {
				// get all NodeTemplates that are reachable from this
				// nodeTemplate
				Utils.getNodesFromNodeToSink(this.getNodeTemplate(), infraNodes);
			} else {
				Utils.getNodesFromNodeToSource(this.getNodeTemplate(), infraNodes);
			}
		} else {
			if (forSource) {
				Utils.getNodesFromNodeToSink(this.getRelationshipTemplate().getSource(), infraNodes);
			} else {
				Utils.getNodesFromRelationToSink(this.getRelationshipTemplate(), infraNodes);
			}
		}

		for (AbstractNodeTemplate infraNode : infraNodes) {
			if ((infraNode.getProperties() == null) || (infraNode.getProperties().getDOMElement() == null)) {
				continue;
			}
			NodeList infraNodePropChilde = infraNode.getProperties().getDOMElement().getChildNodes();
			for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
				Node infraNodeProp = infraNodePropChilde.item(index);
				if (localName.equals(infraNodeProp.getLocalName())) {
					return new Variable(infraNode.getId(),
							this.getVariableNameOfProperty(infraNode.getId(), infraNodeProp.getLocalName()));
				}
			}
		}
		return null;
	}

	/**
	 * Returns a Map with ToscaParameter Names as Key and a Wrapper for
	 * (TemplateId,PropertyVariableName) as Value.
	 *
	 * The Parameters to Property Mapping is calculated on all Infrastructure
	 * Element (Nodes, Edges) of the Template this context belongs to.
	 *
	 * @param toscaParameters
	 *            Set of Parameter Names for which the list of internal
	 *            properties or external properties should be calculated
	 * @return Map<String, TemplatePropWrapper> if a value is null, it indicates
	 *         that the parameter is external. The Key is a TOSCA Operation
	 *         Parameters and value is a Mapping from TemplateId to Property
	 *         Variable name
	 */
	// this method is to mighty -> non-deterministic
	@Deprecated
	public Map<String, Variable> getInternalExternalParameters(Set<String> toscaParameters) {
		// initialize hashmap to save found matches
		Map<String, Variable> matchMap = new HashMap<String, Variable>();
		for (String param : toscaParameters) {
			matchMap.put(param, null);
		}

		// check for properties inside context template
		String id;
		NodeList TemplateChilde = null;
		if ((this.getNodeTemplate() != null) && (this.getNodeTemplate().getProperties() != null)
				&& (this.getNodeTemplate().getProperties().getDOMElement() != null)) {
			id = this.getNodeTemplate().getId();
			TemplateChilde = this.getNodeTemplate().getProperties().getDOMElement().getChildNodes();
		} else if ((this.getRelationshipTemplate() != null) && (this.getRelationshipTemplate().getProperties() != null)
				&& (this.getRelationshipTemplate().getProperties().getDOMElement() != null)) {
			id = this.getRelationshipTemplate().getId();
			TemplateChilde = this.getRelationshipTemplate().getProperties().getDOMElement().getChildNodes();
		}
		if (TemplateChilde != null) {
			for (int index = 0; index < TemplateChilde.getLength(); index++) {
				Node nodeTemplateProp = TemplateChilde.item(index);
				if (matchMap.containsKey(nodeTemplateProp.getLocalName())
						&& (matchMap.get(nodeTemplateProp.getLocalName()) == null)) {
					matchMap.put(nodeTemplateProp.getLocalName(), new Variable(this.getNodeTemplate().getId(),
							this.getVarNameOfTemplateProperty(nodeTemplateProp.getLocalName())));
				}
			}
		}

		// check for matches first on the infrastructure
		for (AbstractNodeTemplate infraNode : this.getInfrastructureNodes()) {
			if ((infraNode.getProperties() == null) || (infraNode.getProperties().getDOMElement() == null)) {
				continue;
			}
			NodeList infraNodePropChilde = infraNode.getProperties().getDOMElement().getChildNodes();
			for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
				Node infraNodeProp = infraNodePropChilde.item(index);
				if (matchMap.containsKey(infraNodeProp.getLocalName())
						&& (matchMap.get(infraNodeProp.getLocalName()) == null)) {
					matchMap.put(infraNodeProp.getLocalName(), new Variable(infraNode.getId(),
							this.getVariableNameOfProperty(infraNode.getId(), infraNodeProp.getLocalName())));
				}
			}
		}

		for (AbstractRelationshipTemplate infraEdge : this.getInfrastructureEdges()) {
			if ((infraEdge.getProperties() == null) || (infraEdge.getProperties().getDOMElement() == null)) {
				continue;
			}
			NodeList infraNodePropChilde = infraEdge.getProperties().getDOMElement().getChildNodes();
			for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
				Node infraEdgeProp = infraNodePropChilde.item(index);
				if (matchMap.containsKey(infraEdgeProp.getLocalName())
						&& (matchMap.get(infraEdgeProp.getLocalName()) == null)) {
					matchMap.put(infraEdgeProp.getLocalName(), new Variable(infraEdge.getId(),
							this.getVariableNameOfProperty(infraEdge.getId(), infraEdgeProp.getLocalName())));
				}
			}
		}

		// then on everything else
		for (AbstractNodeTemplate infraNode : this.getAllNodeTemplates()) {
			if ((infraNode.getProperties() == null) || (infraNode.getProperties().getDOMElement() == null)) {
				continue;
			}
			NodeList infraNodePropChilde = infraNode.getProperties().getDOMElement().getChildNodes();
			for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
				Node infraNodeProp = infraNodePropChilde.item(index);
				if (matchMap.containsKey(infraNodeProp.getLocalName())
						&& (matchMap.get(infraNodeProp.getLocalName()) == null)) {
					matchMap.put(infraNodeProp.getLocalName(), new Variable(infraNode.getId(),
							this.getVariableNameOfProperty(infraNode.getId(), infraNodeProp.getLocalName())));
				}
			}
		}

		for (AbstractRelationshipTemplate infraEdge : this.getAllRelationshipTemplates()) {
			if ((infraEdge.getProperties() == null) || (infraEdge.getProperties().getDOMElement() == null)) {
				continue;
			}
			NodeList infraNodePropChilde = infraEdge.getProperties().getDOMElement().getChildNodes();
			for (int index = 0; index < infraNodePropChilde.getLength(); index++) {
				Node infraEdgeProp = infraNodePropChilde.item(index);
				if (matchMap.containsKey(infraEdgeProp.getLocalName())
						&& (matchMap.get(infraEdgeProp.getLocalName()) == null)) {
					matchMap.put(infraEdgeProp.getLocalName(), new Variable(infraEdge.getId(),
							this.getVariableNameOfProperty(infraEdge.getId(), infraEdgeProp.getLocalName())));
				}
			}
		}

		return matchMap;
	}

	/**
	 * Returns all NodeTemplates of the BuildPlan
	 *
	 * @return a List of AbstractNodeTemplates
	 */
	private List<AbstractNodeTemplate> getAllNodeTemplates() {
		List<AbstractNodeTemplate> list = new ArrayList<AbstractNodeTemplate>();

		for (TemplateBuildPlan template : this.templateBuildPlan.getBuildPlan().getTemplateBuildPlans()) {
			if (template.getNodeTemplate() != null) {
				list.add(template.getNodeTemplate());
			}
		}
		return list;
	}

	/**
	 * Returns all RelationshipTemplate of the BuildPlan
	 *
	 * @return a List of AbstractRelationshipTemplates
	 */
	private List<AbstractRelationshipTemplate> getAllRelationshipTemplates() {
		List<AbstractRelationshipTemplate> list = new ArrayList<AbstractRelationshipTemplate>();

		for (TemplateBuildPlan template : this.templateBuildPlan.getBuildPlan().getTemplateBuildPlans()) {
			if (template.getNodeTemplate() == null) {
				list.add(template.getRelationshipTemplate());
			}
		}
		return list;
	}

	/**
	 * Adds Property with its Type to the BuildPlan WSDL
	 *
	 * @param propertyName
	 *            the name of the Property
	 * @param propertyType
	 *            the XSD Type of the Property
	 * @return a QName to be used for References
	 */
	public QName addProperty(String propertyName, QName propertyType) {
		QName importedQName = this.importNamespace(propertyType);
		this.templateBuildPlan.getBuildPlan().getWsdl().addProperty(propertyName, importedQName);
		return importedQName;
	}

	/**
	 * Adds a Property Alias for the given Property into the BuildPlan WSDL
	 *
	 * @param propertyName
	 *            the name of the property
	 * @param messageType
	 *            the type of the Message to make an Alias for
	 * @param partName
	 *            the part name of the Message
	 * @param query
	 *            the query to the Element inside the Message
	 * @return true if adding property alias was successful, else false
	 */
	public boolean addPropertyAlias(String propertyName, QName messageType, String partName, String query) {
		QName importedQName = this.importNamespace(messageType);
		return this.templateBuildPlan.getBuildPlan().getWsdl().addPropertyAlias(propertyName, partName, importedQName,
				query);
	}

	/**
	 * Adds a correlationSet with the specified property
	 *
	 * @param correlationSetName
	 *            the name for the correlationSet
	 * @param propertyName
	 *            the property to use inside the correlationSet
	 * @return true if adding the correlation set was successful, else false
	 */
	public boolean addCorrelationSet(String correlationSetName, String propertyName) {
		return this.bpelTemplateHandler.addCorrelationSet(correlationSetName, propertyName, this.templateBuildPlan);
	}

	/**
	 * Returns the TOSCA BaseType of the given RelationshipTemplate
	 *
	 * @param template
	 *            an AbstractRelationshipTemplate
	 * @return a QName representing the BaseType of the given Template
	 */
	public QName getBaseType(AbstractRelationshipTemplate template) {
		return Utils.getRelationshipBaseType(template);
	}

	public QName getServiceTemplateId() {
		return this.serviceTemplateId;
	}

	public String getTemplateId() {
		if (this.getNodeTemplate() != null) {
			return this.getNodeTemplate().getId();
		} else {
			return this.getRelationshipTemplate().getId();
		}

	}

	public Variable generateVariableWithRandomValue() {
		String varName = "randomVar" + this.getIdForNames();
		boolean check = this.buildPlanHandler.addPropertyVariable(varName, this.templateBuildPlan.getBuildPlan());
		check &= this.buildPlanHandler.initializePropertyVariable(varName, String.valueOf(System.currentTimeMillis()),
				this.templateBuildPlan.getBuildPlan());
		if (check) {
			return new Variable(this.getTemplateId(), "prop_" + varName);
		} else {
			return null;

		}

	}

	/**
	 * Generates a bpel string variable with the given name + "_" +
	 * randomPositiveInt.
	 *
	 * @param variableName
	 *            String containing a name
	 * @param initVal
	 *            the value for the variable, if null the value will be empty
	 * @return a TemplatePropWrapper containing the generated Id for the
	 *         variable
	 */
	public Variable createGlobalStringVariable(String variableName, String initVal) {
		String varName = variableName + "_" + this.getIdForNames();
		boolean check = this.buildPlanHandler.addPropertyVariable(varName, this.templateBuildPlan.getBuildPlan());
		check &= this.buildPlanHandler.initializePropertyVariable(varName, initVal == null ? "" : initVal,
				this.templateBuildPlan.getBuildPlan());
		if (check) {
			return new Variable(this.getTemplateId(), "prop_" + varName);
		} else {
			return null;
		}
	}

	/**
	 * Executes the operation of the given NodeTemplate
	 * 
	 * @param nodeTemplate
	 *            the NodeTemplate the operation belongs to
	 * @param operationName
	 *            the name of the operation to execute
	 * @param param2propertyMapping
	 *            If a Map of Parameter to Variable is given this will be used
	 *            for the operation call
	 * @return true if appending logic to execute the operation at runtime was
	 *         successfull
	 */
	public boolean executeOperation(AbstractNodeTemplate nodeTemplate, String interfaceName, String operationName,
			Map<AbstractParameter, Variable> param2propertyMapping) {

		ProvisioningChain chain = TemplatePlanBuilder.createProvisioningCall(nodeTemplate, interfaceName,
				operationName);
		if (chain == null) {
			return false;
		}

		List<String> opNames = new ArrayList<String>();
		opNames.add(operationName);

		/*
		 * create a new templatePlanContext that combines the requested
		 * nodeTemplate and the scope of this context
		 */
		// backup nodes
		AbstractRelationshipTemplate relationBackup = this.templateBuildPlan.getRelationshipTemplate();
		AbstractNodeTemplate nodeBackup = this.templateBuildPlan.getNodeTemplate();

		// create context from this context and set the given nodeTemplate as
		// the node for the scope
		TemplatePlanContext context = new TemplatePlanContext(this.templateBuildPlan, this.propertyMap,
				this.serviceTemplateId);

		context.templateBuildPlan.setNodeTemplate(nodeTemplate);
		context.templateBuildPlan.setRelationshipTemplate(null);

		/*
		 * chain.executeIAProvisioning(context);
		 * chain.executeDAProvisioning(context);
		 */
		if (param2propertyMapping == null) {
			chain.executeOperationProvisioning(context, opNames);
		} else {
			chain.executeOperationProvisioning(context, opNames, param2propertyMapping);
		}

		// re-set the orginal configuration of the templateBuildPlan
		this.templateBuildPlan.setNodeTemplate(nodeBackup);
		this.templateBuildPlan.setRelationshipTemplate(relationBackup);

		return true;
	}

	/**
	 * Appends the given node the the main sequence of the buildPlan this
	 * context belongs to
	 * 
	 * @param node
	 *            a XML DOM Node
	 * @return true if adding the node to the main sequence was successfull
	 */
	public boolean appendToInitSequence(Node node) {
		Node importedNode = this.importNode(node);

		Element flowElement = this.templateBuildPlan.getBuildPlan().getBpelMainFlowElement();

		Node mainSequenceNode = flowElement.getParentNode();

		mainSequenceNode.insertBefore(importedNode, flowElement);

		return true;
	}

	/**
	 * Returns the names of the global variables defined in the buildPlan this
	 * context belongs to
	 * 
	 * @return a List of Strings representing the global variable names
	 */
	public List<String> getMainVariableNames() {
		return this.bpelProcessHandler.getMainVariableNames(this.templateBuildPlan.getBuildPlan());
	}

	/**
	 * Returns the localNames defined inside the input message of the buildPlan
	 * this context belongs to
	 * 
	 * @return a List of Strings representing the XML localNames of the elements
	 *         inside the input message of the buildPlan this context belongs to
	 */
	public List<String> getInputMessageElementNames() {
		return this.templateBuildPlan.getBuildPlan().getWsdl().getInputMessageLocalNames();
	}
}
