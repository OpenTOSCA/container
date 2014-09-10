package org.opentosca.toscaengine.service.impl.toscareferencemapping;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.consolidatedtosca.ConsolidatedPolicies;
import org.opentosca.model.consolidatedtosca.ConsolidatedPolicy;
import org.opentosca.model.consolidatedtosca.PublicPlan;
import org.opentosca.model.consolidatedtosca.PublicPlanTypes;
import org.opentosca.model.tosca.TDefinitions;
import org.opentosca.model.tosca.TExportedInterface;
import org.opentosca.model.tosca.referencemapping.CSARIDToDefinitionsMap;
import org.opentosca.model.tosca.referencemapping.CSARIDToServiceTemplateIDsMap;
import org.opentosca.model.tosca.referencemapping.CsarIDToConsolidatedPolicies;
import org.opentosca.model.tosca.referencemapping.CsarIDToPlanTypeToIntegerToPublicPlan;
import org.opentosca.model.tosca.referencemapping.CsarIDToServiceTemplateIDToPlanID;
import org.opentosca.model.tosca.referencemapping.CsarIDToWSDLDocuments;
import org.opentosca.model.tosca.referencemapping.DocumentMap;
import org.opentosca.model.tosca.referencemapping.MapQNameNode;
import org.opentosca.model.tosca.referencemapping.ReferenceMap;
import org.opentosca.toscaengine.service.IToscaReferenceMapper;
import org.opentosca.toscaengine.service.impl.servicehandler.ServiceHandler;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The ToscaReferenceMapper provides the functionality of a library for DOM
 * Nodes. These Nodes are referenced informations of TOSCA files. They are
 * stored as a pair of a QName and the Node itself. The QName is the one with
 * which the Node is referenced inside of a TOSCA document. But not only TOSCA
 * elements are stored as Node, but also elements of XML Schema or WSDL and so
 * on. Nodes of TOSCA can be retrieved as JAXB objects aswell.
 * 
 * @au Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class ToscaReferenceMapper implements IToscaReferenceMapper {
	
	// services
	private static IXMLSerializerService xmlSerializerService;
	
	// logger
	private Logger LOG = LoggerFactory.getLogger(ToscaReferenceMapper.class);
	
	// internal data structures
	private ReferenceMap referenceMap;
	private DocumentMap documentMap;
	private CSARIDToDefinitionsMap mapCSARIDToDefinitions;
	private CSARIDToServiceTemplateIDsMap mapCSARIDToServiceTemplateIDs;
	private CsarIDToPlanTypeToIntegerToPublicPlan csarIDToPlanTypeToIntegerToPublicPlan;
	private CsarIDToWSDLDocuments csarIDToWSDLDocuments;
	private CsarIDToServiceTemplateIDToPlanID csarIDToServiceTemplateIDToPlanID;
	private Map<CSARID, Map<QName, Boolean>> csarIDToPlanIDToSynchronousBoolean = new HashMap<CSARID, Map<QName, Boolean>>();
	private Map<CSARID, Map<QName, List<TExportedInterface>>> csarIDToExportedInterface = new HashMap<CSARID, Map<QName, List<TExportedInterface>>>();
	private CsarIDToConsolidatedPolicies csarIDToConsolidatedPolicies = new CsarIDToConsolidatedPolicies();
	private Map<CSARID, Map<QName, String>> mapDefinitionsIDToLocationString = new HashMap<CSARID, Map<QName, String>>();
	private Map<CSARID, Map<QName, QName>> mapElementIDToDefinitionsID = new HashMap<CSARID, Map<QName, QName>>();
	
	
	public ToscaReferenceMapper() {
		this.setup();
	}
	
	/**
	 * This function deletes all stored references of a certain CSAR.
	 * 
	 * @param csarID
	 * @return true for success, false for an error
	 */
	public boolean clearCSARContent(CSARID csarID) {
		
		this.LOG.debug("Delete the content of \"" + csarID + "\".");
		
		this.setup();
		this.referenceMap.remove(csarID);
		this.documentMap.remove(csarID);
		this.mapCSARIDToDefinitions.remove(csarID);
		this.mapCSARIDToServiceTemplateIDs.remove(csarID);
		this.csarIDToPlanTypeToIntegerToPublicPlan.remove(csarID);
		this.csarIDToWSDLDocuments.remove(csarID);
		this.csarIDToServiceTemplateIDToPlanID.remove(csarID);
		this.csarIDToPlanIDToSynchronousBoolean.remove(csarID);
		this.csarIDToExportedInterface.remove(csarID);
		this.csarIDToConsolidatedPolicies.remove(csarID);
		this.mapDefinitionsIDToLocationString.remove(csarID);
		this.mapElementIDToDefinitionsID.remove(csarID);
		
		if (this.containsCSARData(csarID)) {
			return false;
		}
		return true;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConsolidatedPolicies getConsolidatedPolicies(CSARID csarID, QName templateID) {
		return this.csarIDToConsolidatedPolicies.get(csarID, templateID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeConsolidatedPolicies(CSARID csarID, QName templateID, ConsolidatedPolicies policies) {
		this.csarIDToConsolidatedPolicies.put(csarID, templateID, policies);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsCSARData(CSARID csarID) {
		this.setup();
		
		boolean found = false;
		
		if (this.referenceMap.containsKey(csarID)) {
			this.LOG.trace("Inside of the referenceMap are informations stored");
			found = true;
		}
		if (this.documentMap.containsKey(csarID)) {
			this.LOG.trace("Inside of the documentMap are informations stored");
			found = true;
		}
		if (this.mapCSARIDToDefinitions.containsKey(csarID)) {
			this.LOG.trace("Inside of the mapCSARIDToDefinitions are informations stored");
			found = true;
		}
		if (this.mapCSARIDToServiceTemplateIDs.containsKey(csarID)) {
			this.LOG.trace("Inside of the mapCSARIDToServiceTemplateIDs are informations stored");
			found = true;
		}
		if (this.csarIDToPlanTypeToIntegerToPublicPlan.containsKey(csarID)) {
			this.LOG.trace("Inside of the csarIDToPlanTypeToIntegerToPublicPlan are informations stored");
			found = true;
		}
		if (this.csarIDToWSDLDocuments.containsKey(csarID)) {
			this.LOG.trace("Inside of the csarIDToWSDLDocuments are informations stored");
			found = true;
		}
		if (this.csarIDToServiceTemplateIDToPlanID.containsKey(csarID)) {
			this.LOG.trace("Inside of the csarIDToServiceTemplateIDToPlanID are informations stored");
			found = true;
		}
		if (this.csarIDToPlanIDToSynchronousBoolean.containsKey(csarID)) {
			this.LOG.trace("Inside of the csarIDToPlanIDToSynchronousBoolean are informations stored");
			found = true;
		}
		if (this.csarIDToExportedInterface.containsKey(csarID)) {
			this.LOG.trace("Inside of the csarIDToExportedInterface are informations stored");
			found = true;
		}
		if (this.csarIDToConsolidatedPolicies.containsKey(csarID)) {
			this.LOG.trace("Inside of the csarIDToConsolidatedPolicies are informations stored");
			found = true;
		}
		return found;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsReferenceInsideCSAR(CSARID csarID, QName reference) {
		this.setup();
		if (this.containsCSARData(csarID)) {
			if (this.referenceMap.get(csarID).containsKey(reference)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>> getCSARIDToPublicPlans(CSARID csarID) {
		
		if (null == this.csarIDToPlanTypeToIntegerToPublicPlan) {
			this.LOG.error("The variable is null.");
		}
		if (!this.csarIDToPlanTypeToIntegerToPublicPlan.containsKey(csarID)) {
			this.csarIDToPlanTypeToIntegerToPublicPlan.put(csarID, new HashMap<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>>());
		}
		if (!this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).containsKey(PublicPlanTypes.BUILD)) {
			this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).put(PublicPlanTypes.BUILD, new LinkedHashMap<Integer, PublicPlan>());
			this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).put(PublicPlanTypes.TERMINATION, new LinkedHashMap<Integer, PublicPlan>());
			this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).put(PublicPlanTypes.OTHERMANAGEMENT, new LinkedHashMap<Integer, PublicPlan>());
		}
		
		return this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QName> getDefinitionIDsOfCSAR(CSARID csarID) {
		this.setup();
		
		List<QName> listOfIDs = new ArrayList<QName>();
		
		if (this.mapCSARIDToDefinitions.containsKey(csarID)) {
			
			for (TDefinitions def : this.mapCSARIDToDefinitions.get(csarID)) {
				
				listOfIDs.add(new QName(def.getTargetNamespace(), def.getId()));
				
			}
			
		} else {
			this.LOG.error("There are no Definitions stored for the CSAR \"" + csarID + "\".");
		}
		
		return listOfIDs;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TDefinitions> getDefinitionsOfCSAR(CSARID csarID) {
		this.setup();
		if (this.mapCSARIDToDefinitions.containsKey(csarID)) {
			return this.mapCSARIDToDefinitions.get(csarID);
		} else {
			this.LOG.error("There are no Definitions stored for the CSAR \"" + csarID + "\".");
		}
		
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public Document getDOMDocumentForReference(CSARID csarID, QName reference) {
		this.setup();
		if (this.documentMap.containsKey(csarID)) {
			
			// The passed ID of a CSAR is found.
			Map<QName, Document> referenceToDocumentForSpecificCSAR = this.documentMap.get(csarID);
			if (referenceToDocumentForSpecificCSAR.containsKey(reference)) {
				// The passed reference is found.
				return referenceToDocumentForSpecificCSAR.get(reference);
			} else {
				this.LOG.error("No stored reference for CSAR \"" + csarID + "\" and \"" + reference + "\" found.");
			}
		} else {
			this.LOG.error("No stored document references for CSAR \"" + csarID + "\" found.");
		}
		
		// nothing found
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<QName, List<TExportedInterface>> getExportedInterfacesOfCSAR(CSARID csarID) {
		if (this.csarIDToExportedInterface.containsKey(csarID)) {
			return this.csarIDToExportedInterface.get(csarID);
		} else {
			return new HashMap<QName, List<TExportedInterface>>();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getReferenceAsNode(CSARID csarID, QName nodeID) {
		this.setup();
		
		this.LOG.debug("Lookup for the node \"" + nodeID + "\" inside of the CSAR \"" + csarID + "\".");
		
		if (this.referenceMap.containsKey(csarID)) {
			
			// The passed ID of a CSAR is found.
			// this.LOG.info("References for the CSAR with the QName \"" +
			// csarID.toString() + "\" found.");
			if (this.referenceMap.get(csarID).containsKey(nodeID)) {
				
				// The passed reference is found.
				// this.LOG.info("Reference with the QName \"" +
				// nodeID.toString() + "\" found.");
				return this.referenceMap.get(csarID).get(nodeID);
			}
		}
		
		this.LOG.error("There is no Node stored for CSAR \"" + csarID + "\" and reference \"" + nodeID + "\".");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getJAXBReference(CSARID csarID, QName nodeID) {
		this.setup();
		
		this.LOG.debug("Lookup for the node \"" + nodeID + "\" inside of the CSAR \"" + csarID + "\".");
		
		if (this.referenceMap.containsKey(csarID)) {
			
			// The passed ID of a CSAR is found.
			// this.LOG.info("References for the CSAR with the QName \"" +
			// csarID.toString() + "\" found.");
			if (this.referenceMap.get(csarID).containsKey(nodeID)) {
				
				// The passed reference is found.
				// this.LOG.info("Reference with the QName \"" +
				// nodeID.toString() + "\" found.");
				Node node = this.referenceMap.get(csarID).get(nodeID);
				
				if (AvailableToscaElements.getElementName(node.getLocalName()).getElementClass() != null) {
					// The name of the node implies that is marshalable into one
					// of the JAXB classes of TOSCA.
					return ToscaReferenceMapper.xmlSerializerService.getXmlSerializer().unmarshal(node, AvailableToscaElements.getElementName(node.getLocalName()).getElementClass());
				} else {
					this.LOG.error("The reference is not a JAXB element.");
				}
				
			} else {
				this.LOG.error("Reference with the QName \"" + nodeID.toString() + "\" was not found for the CSAR \"" + csarID + "\".");
			}
		} else {
			this.LOG.error("No references for the CSAR with the QName \"" + csarID.toString() + "\" found.");
		}
		
		return null;
	}
	
	public List<Document> getListOfWSDLForCSAR(CSARID csarID) {
		if (this.csarIDToWSDLDocuments.containsKey(csarID)) {
			return this.csarIDToWSDLDocuments.get(csarID);
		}
		return new ArrayList<Document>();
	}
	
	@Override
	public Map<CSARID, Map<QName, List<QName>>> getMapCsarIDToServiceTemplateIDToPlanID() {
		return this.csarIDToServiceTemplateIDToPlanID;
	}
	
	/**
	 * Returns a PublicPlan if found.
	 * 
	 * @param csarID
	 * @param planID
	 * @return the PublicPlan if found, null instead.
	 */
	@Override
	public PublicPlan getPublicPlan(CSARID csarID, QName planID) {
		
		if (!this.csarIDToPlanTypeToIntegerToPublicPlan.containsKey(csarID)) {
			this.csarIDToPlanTypeToIntegerToPublicPlan.put(csarID, new HashMap<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>>());
		}
		if (!this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).containsKey(PublicPlanTypes.BUILD)) {
			this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).put(PublicPlanTypes.BUILD, new LinkedHashMap<Integer, PublicPlan>());
			this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).put(PublicPlanTypes.TERMINATION, new LinkedHashMap<Integer, PublicPlan>());
			this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).put(PublicPlanTypes.OTHERMANAGEMENT, new LinkedHashMap<Integer, PublicPlan>());
		}
		
		for (PublicPlanTypes type : this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).keySet()) {
			for (Integer itr : this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).get(type).keySet()) {
				PublicPlan plan = this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).get(type).get(itr);
				if (plan.getPlanID().equals(planID)) {
					return plan;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QName> getServiceTemplateIDsContainedInCSAR(CSARID csarID) {
		this.setup();
		return this.mapCSARIDToServiceTemplateIDs.get(csarID);
	}
	
	@Override
	public Boolean isPlanAsynchronous(CSARID csarID, QName planID) {
		if ((null == this.csarIDToPlanIDToSynchronousBoolean.get(csarID)) || (null == this.csarIDToPlanIDToSynchronousBoolean.get(csarID).get(planID))) {
			this.LOG.error("There is no information stored about the plan " + planID + " of CSAR " + csarID + " is synchronous or asynchronous. Thus return null.");
			return null;
		} else {
			return this.csarIDToPlanIDToSynchronousBoolean.get(csarID).get(planID);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printStoredData() {
		this.setup();
		String string = "";
		String ls = System.getProperty("line.separator");
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("Debug output of the stored data of the TOSCA resolving." + ls);
		
		if ((null == this.referenceMap.keySet()) || (this.referenceMap.keySet().size() == 0)) {
			builder.append("No data about CSARs stored yet.");
			this.LOG.debug(builder.toString());
			return;
		}
		
		for (CSARID csarID : this.referenceMap.keySet()) {
			
			builder.append(ls + "Print all stored references of \"" + csarID + "\"." + ls);
			for (QName ref : this.referenceMap.get(csarID).keySet()) {
				
				if (this.referenceMap.get(csarID).get(ref) == null) {
					builder.append("ERROR: There is no data stored for the reference \"" + ref + "\"." + ls);
				} else {
					string = ToscaReferenceMapper.xmlSerializerService.getXmlSerializer().docToString(this.referenceMap.get(csarID).get(ref), true);
					string = string.replace(ls, "");
					builder.append("       " + ref + " --> " + string + ls);
				}
			}
			
			if (this.documentMap.containsKey(csarID)) {
				builder.append(ls + "Print all stored documents of \"" + csarID + "\"." + ls);
				for (QName ref : this.documentMap.get(csarID).keySet()) {
					
					if (this.documentMap.get(csarID).get(ref) == null) {
						builder.append("ERROR: There is no data stored for the reference \"" + ref + "\"." + ls);
					} else {
						string = ToscaReferenceMapper.xmlSerializerService.getXmlSerializer().docToString(this.documentMap.get(csarID).get(ref), true);
						string = string.replace(ls, "");
						builder.append("       " + ref + " --> " + string + ls);
					}
				}
			} else {
				builder.append("ERROR: There is no document stored for \"" + csarID + "\"." + ls);
			}
			
			builder.append(ls + "Print all due the BoundaryDefinitions defined PublicPlans" + ls);
			for (PublicPlanTypes type : this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).keySet()) {
				builder.append("   type: " + type + ls);
				for (Integer planID : this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).get(type).keySet()) {
					PublicPlan pp = this.csarIDToPlanTypeToIntegerToPublicPlan.get(csarID).get(type).get(planID);
					builder.append("      id: " + planID + " PublicPlan QName: " + pp.getPlanID() + " and internal ID " + pp.getInternalPlanID() + ls);
				}
			}
			
			builder.append(ls + "Print all stored plan IDs of this CSAR:" + ls);
			if (null != this.csarIDToServiceTemplateIDToPlanID.get(csarID)) {
				for (QName serviceTemplateID : this.csarIDToServiceTemplateIDToPlanID.get(csarID).keySet()) {
					for (QName planID : this.csarIDToServiceTemplateIDToPlanID.get(csarID).get(serviceTemplateID)) {
						builder.append("       Plan \"" + planID + "\" is inside of ServiceTemplate \"" + serviceTemplateID + "\"" + ls);
					}
				}
			} else {
				builder.append("       nothing found ..." + ls);
			}
			
			builder.append(ls + "Print all stored informations about synchronous (false) and asynchronous (true) plans of CSAR \"" + csarID + "\":" + ls);
			if (null != this.csarIDToPlanIDToSynchronousBoolean.get(csarID)) {
				for (QName planID : this.csarIDToPlanIDToSynchronousBoolean.get(csarID).keySet()) {
					builder.append("    Plan \"" + planID + "\" is asynchronous? " + this.csarIDToPlanIDToSynchronousBoolean.get(csarID).get(planID) + ls);
				}
			}
			
			JAXBContext context;
			Marshaller marshaller = null;
			StringWriter writer = new StringWriter();
			try {
				context = JAXBContext.newInstance("org.opentosca.model.consolidatedtosca");
				marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
			
			builder.append(ls + "Print list of the mapping of a TemplateID to Consolidated Policies" + ls);
			for (QName templateID : this.csarIDToConsolidatedPolicies.getTemplateIDs(csarID)) {
				ConsolidatedPolicies pols = this.csarIDToConsolidatedPolicies.get(csarID, templateID);
				builder.append("   " + templateID + " mapps to following policies." + ls);
				for (ConsolidatedPolicy pol : pols.getConsolidatedPolicy()) {
					builder.append("      policy name=\"" + pol.getName() + "\"");
					try {
						marshaller.marshal(pol, writer);
						builder.append(" --> " + writer.toString().replaceAll("\\n|\\r", ""));
					} catch (JAXBException e) {
						e.printStackTrace();
					}
					builder.append(ls);
					// builder.append("      name=\"" + pol.getName() +
					// "\" type=\"" + pol.getType() + "\" language=\"" +
					// pol.getPolicyLanguage() + "\"" + ls);
					// builder.append("         properties: " +
					// xmlSerializerService.getXmlSerializer().docToString((Node)
					// pol.getProperties().getAny(), true) + ls);
					// builder.append("         specific content: todo: serialize to string"
					// + ls);
				}
			}
			
			if (this.mapDefinitionsIDToLocationString.containsKey(csarID)) {
				builder.append(ls + "Print map of TOSCA Definitions locations." + ls);
				for (QName defID : this.mapDefinitionsIDToLocationString.get(csarID).keySet()) {
					builder.append("   " + defID + " is stored at \"" + this.mapDefinitionsIDToLocationString.get(csarID).get(defID).replace("\\", "/") + "\"" + ls);
				}
			}
			
			if (this.mapElementIDToDefinitionsID.containsKey(csarID)) {
				builder.append(ls + "Print map of TOSCA element IDs to Definitions ID." + ls);
				for (QName eleID : this.mapElementIDToDefinitionsID.get(csarID).keySet()) {
					builder.append("   " + eleID + " is contained in Definitions \"" + this.mapElementIDToDefinitionsID.get(csarID).get(eleID) + "\"" + ls);
				}
			}
			
		}
		
		this.LOG.debug(builder.toString());
		
	}
	
	/**
	 * This method initializes the data structures in which the the DOM Nodes
	 * and Documents are stored if not done already.
	 */
	private void setup() {
		if (this.referenceMap == null) {
			this.referenceMap = new ReferenceMap();
		}
		if (this.documentMap == null) {
			this.documentMap = new DocumentMap();
		}
		if (this.mapCSARIDToDefinitions == null) {
			this.mapCSARIDToDefinitions = new CSARIDToDefinitionsMap();
		}
		if (this.mapCSARIDToServiceTemplateIDs == null) {
			this.mapCSARIDToServiceTemplateIDs = new CSARIDToServiceTemplateIDsMap();
		}
		if (null == this.csarIDToPlanTypeToIntegerToPublicPlan) {
			this.csarIDToPlanTypeToIntegerToPublicPlan = new CsarIDToPlanTypeToIntegerToPublicPlan();
		}
		if (null == this.csarIDToWSDLDocuments) {
			this.csarIDToWSDLDocuments = new CsarIDToWSDLDocuments();
		}
		if (null == this.csarIDToServiceTemplateIDToPlanID) {
			this.csarIDToServiceTemplateIDToPlanID = new CsarIDToServiceTemplateIDToPlanID();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeDefinitions(CSARID csarID, TDefinitions definitions) {
		
		this.setup();
		if ((csarID != null) && (definitions != null)) {
			
			QName reference = new QName(definitions.getTargetNamespace(), definitions.getId());
			this.LOG.debug("Store the Definitions \"" + reference + "\".");
			
			// store it in the Definitions map
			if (!this.mapCSARIDToDefinitions.containsKey(csarID)) {
				this.mapCSARIDToDefinitions.put(csarID, new ArrayList<TDefinitions>());
			}
			this.mapCSARIDToDefinitions.get(csarID).add(definitions);
			// this.mapCSARIDToDefinitions.save(); // Persist definitions.
			// TDefinitions + subclasses need to be serializable
			
			// store it in the references map
			if (!this.referenceMap.containsKey(csarID)) {
				this.referenceMap.put(csarID, new MapQNameNode());
			}
			this.referenceMap.get(csarID).put(reference, ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(definitions));
			
		} else {
			this.LOG.error("An error has occured.");
		}
		
	}
	
	/**
	 * This method stores a DOM document and its QName for a certain CSAR.
	 * 
	 * @param csarID ID of the CSAR in which the document is referenced.
	 * @param documentID ID of the document.
	 * @param doc DOM document which shall be stored.
	 * @return true means no error, false means one or more errors
	 */
	@Override
	public void storeDocument(CSARID csarID, QName documentID, Document doc) {
		
		this.setup();
		
		if (csarID == null) {
			this.LOG.error("The CSARID is null!");
			return;
		}
		if (documentID == null) {
			this.LOG.error("The document ID is null!");
			return;
		}
		if (doc == null) {
			this.LOG.error("The document is null!");
			return;
		}
		
		this.LOG.debug("Store new document reference for CSAR \"" + csarID + "\" the reference \"" + documentID + "\".");
		
		if (!this.documentMap.containsKey(csarID)) {
			this.documentMap.put(csarID, new HashMap<QName, Document>());
		}
		
		if (this.documentMap.get(csarID).containsKey(documentID)) {
			this.LOG.debug("The reference with the QName \"" + documentID.toString() + "\" is already stored for the CSAR \"" + csarID + "\".");
		} else {
			this.documentMap.get(csarID).put(documentID, doc);
			this.LOG.debug("Storing of Document \"" + documentID.toString() + "\" completed.");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeExportedInterface(CSARID csarID, QName serviceTemplateID, TExportedInterface iface) {
		if (!this.csarIDToExportedInterface.containsKey(csarID)) {
			this.csarIDToExportedInterface.put(csarID, new HashMap<QName, List<TExportedInterface>>());
		}
		if (!this.csarIDToExportedInterface.get(csarID).containsKey(serviceTemplateID)) {
			this.csarIDToExportedInterface.get(csarID).put(serviceTemplateID, new ArrayList<TExportedInterface>());
		}
		this.csarIDToExportedInterface.get(csarID).get(serviceTemplateID).add(iface);
	}
	
	/**
	 * Converts to DOM Document and stores a list of WSDL files for a certain
	 * CSAR.
	 * 
	 * @param csarID
	 * @param listOfWSDL
	 * @return true for success, false for error
	 */
	public boolean storeListOfWSDLForCSAR(CSARID csarID, List<Document> listOfWSDL) {
		
		if (!this.csarIDToWSDLDocuments.containsKey(csarID)) {
			this.csarIDToWSDLDocuments.put(csarID, new ArrayList<Document>());
		}
		
		for (Document doc : listOfWSDL) {
			this.csarIDToWSDLDocuments.get(csarID).add(doc);
		}
		
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storePlanAsynchronousBoolean(CSARID csarID, QName planID, boolean checkAsynchronous) {
		if (null == this.csarIDToPlanIDToSynchronousBoolean.get(csarID)) {
			this.csarIDToPlanIDToSynchronousBoolean.put(csarID, new HashMap<QName, Boolean>());
		}
		if (null == this.csarIDToPlanIDToSynchronousBoolean.get(csarID).get(planID)) {
			this.csarIDToPlanIDToSynchronousBoolean.get(csarID).put(planID, checkAsynchronous);
		} else {
			this.LOG.error("For the CSAR " + csarID + " and plan " + planID + " is already stored wheter it is a synchronous or an asynchronous plan.");
		}
	}
	
	public void storePlanIDForCSARAndServiceTemplate(CSARID csarID, QName serviceTemplateID, QName planID) {
		if (!this.csarIDToServiceTemplateIDToPlanID.containsKey(csarID)) {
			this.csarIDToServiceTemplateIDToPlanID.put(csarID, new HashMap<QName, List<QName>>());
		}
		if (!this.csarIDToServiceTemplateIDToPlanID.get(csarID).containsKey(serviceTemplateID)) {
			this.csarIDToServiceTemplateIDToPlanID.get(csarID).put(serviceTemplateID, new ArrayList<QName>());
		}
		this.csarIDToServiceTemplateIDToPlanID.get(csarID).get(serviceTemplateID).add(planID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeReference(CSARID csarID, QName nodeID, Node node) {
		this.setup();
		
		MapQNameNode csarMap;
		
		if (this.referenceMap.containsKey(csarID)) {
			// CSARID is known
			csarMap = this.referenceMap.get(csarID);
			if (csarMap.containsKey(nodeID)) {
				// node is stored already
				this.LOG.debug("The reference with the QName \"" + nodeID.toString() + "\" is already stored for the CSAR \"" + csarID + "\".");
			} else {
				// store this node
				csarMap.put(nodeID, node);
				this.LOG.debug("Storing of Node \"" + nodeID.toString() + "\" completed.");
			}
			
		} else {
			// CSARID is not known, so store a new HashMap for this ID
			csarMap = new MapQNameNode();
			csarMap.put(nodeID, node);
			this.referenceMap.put(csarID, csarMap);
			this.LOG.debug("Storing of \"" + nodeID.toString() + "\" completed.");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeServiceTemplateIDForCSARID(QName serviceTemplateID, CSARID csarID) {
		this.setup();
		if ((serviceTemplateID != null) && (csarID != null)) {
			if (!this.mapCSARIDToServiceTemplateIDs.containsKey(csarID)) {
				this.mapCSARIDToServiceTemplateIDs.put(csarID, new ArrayList<QName>());
			}
			this.mapCSARIDToServiceTemplateIDs.get(csarID).add(serviceTemplateID);
		} else {
			this.LOG.error("An error has occured.");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeDefinitionsLocation(CSARID csarID, QName defID, String location) {
		if (!this.mapDefinitionsIDToLocationString.containsKey(csarID)) {
			this.mapDefinitionsIDToLocationString.put(csarID, new HashMap<QName, String>());
		}
		if (this.mapDefinitionsIDToLocationString.get(csarID).containsKey(defID)) {
			this.LOG.warn("Overwrite the location for the Definitions \"" + defID + "\" in the CSAR \"" + csarID + "\".");
		}
		this.mapDefinitionsIDToLocationString.get(csarID).put(defID, location);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDefinitionsLocation(CSARID csarID, QName defID) {
		if (this.mapDefinitionsIDToLocationString.containsKey(csarID)) {
			if (this.mapDefinitionsIDToLocationString.get(csarID).containsKey(defID)) {
				return this.mapDefinitionsIDToLocationString.get(csarID).get(defID);
			}
		}
		this.LOG.error("No location found for the Definitions \"" + defID + "\" in CSAR \"" + csarID + "\".");
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeContainingDefinitionsID(CSARID csarID, QName elementID, QName definitionsID) {
		if (!this.mapElementIDToDefinitionsID.containsKey(csarID)) {
			this.mapElementIDToDefinitionsID.put(csarID, new HashMap<QName, QName>());
		}
		if (this.mapElementIDToDefinitionsID.get(csarID).containsKey(elementID)) {
			this.LOG.warn("Overwrite the mapping for the element \"" + elementID + "\" in the CSAR \"" + csarID + "\".");
		}
		this.mapElementIDToDefinitionsID.get(csarID).put(elementID, definitionsID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public QName getContainingDefinitionsID(CSARID csarID, QName elementID) {
		if (this.mapElementIDToDefinitionsID.containsKey(csarID)) {
			if (this.mapElementIDToDefinitionsID.get(csarID).containsKey(elementID)) {
				return this.mapElementIDToDefinitionsID.get(csarID).get(elementID);
			}
		}
		this.LOG.error("No Definitions ID found for the element \"" + elementID + "\" in CSAR \"" + csarID + "\".");
		return null;
	}
	
	protected void bindIXMLSerializerService(IXMLSerializerService service) {
		if (service == null) {
			this.LOG.error("Service IXMLSerializerService is null.");
		} else {
			this.LOG.debug("Bind of the IXMLSerializerService.");
			ToscaReferenceMapper.xmlSerializerService = service;
		}
	}
	
	protected void unbindIXMLSerializerService(IXMLSerializerService service) {
		this.LOG.debug("Unbind of the IXMLSerializerService.");
		ToscaReferenceMapper.xmlSerializerService = null;
	}
}
