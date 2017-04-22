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
import org.opentosca.model.tosca.TBoundaryDefinitions.Policies;
import org.opentosca.model.tosca.TBoundaryDefinitions.Properties.PropertyMappings;
import org.opentosca.model.tosca.TDefinitions;
import org.opentosca.model.tosca.TExportedInterface;
import org.opentosca.model.tosca.TExportedOperation;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.TPolicy;
import org.opentosca.model.tosca.extension.helpers.PlanTypes;
import org.opentosca.model.tosca.referencemapping.CSARIDToDefinitionsMap;
import org.opentosca.model.tosca.referencemapping.CSARIDToServiceTemplateIDsMap;
import org.opentosca.model.tosca.referencemapping.CsarIDToPlanTypeToPlanNameToPlan;
import org.opentosca.model.tosca.referencemapping.CsarIDToPolicies;
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
	private CsarIDToPlanTypeToPlanNameToPlan csarIDToPlanTypeToIntegerToPlan;
	private CsarIDToWSDLDocuments csarIDToWSDLDocuments;
	private CsarIDToServiceTemplateIDToPlanID csarIDToServiceTemplateIDToPlanID;
	private Map<CSARID, Map<QName, Boolean>> csarIDToPlanIDToSynchronousBoolean = new HashMap<CSARID, Map<QName, Boolean>>();
	private Map<CSARID, Map<QName, List<TExportedInterface>>> csarIDToExportedInterface = new HashMap<CSARID, Map<QName, List<TExportedInterface>>>();
	private CsarIDToPolicies csarIDToPolicies = new CsarIDToPolicies();
	private Map<CSARID, Map<QName, String>> mapDefinitionsIDToLocationString = new HashMap<CSARID, Map<QName, String>>();
	private Map<CSARID, Map<QName, QName>> mapElementIDToDefinitionsID = new HashMap<CSARID, Map<QName, QName>>();
	private Map<CSARID, Map<QName, QName>> mapCSARIDToPlanIDToInputMessageID = new HashMap<CSARID, Map<QName, QName>>();

	// private Map<CSARID, Map<QName, Map<QName, String>>>
	// mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName = new HashMap<CSARID,
	// Map<QName, Map<QName, String>>>();
	// private Map<CSARID, Map<QName, Map<QName, String>>>
	// mapCSARIDToServiceTemplateIdToPlanIDToOperationName = new HashMap<CSARID,
	// Map<QName, Map<QName, String>>>();
	private Map<CSARID, Map<QName, Map<String, Map<String, QName>>>> mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan = new HashMap<CSARID, Map<QName, Map<String, Map<String, QName>>>>();

	private Map<CSARID, Map<String, String>> mapCSARIDToPlanNameToNamespace = new HashMap<CSARID, Map<String, String>>();

	private Map<CSARID, Map<QName, List<String>>> mapCSARIDToServiceTemplateQNameToNodeTemplateID = new HashMap<>();

	private Map<CSARID, Map<QName, List<String>>> mapCSARIDToServiceTemplateQNameToRelationshipTemplateID = new HashMap<>();

	private Map<CSARID, Map<QName, String>> serviceTemplatePropertiesContent = new HashMap<CSARID, Map<QName, String>>();
	private Map<CSARID, Map<QName, PropertyMappings>> serviceTemplatePropertyMappings = new HashMap<CSARID, Map<QName, PropertyMappings>>();

	public ToscaReferenceMapper() {
		setup();
	}

	/**
	 * This function deletes all stored references of a certain CSAR.
	 * 
	 * @param csarID
	 * @return true for success, false for an error
	 */
	public boolean clearCSARContent(CSARID csarID) {

		LOG.debug("Delete the content of \"" + csarID + "\".");

		setup();
		referenceMap.remove(csarID);
		documentMap.remove(csarID);
		mapCSARIDToDefinitions.remove(csarID);
		mapCSARIDToServiceTemplateIDs.remove(csarID);
		csarIDToPlanTypeToIntegerToPlan.remove(csarID);
		csarIDToWSDLDocuments.remove(csarID);
		csarIDToServiceTemplateIDToPlanID.remove(csarID);
		csarIDToPlanIDToSynchronousBoolean.remove(csarID);
		csarIDToExportedInterface.remove(csarID);
		csarIDToPolicies.remove(csarID);
		mapDefinitionsIDToLocationString.remove(csarID);
		mapElementIDToDefinitionsID.remove(csarID);

		if (containsCSARData(csarID)) {
			return false;
		}
		return true;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.opentosca.model.tosca.TBoundaryDefinitions.Policies getPolicies(CSARID csarID, QName templateID) {
		return csarIDToPolicies.get(csarID, templateID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeConsolidatedPolicies(CSARID csarID, QName templateID, Policies policies) {
		csarIDToPolicies.put(csarID, templateID, policies);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsCSARData(CSARID csarID) {
		setup();

		boolean found = false;

		if (referenceMap.containsKey(csarID)) {
			LOG.trace("Inside of the referenceMap are informations stored");
			found = true;
		}
		if (documentMap.containsKey(csarID)) {
			LOG.trace("Inside of the documentMap are informations stored");
			found = true;
		}
		if (mapCSARIDToDefinitions.containsKey(csarID)) {
			LOG.trace("Inside of the mapCSARIDToDefinitions are informations stored");
			found = true;
		}
		if (mapCSARIDToServiceTemplateIDs.containsKey(csarID)) {
			LOG.trace("Inside of the mapCSARIDToServiceTemplateIDs are informations stored");
			found = true;
		}
		if (csarIDToPlanTypeToIntegerToPlan.containsKey(csarID)) {
			LOG.trace("Inside of the csarIDToPlanTypeToIntegerToPublicPlan are informations stored");
			found = true;
		}
		if (csarIDToWSDLDocuments.containsKey(csarID)) {
			LOG.trace("Inside of the csarIDToWSDLDocuments are informations stored");
			found = true;
		}
		if (csarIDToServiceTemplateIDToPlanID.containsKey(csarID)) {
			LOG.trace("Inside of the csarIDToServiceTemplateIDToPlanID are informations stored");
			found = true;
		}
		if (csarIDToPlanIDToSynchronousBoolean.containsKey(csarID)) {
			LOG.trace("Inside of the csarIDToPlanIDToSynchronousBoolean are informations stored");
			found = true;
		}
		if (csarIDToExportedInterface.containsKey(csarID)) {
			LOG.trace("Inside of the csarIDToExportedInterface are informations stored");
			found = true;
		}
		if (csarIDToPolicies.containsKey(csarID)) {
			LOG.trace("Inside of the csarIDToConsolidatedPolicies are informations stored");
			found = true;
		}
		return found;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsReferenceInsideCSAR(CSARID csarID, QName reference) {
		setup();
		if (containsCSARData(csarID)) {
			if (referenceMap.get(csarID).containsKey(reference)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<PlanTypes, LinkedHashMap<QName, TPlan>> getCSARIDToPlans(CSARID csarID) {

		if (null == csarIDToPlanTypeToIntegerToPlan) {
			LOG.error("The variable is null.");
		}
		if (!csarIDToPlanTypeToIntegerToPlan.containsKey(csarID)) {
			csarIDToPlanTypeToIntegerToPlan.put(csarID, new HashMap<PlanTypes, LinkedHashMap<QName, TPlan>>());
		}
		if (!csarIDToPlanTypeToIntegerToPlan.get(csarID).containsKey(PlanTypes.BUILD)) {
			csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.BUILD, new LinkedHashMap<QName, TPlan>());
			csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.TERMINATION, new LinkedHashMap<QName, TPlan>());
			csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.OTHERMANAGEMENT,
					new LinkedHashMap<QName, TPlan>());
			csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.APPLICATION, new LinkedHashMap<QName, TPlan>());
		}

		return csarIDToPlanTypeToIntegerToPlan.get(csarID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QName> getDefinitionIDsOfCSAR(CSARID csarID) {
		setup();

		List<QName> listOfIDs = new ArrayList<QName>();

		if (mapCSARIDToDefinitions.containsKey(csarID)) {

			for (TDefinitions def : mapCSARIDToDefinitions.get(csarID)) {

				listOfIDs.add(new QName(def.getTargetNamespace(), def.getId()));

			}

		} else {
			LOG.error("There are no Definitions stored for the CSAR \"" + csarID + "\".");
		}

		return listOfIDs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TDefinitions> getDefinitionsOfCSAR(CSARID csarID) {
		setup();
		if (mapCSARIDToDefinitions.containsKey(csarID)) {
			return mapCSARIDToDefinitions.get(csarID);
		} else {
			LOG.error("There are no Definitions stored for the CSAR \"" + csarID + "\".");
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public Document getDOMDocumentForReference(CSARID csarID, QName reference) {
		setup();
		if (documentMap.containsKey(csarID)) {

			// The passed ID of a CSAR is found.
			Map<QName, Document> referenceToDocumentForSpecificCSAR = documentMap.get(csarID);
			if (referenceToDocumentForSpecificCSAR.containsKey(reference)) {
				// The passed reference is found.
				return referenceToDocumentForSpecificCSAR.get(reference);
			} else {
				LOG.error("No stored reference for CSAR \"" + csarID + "\" and \"" + reference + "\" found.");
			}
		} else {
			LOG.error("No stored document references for CSAR \"" + csarID + "\" found.");
		}

		// nothing found
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<QName, List<TExportedInterface>> getExportedInterfacesOfCSAR(CSARID csarID) {
		if (csarIDToExportedInterface.containsKey(csarID)) {
			return csarIDToExportedInterface.get(csarID);
		} else {
			return new HashMap<QName, List<TExportedInterface>>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getReferenceAsNode(CSARID csarID, QName nodeID) {
		setup();

		LOG.debug("Lookup for the node \"" + nodeID + "\" inside of the CSAR \"" + csarID + "\".");

		if (referenceMap.containsKey(csarID)) {

			// The passed ID of a CSAR is found.
			// this.LOG.info("References for the CSAR with the QName \"" +
			// csarID.toString() + "\" found.");
			if (referenceMap.get(csarID).containsKey(nodeID)) {

				// The passed reference is found.
				// this.LOG.info("Reference with the QName \"" +
				// nodeID.toString() + "\" found.");
				return referenceMap.get(csarID).get(nodeID);
			}
		}

		LOG.error("There is no Node stored for CSAR \"" + csarID + "\" and reference \"" + nodeID + "\".");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getJAXBReference(CSARID csarID, QName nodeID) {
		setup();

		LOG.debug("Lookup for the node \"" + nodeID + "\" inside of the CSAR \"" + csarID + "\".");

		if (referenceMap.containsKey(csarID)) {

			// The passed ID of a CSAR is found.
			// this.LOG.info("References for the CSAR with the QName \"" +
			// csarID.toString() + "\" found.");
			if (referenceMap.get(csarID).containsKey(nodeID)) {

				// The passed reference is found.
				// this.LOG.info("Reference with the QName \"" +
				// nodeID.toString() + "\" found.");
				Node node = referenceMap.get(csarID).get(nodeID);

				if (AvailableToscaElements.getElementName(node.getLocalName()).getElementClass() != null) {
					// The name of the node implies that is marshalable into one
					// of the JAXB classes of TOSCA.
					return ToscaReferenceMapper.xmlSerializerService.getXmlSerializer().unmarshal(node,
							AvailableToscaElements.getElementName(node.getLocalName()).getElementClass());
				} else {
					LOG.error("The reference is not a JAXB element.");
				}

			} else {
				LOG.error("Reference with the QName \"" + nodeID.toString() + "\" was not found for the CSAR \""
						+ csarID + "\".");
			}
		} else {
			LOG.error("No references for the CSAR with the QName \"" + csarID.toString() + "\" found.");
		}

		return null;
	}

	public List<Document> getListOfWSDLForCSAR(CSARID csarID) {
		if (csarIDToWSDLDocuments.containsKey(csarID)) {
			return csarIDToWSDLDocuments.get(csarID);
		}
		return new ArrayList<Document>();
	}

	@Override
	public Map<CSARID, Map<QName, List<QName>>> getMapCsarIDToServiceTemplateIDToPlanID() {
		return csarIDToServiceTemplateIDToPlanID;
	}

	/**
	 * Returns a PublicPlan if found.
	 * 
	 * @param csarID
	 * @param planID
	 * @return the PublicPlan if found, null instead.
	 */
	@Override
	public TPlan getPlanForCSARIDAndPlanID(CSARID csarID, QName planID) {

		if (!csarIDToPlanTypeToIntegerToPlan.containsKey(csarID)) {
			csarIDToPlanTypeToIntegerToPlan.put(csarID, new HashMap<PlanTypes, LinkedHashMap<QName, TPlan>>());
		}
		if (!csarIDToPlanTypeToIntegerToPlan.get(csarID).containsKey(PlanTypes.BUILD)) {
			csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.BUILD, new LinkedHashMap<QName, TPlan>());
			csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.TERMINATION, new LinkedHashMap<QName, TPlan>());
			csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.OTHERMANAGEMENT,
					new LinkedHashMap<QName, TPlan>());
			csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.APPLICATION, new LinkedHashMap<QName, TPlan>());
		}

		for (PlanTypes type : csarIDToPlanTypeToIntegerToPlan.get(csarID).keySet()) {
			for (QName planName : csarIDToPlanTypeToIntegerToPlan.get(csarID).get(type).keySet()) {
				TPlan plan = csarIDToPlanTypeToIntegerToPlan.get(csarID).get(type).get(planName);
				if (plan.getId().equals(planID.getLocalPart())) {
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
		setup();
		return mapCSARIDToServiceTemplateIDs.get(csarID);
	}

	@Override
	public Boolean isPlanAsynchronous(CSARID csarID, QName planID) {
		if ((null == csarIDToPlanIDToSynchronousBoolean.get(csarID))
				|| (null == csarIDToPlanIDToSynchronousBoolean.get(csarID).get(planID))) {
			LOG.error("There is no information stored about the plan " + planID + " of CSAR " + csarID
					+ " is synchronous or asynchronous. Thus return null.");
			return null;
		} else {
			return csarIDToPlanIDToSynchronousBoolean.get(csarID).get(planID);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printStoredData() {
		setup();
		String string = "";
		String ls = System.getProperty("line.separator");

		StringBuilder builder = new StringBuilder();

		builder.append("Debug output of the stored data of the TOSCA resolving." + ls);

		if ((null == referenceMap.keySet()) || (referenceMap.keySet().size() == 0)) {
			builder.append("No data about CSARs stored yet.");
			LOG.debug(builder.toString());
			return;
		}

		for (CSARID csarID : referenceMap.keySet()) {

			builder.append(ls + "Print all stored references of \"" + csarID + "\"." + ls);
			for (QName ref : referenceMap.get(csarID).keySet()) {

				if (referenceMap.get(csarID).get(ref) == null) {
					builder.append("ERROR: There is no data stored for the reference \"" + ref + "\"." + ls);
				} else {
					string = ToscaReferenceMapper.xmlSerializerService.getXmlSerializer()
							.docToString(referenceMap.get(csarID).get(ref), true);
					string = string.replace(ls, "");
					builder.append("       " + ref + " --> " + string + ls);
				}
			}

			if (documentMap.containsKey(csarID)) {
				builder.append(ls + "Print all stored documents of \"" + csarID + "\"." + ls);
				for (QName ref : documentMap.get(csarID).keySet()) {

					if (documentMap.get(csarID).get(ref) == null) {
						builder.append("ERROR: There is no data stored for the reference \"" + ref + "\"." + ls);
					} else {
						string = ToscaReferenceMapper.xmlSerializerService.getXmlSerializer()
								.docToString(documentMap.get(csarID).get(ref), true);
						string = string.replace(ls, "");
						builder.append("       " + ref + " --> " + string + ls);
					}
				}
			} else {
				builder.append("ERROR: There is no document stored for \"" + csarID + "\"." + ls);
			}

			builder.append(ls + "Print all due the BoundaryDefinitions defined PublicPlans" + ls);
			for (PlanTypes type : csarIDToPlanTypeToIntegerToPlan.get(csarID).keySet()) {
				builder.append("   type: " + type + ls);
				for (QName planID : csarIDToPlanTypeToIntegerToPlan.get(csarID).get(type).keySet()) {
					TPlan pp = csarIDToPlanTypeToIntegerToPlan.get(csarID).get(type).get(planID);
					builder.append("      name: " + planID + " PublicPlan QName: " + pp.getId() + ls);
				}
			}

			builder.append(ls + "Print all stored plan IDs of this CSAR:" + ls);
			if (null != csarIDToServiceTemplateIDToPlanID.get(csarID)) {
				for (QName serviceTemplateID : csarIDToServiceTemplateIDToPlanID.get(csarID).keySet()) {
					for (QName planID : csarIDToServiceTemplateIDToPlanID.get(csarID).get(serviceTemplateID)) {
						builder.append("       Plan \"" + planID + "\" is inside of ServiceTemplate \""
								+ serviceTemplateID + "\"" + ls);
					}
				}
			} else {
				builder.append("       nothing found ..." + ls);
			}

			builder.append(
					ls + "Print all stored informations about synchronous (false) and asynchronous (true) plans of CSAR \""
							+ csarID + "\":" + ls);
			if (null != csarIDToPlanIDToSynchronousBoolean.get(csarID)) {
				for (QName planID : csarIDToPlanIDToSynchronousBoolean.get(csarID).keySet()) {
					builder.append("    Plan \"" + planID + "\" is asynchronous? "
							+ csarIDToPlanIDToSynchronousBoolean.get(csarID).get(planID) + ls);
				}
			}

			JAXBContext context;
			Marshaller marshaller = null;
			StringWriter writer = new StringWriter();
			try {
				context = JAXBContext.newInstance("org.opentosca.model.tosca");
				marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			} catch (JAXBException e) {
				e.printStackTrace();
			}

			builder.append(ls + "Print list of the mapping of a TemplateID to Consolidated Policies" + ls);
			for (QName templateID : csarIDToPolicies.getTemplateIDs(csarID)) {
				org.opentosca.model.tosca.TBoundaryDefinitions.Policies pols = csarIDToPolicies.get(csarID, templateID);
				builder.append("   " + templateID + " mapps to following policies." + ls);
				for (TPolicy pol : pols.getPolicy()) {
					builder.append("      policy name=\"" + pol.getName() + "\"");
					try {
						marshaller.marshal(pol, writer);
						builder.append(" --> " + writer.toString().replaceAll("\\n|\\r", ""));
					} catch (JAXBException e) {
						e.printStackTrace();
					}
					builder.append(ls);
					// builder.append(" name=\"" + pol.getName() +
					// "\" type=\"" + pol.getType() + "\" language=\"" +
					// pol.getPolicyLanguage() + "\"" + ls);
					// builder.append(" properties: " +
					// xmlSerializerService.getXmlSerializer().docToString((Node)
					// pol.getProperties().getAny(), true) + ls);
					// builder.append(" specific content: todo: serialize to
					// string"
					// + ls);
				}
			}

			if (mapDefinitionsIDToLocationString.containsKey(csarID)) {
				builder.append(ls + "Print map of TOSCA Definitions locations." + ls);
				for (QName defID : mapDefinitionsIDToLocationString.get(csarID).keySet()) {
					builder.append("   " + defID + " is stored at \""
							+ mapDefinitionsIDToLocationString.get(csarID).get(defID).replace("\\", "/") + "\"" + ls);
				}
			}

			if (mapElementIDToDefinitionsID.containsKey(csarID)) {
				builder.append(ls + "Print map of TOSCA element IDs to Definitions ID." + ls);
				for (QName eleID : mapElementIDToDefinitionsID.get(csarID).keySet()) {
					builder.append("   " + eleID + " is contained in Definitions \""
							+ mapElementIDToDefinitionsID.get(csarID).get(eleID) + "\"" + ls);
				}
			}

		}

		LOG.debug(builder.toString());

	}

	/**
	 * This method initializes the data structures in which the the DOM Nodes
	 * and Documents are stored if not done already.
	 */
	private void setup() {
		if (referenceMap == null) {
			referenceMap = new ReferenceMap();
		}
		if (documentMap == null) {
			documentMap = new DocumentMap();
		}
		if (mapCSARIDToDefinitions == null) {
			mapCSARIDToDefinitions = new CSARIDToDefinitionsMap();
		}
		if (mapCSARIDToServiceTemplateIDs == null) {
			mapCSARIDToServiceTemplateIDs = new CSARIDToServiceTemplateIDsMap();
		}
		if (null == csarIDToPlanTypeToIntegerToPlan) {
			csarIDToPlanTypeToIntegerToPlan = new CsarIDToPlanTypeToPlanNameToPlan();
		}
		if (null == csarIDToWSDLDocuments) {
			csarIDToWSDLDocuments = new CsarIDToWSDLDocuments();
		}
		if (null == csarIDToServiceTemplateIDToPlanID) {
			csarIDToServiceTemplateIDToPlanID = new CsarIDToServiceTemplateIDToPlanID();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeDefinitions(CSARID csarID, TDefinitions definitions) {

		setup();
		if ((csarID != null) && (definitions != null)) {

			QName reference = new QName(definitions.getTargetNamespace(), definitions.getId());
			LOG.debug("Store the Definitions \"" + reference + "\".");

			// store it in the Definitions map
			if (!mapCSARIDToDefinitions.containsKey(csarID)) {
				mapCSARIDToDefinitions.put(csarID, new ArrayList<TDefinitions>());
			}
			mapCSARIDToDefinitions.get(csarID).add(definitions);
			// this.mapCSARIDToDefinitions.save(); // Persist definitions.
			// TDefinitions + subclasses need to be serializable

			// store it in the references map
			if (!referenceMap.containsKey(csarID)) {
				referenceMap.put(csarID, new MapQNameNode());
			}
			referenceMap.get(csarID).put(reference,
					ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(definitions));

		} else {
			LOG.error("An error has occured.");
		}

	}

	/**
	 * This method stores a DOM document and its QName for a certain CSAR.
	 * 
	 * @param csarID
	 *            ID of the CSAR in which the document is referenced.
	 * @param documentID
	 *            ID of the document.
	 * @param doc
	 *            DOM document which shall be stored.
	 * @return true means no error, false means one or more errors
	 */
	@Override
	public void storeDocument(CSARID csarID, QName documentID, Document doc) {

		setup();

		if (csarID == null) {
			LOG.error("The CSARID is null!");
			return;
		}
		if (documentID == null) {
			LOG.error("The document ID is null!");
			return;
		}
		if (doc == null) {
			LOG.error("The document is null!");
			return;
		}

		LOG.debug("Store new document reference for CSAR \"" + csarID + "\" the reference \"" + documentID + "\".");

		if (!documentMap.containsKey(csarID)) {
			documentMap.put(csarID, new HashMap<QName, Document>());
		}

		if (documentMap.get(csarID).containsKey(documentID)) {
			LOG.debug("The reference with the QName \"" + documentID.toString() + "\" is already stored for the CSAR \""
					+ csarID + "\".");
		} else {
			documentMap.get(csarID).put(documentID, doc);
			LOG.debug("Storing of Document \"" + documentID.toString() + "\" completed.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeExportedInterface(CSARID csarID, QName serviceTemplateID, TExportedInterface iface) {
		if (!csarIDToExportedInterface.containsKey(csarID)) {
			csarIDToExportedInterface.put(csarID, new HashMap<QName, List<TExportedInterface>>());
		}
		if (!csarIDToExportedInterface.get(csarID).containsKey(serviceTemplateID)) {
			csarIDToExportedInterface.get(csarID).put(serviceTemplateID, new ArrayList<TExportedInterface>());
		}
		csarIDToExportedInterface.get(csarID).get(serviceTemplateID).add(iface);
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

		if (!csarIDToWSDLDocuments.containsKey(csarID)) {
			csarIDToWSDLDocuments.put(csarID, new ArrayList<Document>());
		}

		for (Document doc : listOfWSDL) {
			csarIDToWSDLDocuments.get(csarID).add(doc);
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storePlanAsynchronousBoolean(CSARID csarID, QName planID, boolean checkAsynchronous) {
		if (null == csarIDToPlanIDToSynchronousBoolean.get(csarID)) {
			csarIDToPlanIDToSynchronousBoolean.put(csarID, new HashMap<QName, Boolean>());
		}
		if (null == csarIDToPlanIDToSynchronousBoolean.get(csarID).get(planID)) {
			csarIDToPlanIDToSynchronousBoolean.get(csarID).put(planID, checkAsynchronous);
		} else {
			LOG.error("For the CSAR " + csarID + " and plan " + planID
					+ " is already stored wheter it is a synchronous or an asynchronous plan.");
		}
	}

	public void storePlanIDForCSARAndServiceTemplate(CSARID csarID, QName serviceTemplateID, QName planID) {
		if (!csarIDToServiceTemplateIDToPlanID.containsKey(csarID)) {
			csarIDToServiceTemplateIDToPlanID.put(csarID, new HashMap<QName, List<QName>>());
		}
		if (!csarIDToServiceTemplateIDToPlanID.get(csarID).containsKey(serviceTemplateID)) {
			csarIDToServiceTemplateIDToPlanID.get(csarID).put(serviceTemplateID, new ArrayList<QName>());
		}
		csarIDToServiceTemplateIDToPlanID.get(csarID).get(serviceTemplateID).add(planID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeReference(CSARID csarID, QName nodeID, Node node) {
		setup();

		MapQNameNode csarMap;

		if (referenceMap.containsKey(csarID)) {
			// CSARID is known
			csarMap = referenceMap.get(csarID);
			if (csarMap.containsKey(nodeID)) {
				// node is stored already
				LOG.debug("The reference with the QName \"" + nodeID.toString() + "\" is already stored for the CSAR \""
						+ csarID + "\".");
			} else {
				// store this node
				csarMap.put(nodeID, node);
				LOG.debug("Storing of Node \"" + nodeID.toString() + "\" completed.");
			}

		} else {
			// CSARID is not known, so store a new HashMap for this ID
			csarMap = new MapQNameNode();
			csarMap.put(nodeID, node);
			referenceMap.put(csarID, csarMap);
			LOG.debug("Storing of \"" + nodeID.toString() + "\" completed.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeServiceTemplateIDForCSARID(QName serviceTemplateID, CSARID csarID) {
		setup();
		if ((serviceTemplateID != null) && (csarID != null)) {
			if (!mapCSARIDToServiceTemplateIDs.containsKey(csarID)) {
				mapCSARIDToServiceTemplateIDs.put(csarID, new ArrayList<QName>());
			}
			mapCSARIDToServiceTemplateIDs.get(csarID).add(serviceTemplateID);
		} else {
			LOG.error("An error has occured.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeDefinitionsLocation(CSARID csarID, QName defID, String location) {
		if (!mapDefinitionsIDToLocationString.containsKey(csarID)) {
			mapDefinitionsIDToLocationString.put(csarID, new HashMap<QName, String>());
		}
		if (mapDefinitionsIDToLocationString.get(csarID).containsKey(defID)) {
			LOG.warn("Overwrite the location for the Definitions \"" + defID + "\" in the CSAR \"" + csarID + "\".");
		}
		mapDefinitionsIDToLocationString.get(csarID).put(defID, location);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDefinitionsLocation(CSARID csarID, QName defID) {
		if (mapDefinitionsIDToLocationString.containsKey(csarID)) {
			if (mapDefinitionsIDToLocationString.get(csarID).containsKey(defID)) {
				return mapDefinitionsIDToLocationString.get(csarID).get(defID);
			}
		}
		LOG.error("No location found for the Definitions \"" + defID + "\" in CSAR \"" + csarID + "\".");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeContainingDefinitionsID(CSARID csarID, QName elementID, QName definitionsID) {
		if (!mapElementIDToDefinitionsID.containsKey(csarID)) {
			mapElementIDToDefinitionsID.put(csarID, new HashMap<QName, QName>());
		}
		if (mapElementIDToDefinitionsID.get(csarID).containsKey(elementID)) {
			LOG.warn("Overwrite the mapping for the element \"" + elementID + "\" in the CSAR \"" + csarID + "\".");
		}
		mapElementIDToDefinitionsID.get(csarID).put(elementID, definitionsID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QName getContainingDefinitionsID(CSARID csarID, QName elementID) {
		if (mapElementIDToDefinitionsID.containsKey(csarID)) {
			if (mapElementIDToDefinitionsID.get(csarID).containsKey(elementID)) {
				return mapElementIDToDefinitionsID.get(csarID).get(elementID);
			}
		}
		LOG.error("No Definitions ID found for the element \"" + elementID + "\" in CSAR \"" + csarID + "\".");
		return null;
	}

	protected void bindIXMLSerializerService(IXMLSerializerService service) {
		if (service == null) {
			LOG.error("Service IXMLSerializerService is null.");
		} else {
			LOG.debug("Bind of the IXMLSerializerService.");
			ToscaReferenceMapper.xmlSerializerService = service;
		}
	}

	protected void unbindIXMLSerializerService(IXMLSerializerService service) {
		LOG.debug("Unbind of the IXMLSerializerService.");
		ToscaReferenceMapper.xmlSerializerService = null;
	}

	@Override
	public void storePlanInputMessageID(CSARID csarID, QName planID, QName messageID) {
		if (!mapCSARIDToPlanIDToInputMessageID.containsKey(csarID)) {
			mapCSARIDToPlanIDToInputMessageID.put(csarID, new HashMap<QName, QName>());
		}
		if (null != mapCSARIDToPlanIDToInputMessageID.get(csarID).get(planID)) {
			LOG.error("There is already a message ID stored for CSAR {} and Plan {}", csarID, planID);
		} else {
			mapCSARIDToPlanIDToInputMessageID.get(csarID).put(planID, messageID);
		}

	}

	@Override
	public QName getPlanInputMessageID(CSARID csarID, QName planID) {
		try {
			return mapCSARIDToPlanIDToInputMessageID.get(csarID).get(planID);
		} catch (NullPointerException e) {
			LOG.error("There is no message ID stored for CSAR {} and Plan {}", csarID, planID);
			return null;
		}
	}

	@Override
	public void storeServiceTemplateBoundsPlan(CSARID csarID, QName serviceTemplateID, String interfaceName,
			String opName, QName planID) {

		if (null == mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID)) {
			mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.put(csarID,
					new HashMap<QName, Map<String, Map<String, QName>>>());
		}
		if (null == mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)) {
			mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).put(serviceTemplateID,
					new HashMap<String, Map<String, QName>>());
		}
		if (null == mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)
				.get(interfaceName)) {
			mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)
					.put(interfaceName, new HashMap<String, QName>());
		}
		mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID).get(interfaceName)
				.put(opName, planID);
	}

	// @Override
	// public void storeInterfaceNameForPlan(CSARID csarID, QName
	// serviceTemplateID, QName planID, String name) {
	// if (null ==
	// mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.get(csarID)) {
	// mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.put(csarID, new
	// HashMap<QName, Map<QName, String>>());
	// }
	// if (null ==
	// mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.get(csarID).get(serviceTemplateID))
	// {
	// mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.get(csarID).put(serviceTemplateID,
	// new HashMap<QName, String>());
	// }
	// mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.get(csarID).get(serviceTemplateID).put(planID,
	// name);
	// }

	// @Override
	// public void storeOperationNameForPlan(CSARID csarID, QName
	// serviceTemplateID, QName planID, String interfaceName, String
	// operationName) {
	// if (null ==
	// mapCSARIDToServiceTemplateIdToPlanIDToOperationName.get(csarID)) {
	// mapCSARIDToServiceTemplateIdToPlanIDToOperationName.put(csarID, new
	// HashMap<QName, Map<QName, String>>());
	// }
	// if (null ==
	// mapCSARIDToServiceTemplateIdToPlanIDToOperationName.get(csarID).get(serviceTemplateID))
	// {
	// mapCSARIDToServiceTemplateIdToPlanIDToOperationName.get(csarID).put(serviceTemplateID,
	// new HashMap<QName, String>());
	// }
	// mapCSARIDToServiceTemplateIdToPlanIDToOperationName.get(csarID).get(serviceTemplateID).put(planID,
	// operationName);
	// }

	@Override
	public String getIntferaceNameOfPlan(CSARID csarID, QName planID) {

		if (!mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.containsKey(csarID)) {
			return null;
		}
		for (QName st : mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).keySet()) {
			for (String intf : mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st).keySet()) {
				for (String op : mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st).get(intf)
						.keySet()) {
					if (mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st).get(intf).get(op)
							.equals(planID)) {
						return intf;
					}
				}
			}
		}

		// if (null !=
		// mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.get(csarID)) {
		// for (QName stid :
		// mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.get(csarID).keySet())
		// {
		// if
		// (mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.get(csarID).get(stid).containsKey(planID))
		// {
		// return
		// mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.get(csarID).get(stid).get(planID);
		// }
		// }
		// }
		return null;
	}

	@Override
	public String getIntferaceNameOfPlan(CSARID csarID, QName serviceTemplateID, QName planID) {

		if (!mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.containsKey(csarID)) {
			return null;
		}
		if (!mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).containsKey(serviceTemplateID)) {
			return null;
		}

		for (String intf : mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)
				.keySet()) {
			for (String op : mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)
					.get(intf).keySet()) {
				if (mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)
						.get(intf).get(op).equals(planID)) {
					return intf;
				}
			}
		}
		return null;
	}

	@Override
	public String getOperationNameOfPlan(CSARID csarID, QName planID) {
		if (!mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.containsKey(csarID)) {
			return null;
		}
		for (QName st : mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).keySet()) {
			for (String intf : mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st).keySet()) {
				for (String op : mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st).get(intf)
						.keySet()) {
					if (mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st).get(intf).get(op)
							.equals(planID)) {
						return op;
					}
				}
			}
		}
		return null;
	}

	@Override
	public List<String> getBoundaryInterfacesOfCSAR(CSARID csarID) {
		List<String> list = new ArrayList<String>();

		if (!mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.containsKey(csarID)) {
			return null;
		}
		for (QName st : mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).keySet()) {
			for (String intf : mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st).keySet()) {
				list.add(intf);
			}
		}

		return list;
	}

	@Override
	public List<String> getBoundaryInterfacesOfServiceTemplate(CSARID csarID, QName serviceTemplateID) {
		List<String> list = new ArrayList<String>();

		if (!mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.containsKey(csarID)) {
			return null;
		}
		if (!mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).containsKey(serviceTemplateID)) {
			return null;
		}

		for (String intf : mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)
				.keySet()) {
			list.add(intf);
		}

		return list;
	}

	// @Override
	// public void setBoundaryInterfaceForCSARIDPlan(CSARID csarID, QName
	// serviceTemplateID, QName planID, String ifaceName) {
	// if
	// (!mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.containsKey(csarID))
	// {
	// mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.put(csarID, new
	// HashMap<QName, Map<QName, String>>());
	// }
	// if
	// (!mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.get(csarID).containsKey(serviceTemplateID))
	// {
	// mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.get(csarID).put(serviceTemplateID,
	// new HashMap<QName, String>());
	// }
	// mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName.get(csarID).get(serviceTemplateID).put(planID,
	// ifaceName);
	// }

	// @Override
	// public void setBoundaryOperationForCSARIDPlan(CSARID csarID, QName
	// serviceTemplateID, QName planName, String opName) {
	// if (null ==
	// mapCSARIDToServiceTemplateIdToPlanIDToOperationName.get(csarID)) {
	// mapCSARIDToServiceTemplateIdToPlanIDToOperationName.put(csarID, new
	// HashMap<QName, Map<QName, String>>());
	// }
	// if (null ==
	// mapCSARIDToServiceTemplateIdToPlanIDToOperationName.get(csarID).get(serviceTemplateID))
	// {
	// mapCSARIDToServiceTemplateIdToPlanIDToOperationName.get(csarID).put(serviceTemplateID,
	// new HashMap<QName, String>());
	// }
	// mapCSARIDToServiceTemplateIdToPlanIDToOperationName.get(csarID).get(serviceTemplateID).put(planName,
	// opName);
	// }

	@Override
	public List<String> getBoundaryOperationsOfCSARInterface(CSARID csarID, QName serviceTemplateID, String intName) {
		List<String> list = new ArrayList<String>();

		if (!mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.containsKey(csarID)) {
			return list;
		}
		if (!mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).containsKey(serviceTemplateID)) {
			return list;
		}
		if (!mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)
				.containsKey(intName)) {
			return list;
		}

		for (String op : mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)
				.get(intName).keySet()) {
			list.add(op);
		}

		return list;
	}

	@Override
	public QName getBoundaryPlanOfCSARInterface(CSARID csarID, String intName, String opName) {

		Map<QName, List<TExportedInterface>> stToIntfs = csarIDToExportedInterface.get(csarID);

		if (null != stToIntfs) {
			for (QName serviceTemplate : stToIntfs.keySet()) {
				for (TExportedInterface intf : stToIntfs.get(serviceTemplate)) {
					if (intf.getName().equals(intName)) {
						for (TExportedOperation op : intf.getOperation()) {
							if (op.getName().equals(opName)) {
								return new QName(serviceTemplate.getNamespaceURI(),
										((TPlan) op.getPlan().getPlanRef()).getId());
							}
						}
					}
				}
			}
		}

		// Map<QName, String> map =
		// mapCSARIDToPlanIDToOperationName.get(csarID);
		//
		// if (null != map) {
		// for (QName plan : map.keySet()) {
		// if (map.get(plan).contains(opName)) {
		// return plan;
		// }
		// }
		// }
		return null;
	}

	@Override
	public String getNamespaceOfPlan(CSARID csarID, String planID) {
		if (null != mapCSARIDToPlanNameToNamespace.get(csarID)) {
			return mapCSARIDToPlanNameToNamespace.get(csarID).get(planID);
		}
		return null;
	}

	@Override
	public void storeNamespaceOfPlan(CSARID csarID, String planID, String namespace) {
		if (!mapCSARIDToPlanNameToNamespace.containsKey(csarID)) {
			mapCSARIDToPlanNameToNamespace.put(csarID, new HashMap<String, String>());
		}
		mapCSARIDToPlanNameToNamespace.get(csarID).put(planID, namespace);
	}

	@Override
	public void storeNodeTemplateIDForServiceTemplateAndCSAR(CSARID csarID, QName serviceTemplateID, String id) {
		if (!mapCSARIDToServiceTemplateQNameToNodeTemplateID.containsKey(csarID)) {
			mapCSARIDToServiceTemplateQNameToNodeTemplateID.put(csarID, new HashMap<QName, List<String>>());
		}
		Map<QName, List<String>> map = mapCSARIDToServiceTemplateQNameToNodeTemplateID.get(csarID);
		if (!map.containsKey(serviceTemplateID)) {
			map.put(serviceTemplateID, new ArrayList<String>());
		}
		List<String> list = map.get(serviceTemplateID);
		if (!list.contains(id)) {
			list.add(id);
		}
	}

	@Override
	public void storeRelationshipTemplateIDForServiceTemplateANdCSAR(CSARID csarId, QName serviceTemplateID,
			String id) {
		if (!mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.containsKey(csarId)) {
			mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.put(csarId, new HashMap<QName, List<String>>());
		}
		Map<QName, List<String>> map = mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.get(csarId);
		if (!map.containsKey(serviceTemplateID)) {
			map.put(serviceTemplateID, new ArrayList<String>());
		}
		List<String> list = map.get(serviceTemplateID);
		if (!list.contains(id)) {
			list.add(id);
		}

	}

	@Override
	public Map<QName, List<String>> getServiceTemplatesAndNodeTemplatesInCSAR(CSARID csarID) {
		return mapCSARIDToServiceTemplateQNameToNodeTemplateID.get(csarID);
	}

	@Override
	public Map<QName, List<String>> getServiceTemplate2RelationshipTemplateMap(CSARID csarID) {
		return mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.get(csarID);
	}

	@Override
	public void storeServiceTemplateBoundsPropertiesInformation(CSARID csarID, QName serviceTemplateID,
			String propertiesContent, PropertyMappings propertyMappings) {
		if (null == serviceTemplatePropertiesContent.get(csarID)) {
			serviceTemplatePropertiesContent.put(csarID, new HashMap<QName, String>());
		}
		serviceTemplatePropertiesContent.get(csarID).put(serviceTemplateID, propertiesContent);
		if (null == serviceTemplatePropertyMappings.get(csarID)) {
			serviceTemplatePropertyMappings.put(csarID, new HashMap<QName, PropertyMappings>());
		}
		serviceTemplatePropertyMappings.get(csarID).put(serviceTemplateID, propertyMappings);
	}

	@Override
	public String getServiceTemplateBoundsPropertiesContent(CSARID csarID, QName serviceTemplateID) {
		return serviceTemplatePropertiesContent.get(csarID).get(serviceTemplateID);
	}

	@Override
	public List<String> getServiceTemplateBoundsPropertiesContent(CSARID csarID) {
		List<String> ret = new ArrayList<String>();
		for (QName st : serviceTemplatePropertiesContent.get(csarID).keySet()) {
			ret.add(serviceTemplatePropertiesContent.get(csarID).get(st));
		}
		return ret;
	}

	@Override
	public PropertyMappings getServiceTemplateBoundsPropertyMappings(CSARID csarID, QName serviceTemplateID) {
		return serviceTemplatePropertyMappings.get(csarID).get(serviceTemplateID);
	}

	@Override
	public List<PropertyMappings> getServiceTemplateBoundsPropertyMappings(CSARID csarID) {
		List<PropertyMappings> ret = new ArrayList<PropertyMappings>();
		for (QName st : serviceTemplatePropertyMappings.get(csarID).keySet()) {
			ret.add(serviceTemplatePropertyMappings.get(csarID).get(st));
		}
		return ret;
	}

}
