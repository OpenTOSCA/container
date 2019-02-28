package org.opentosca.container.core.engine.impl;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.opentosca.container.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties.PropertyMappings;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPropertyMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The ToscaReferenceMapper provides the functionality of a library for DOM Nodes. These Nodes are
 * referenced informations of TOSCA files. They are stored as a pair of a QName and the Node itself.
 * The QName is the one with which the Node is referenced inside of a TOSCA document. But not only
 * TOSCA elements are stored as Node, but also elements of XML Schema or WSDL and so on. Nodes of
 * TOSCA can be retrieved as JAXB objects aswell.
 *
 * @au Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 *
 */
@Service
public class ToscaReferenceMapper implements IToscaReferenceMapper {

    // services
    private static IXMLSerializerService xmlSerializerService;

    // logger
    private final Logger LOG = LoggerFactory.getLogger(ToscaReferenceMapper.class);

    // internal data structures
    private Map<CSARID, Map<QName, Node>> referenceMap = new HashMap<>();
    private Map<CSARID, Map<QName, Document>> documentMap = new HashMap<>();
    private Map<CSARID, List<TDefinitions>> mapCSARIDToDefinitions = new HashMap<>();
    private Map<CSARID, List<QName>> mapCSARIDToServiceTemplateIDs = new HashMap<>();
    private Map<CSARID, Map<PlanTypes, LinkedHashMap<QName, TPlan>>> csarIDToPlanTypeToIntegerToPlan = new HashMap<>();
    private Map<CSARID, List<Document>> csarIDToWSDLDocuments = new HashMap<>();
    private Map<CSARID, Map<QName, List<QName>>> csarIDToServiceTemplateIDToPlanID = new HashMap<>();
    private final Map<CSARID, Map<QName, Boolean>> csarIDToPlanIDToSynchronousBoolean = new HashMap<>();
    private final Map<CSARID, Map<QName, List<TExportedInterface>>> csarIDToExportedInterface = new HashMap<>();
    private final Map<CSARID, Map<QName, TPolicies>> csarIDToPolicies = new HashMap<>();
    private final Map<CSARID, Map<QName, String>> mapDefinitionsIDToLocationString = new HashMap<>();
    private final Map<CSARID, Map<QName, QName>> mapElementIDToDefinitionsID = new HashMap<>();
    private final Map<CSARID, Map<QName, QName>> mapCSARIDToPlanIDToInputMessageID = new HashMap<>();

    private final Map<CSARID, Map<QName, Map<String, Map<String, QName>>>> mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan =
        new HashMap<>();

    private final Map<CSARID, Map<String, String>> mapCSARIDToPlanNameToNamespace = new HashMap<>();

    private final Map<CSARID, Map<QName, List<String>>> mapCSARIDToServiceTemplateQNameToNodeTemplateID =
        new HashMap<>();

    private final Map<CSARID, Map<QName, List<String>>> mapCSARIDToServiceTemplateQNameToRelationshipTemplateID =
        new HashMap<>();

    private final Map<CSARID, Map<QName, String>> serviceTemplatePropertiesContent = new HashMap<>();
    private final Map<CSARID, Map<QName, PropertyMappings>> serviceTemplatePropertyMappings = new HashMap<>();

    /**
     * This function deletes all stored references of a certain CSAR.
     *
     * @param csarID
     * @return true for success, false for an error
     */
    public boolean clearCSARContent(final CSARID csarID) {

        this.LOG.debug("Delete the content of \"" + csarID + "\".");

        this.referenceMap.remove(csarID);
        this.documentMap.remove(csarID);
        this.mapCSARIDToDefinitions.remove(csarID);
        this.mapCSARIDToServiceTemplateIDs.remove(csarID);
        this.csarIDToPlanTypeToIntegerToPlan.remove(csarID);
        this.csarIDToWSDLDocuments.remove(csarID);
        this.csarIDToServiceTemplateIDToPlanID.remove(csarID);
        this.csarIDToPlanIDToSynchronousBoolean.remove(csarID);
        this.csarIDToExportedInterface.remove(csarID);
        this.csarIDToPolicies.remove(csarID);
        this.mapDefinitionsIDToLocationString.remove(csarID);
        this.mapElementIDToDefinitionsID.remove(csarID);
        this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.remove(csarID);
        this.mapCSARIDToServiceTemplateQNameToNodeTemplateID.remove(csarID);
        this.mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.remove(csarID);
        this.serviceTemplatePropertiesContent.remove(csarID);
        this.serviceTemplatePropertyMappings.remove(csarID);

        return !containsCSARData(csarID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TPolicies getPolicies(final CSARID csarID, final QName templateID) {
        return this.csarIDToPolicies.get(csarID).get(templateID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeConsolidatedPolicies(final CSARID csarID, final QName templateID, final TPolicies policies) {
        this.csarIDToPolicies.computeIfAbsent(csarID, id -> new HashMap<>()).put(templateID, policies);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsCSARData(final CSARID csarID) {
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
        if (this.csarIDToPlanTypeToIntegerToPlan.containsKey(csarID)) {
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
        if (this.csarIDToPolicies.containsKey(csarID)) {
            this.LOG.trace("Inside of the csarIDToConsolidatedPolicies are informations stored");
            found = true;
        }
        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsReferenceInsideCSAR(final CSARID csarID, final QName reference) {
        if (containsCSARData(csarID)) {
            if (this.referenceMap.getOrDefault(csarID, new HashMap<>()).containsKey(reference)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<PlanTypes, LinkedHashMap<QName, TPlan>> getCSARIDToPlans(final CSARID csarID) {

        if (null == this.csarIDToPlanTypeToIntegerToPlan) {
            this.LOG.error("The variable is null.");
        }
        if (!this.csarIDToPlanTypeToIntegerToPlan.containsKey(csarID)) {
            this.csarIDToPlanTypeToIntegerToPlan.put(csarID, new HashMap<PlanTypes, LinkedHashMap<QName, TPlan>>());
        }
        Map<PlanTypes, LinkedHashMap<QName, TPlan>> planTypeToPlan = this.csarIDToPlanTypeToIntegerToPlan.get(csarID);
        if (!planTypeToPlan.containsKey(PlanTypes.BUILD)) {
            planTypeToPlan.put(PlanTypes.BUILD, new LinkedHashMap<QName, TPlan>());
            planTypeToPlan.put(PlanTypes.TERMINATION, new LinkedHashMap<QName, TPlan>());
            planTypeToPlan.put(PlanTypes.OTHERMANAGEMENT, new LinkedHashMap<QName, TPlan>());
            planTypeToPlan.put(PlanTypes.APPLICATION, new LinkedHashMap<QName, TPlan>());
        }

        return planTypeToPlan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<QName> getDefinitionIDsOfCSAR(final CSARID csarID) {
        if (!this.mapCSARIDToDefinitions.containsKey(csarID)) {
            this.LOG.error("There are no Definitions stored for the CSAR \"" + csarID + "\".");
            return Collections.emptyList();
        }

        return this.mapCSARIDToDefinitions.getOrDefault(csarID, Collections.emptyList()).stream()
                                          .map(toscaDefinition -> new QName(toscaDefinition.getTargetNamespace(),
                                              toscaDefinition.getId()))
                                          .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TDefinitions> getDefinitionsOfCSAR(final CSARID csarID) {
        if (!this.mapCSARIDToDefinitions.containsKey(csarID)) {
            this.LOG.error("There are no Definitions stored for the CSAR \"" + csarID + "\".");
        }
        // TODO kill null as default value
        return mapCSARIDToDefinitions.getOrDefault(csarID, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public Document getDOMDocumentForReference(final CSARID csarID, final QName reference) {
        if (this.documentMap.containsKey(csarID)) {

            // The passed ID of a CSAR is found.
            final Map<QName, Document> referenceToDocumentForSpecificCSAR = this.documentMap.get(csarID);
            if (referenceToDocumentForSpecificCSAR.containsKey(reference)) {
                // The passed reference is found.
                return referenceToDocumentForSpecificCSAR.get(reference);
            } else {
                this.LOG.error("No stored reference for CSAR \"" + csarID + "\" and \"" + reference + "\" found.");
            }
        } else {
            this.LOG.error("No stored document references for CSAR \"" + csarID + "\" found.");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<QName, List<TExportedInterface>> getExportedInterfacesOfCSAR(final CSARID csarID) {
        return this.csarIDToExportedInterface.getOrDefault(csarID, new HashMap<>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getReferenceAsNode(final CSARID csarID, final QName nodeID) {
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
    public Object getJAXBReference(final CSARID csarID, final QName nodeID) {
        this.LOG.debug("Lookup for the node \"" + nodeID + "\" inside of the CSAR \"" + csarID + "\".");

        if (!this.referenceMap.containsKey(csarID)) {
            this.LOG.error("No references for the CSAR with the QName \"" + csarID.toString() + "\" found.");
            return null;
        }

        // The passed ID of a CSAR is found.
        // this.LOG.info("References for the CSAR with the QName \"" +
        // csarID.toString() + "\" found.");
        if (!this.referenceMap.get(csarID).containsKey(nodeID)) {
            this.LOG.error("Reference with the QName \"" + nodeID.toString() + "\" was not found for the CSAR \""
                + csarID + "\".");
            return null;
        }

        // The passed reference is found.
        // this.LOG.info("Reference with the QName \"" +
        // nodeID.toString() + "\" found.");
        final Node node = this.referenceMap.get(csarID).get(nodeID);

        if (AvailableToscaElements.getElementName(node.getLocalName()).getElementClass() == null) {
            this.LOG.error("The reference is not a JAXB element.");
            return null;
        }
        // The name of the node implies that is marshalable into one
        // of the JAXB classes of TOSCA.
        return ToscaReferenceMapper.xmlSerializerService.getXmlSerializer()
                                                        .unmarshal(node,
                                                                   AvailableToscaElements.getElementName(node.getLocalName())
                                                                                         .getElementClass());
    }

    public List<Document> getListOfWSDLForCSAR(final CSARID csarID) {
        return this.csarIDToWSDLDocuments.getOrDefault(csarID, new ArrayList<>());
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
    public TPlan getPlanForCSARIDAndPlanID(final CSARID csarID, final QName planID) {

        final Map<PlanTypes, LinkedHashMap<QName, TPlan>> plansByType =
            this.csarIDToPlanTypeToIntegerToPlan.computeIfAbsent(csarID,
                                                                 id -> new HashMap<PlanTypes, LinkedHashMap<QName, TPlan>>());
        if (!plansByType.containsKey(PlanTypes.BUILD)) {
            plansByType.put(PlanTypes.BUILD, new LinkedHashMap<QName, TPlan>());
            plansByType.put(PlanTypes.TERMINATION, new LinkedHashMap<QName, TPlan>());
            plansByType.put(PlanTypes.OTHERMANAGEMENT, new LinkedHashMap<QName, TPlan>());
            plansByType.put(PlanTypes.APPLICATION, new LinkedHashMap<QName, TPlan>());
        }

        return plansByType.values().stream().flatMap(byType -> byType.values().stream())
                          .filter(plan -> plan.getId().equals(planID.getLocalPart())).findFirst().orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<QName> getServiceTemplateIDsContainedInCSAR(final CSARID csarID) {
        return this.mapCSARIDToServiceTemplateIDs.getOrDefault(csarID, Collections.emptyList());
    }

    @Override
    public Boolean isPlanAsynchronous(final CSARID csarID, final QName planID) {
        if (null == this.csarIDToPlanIDToSynchronousBoolean.get(csarID)
            || null == this.csarIDToPlanIDToSynchronousBoolean.get(csarID).get(planID)) {
            this.LOG.error("There is no information stored about the plan " + planID + " of CSAR " + csarID
                + " is synchronous or asynchronous. Thus return null.");
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
        String string = "";
        final StringBuilder log = new StringBuilder();
        log.append("Debug output of the stored data of the TOSCA resolving." + System.lineSeparator());

        if (null == this.referenceMap.keySet() || this.referenceMap.keySet().size() == 0) {
            log.append("No data about CSARs stored yet.");
            this.LOG.debug(log.toString());
            return;
        }

        for (final CSARID csarID : this.referenceMap.keySet()) {

            log.append(System.lineSeparator() + "Print all stored references of \"" + csarID + "\"."
                + System.lineSeparator());
            for (final QName ref : this.referenceMap.get(csarID).keySet()) {

                if (this.referenceMap.get(csarID).get(ref) == null) {
                    log.append("ERROR: There is no data stored for the reference \"" + ref + "\"."
                        + System.lineSeparator());
                } else {
                    string =
                        ToscaReferenceMapper.xmlSerializerService.getXmlSerializer()
                                                                 .docToString(this.referenceMap.get(csarID).get(ref),
                                                                              true);
                    string = string.replace(System.lineSeparator(), "");
                    log.append("       " + ref + " --> " + string + System.lineSeparator());
                }
            }

            if (this.documentMap.containsKey(csarID)) {
                log.append(System.lineSeparator() + "Print all stored documents of \"" + csarID + "\"."
                    + System.lineSeparator());
                for (final QName ref : this.documentMap.get(csarID).keySet()) {

                    if (this.documentMap.get(csarID).get(ref) == null) {
                        log.append("ERROR: There is no data stored for the reference \"" + ref + "\"."
                            + System.lineSeparator());
                    } else {
                        string =
                            ToscaReferenceMapper.xmlSerializerService.getXmlSerializer()
                                                                     .docToString(this.documentMap.get(csarID).get(ref),
                                                                                  true);
                        string = string.replace(System.lineSeparator(), "");
                        log.append("       " + ref + " --> " + string + System.lineSeparator());
                    }
                }
            } else {
                log.append("ERROR: There is no document stored for \"" + csarID + "\"." + System.lineSeparator());
            }

            log.append(System.lineSeparator() + "Print all due the BoundaryDefinitions defined PublicPlans"
                + System.lineSeparator());
            for (final PlanTypes type : this.csarIDToPlanTypeToIntegerToPlan.get(csarID).keySet()) {
                log.append("   type: " + type + System.lineSeparator());
                for (final QName planID : this.csarIDToPlanTypeToIntegerToPlan.get(csarID).get(type).keySet()) {
                    final TPlan pp = this.csarIDToPlanTypeToIntegerToPlan.get(csarID).get(type).get(planID);
                    log.append("      name: " + planID + " PublicPlan QName: " + pp.getId() + System.lineSeparator());
                }
            }

            log.append(System.lineSeparator() + "Print all stored plan IDs of this CSAR:" + System.lineSeparator());
            if (null != this.csarIDToServiceTemplateIDToPlanID.get(csarID)) {
                for (final QName serviceTemplateID : this.csarIDToServiceTemplateIDToPlanID.get(csarID).keySet()) {
                    for (final QName planID : this.csarIDToServiceTemplateIDToPlanID.get(csarID)
                                                                                    .get(serviceTemplateID)) {
                        log.append("       Plan \"" + planID + "\" is inside of ServiceTemplate \"" + serviceTemplateID
                            + "\"" + System.lineSeparator());
                    }
                }
            } else {
                log.append("       nothing found ..." + System.lineSeparator());
            }

            log.append(System.lineSeparator()
                + "Print all stored informations about synchronous (false) and asynchronous (true) plans of CSAR \""
                + csarID + "\":" + System.lineSeparator());
            if (null != this.csarIDToPlanIDToSynchronousBoolean.get(csarID)) {
                for (final QName planID : this.csarIDToPlanIDToSynchronousBoolean.get(csarID).keySet()) {
                    log.append("    Plan \"" + planID + "\" is asynchronous? "
                        + this.csarIDToPlanIDToSynchronousBoolean.get(csarID).get(planID) + System.lineSeparator());
                }
            }

            JAXBContext context;
            Marshaller marshaller = null;
            final StringWriter writer = new StringWriter();
            try {
                context = JAXBContext.newInstance(TPolicy.class);
                marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            }
            catch (final JAXBException e) {
                e.printStackTrace();
            }

            log.append(System.lineSeparator() + "Print list of the mapping of a TemplateID to Consolidated Policies"
                + System.lineSeparator());
            for (final QName templateID : this.csarIDToPolicies.get(csarID).keySet()) {
                final TPolicies pols = this.csarIDToPolicies.get(csarID).get(templateID);
                log.append("   " + templateID + " mapps to following policies." + System.lineSeparator());
                for (final TPolicy pol : pols.getPolicy()) {
                    log.append("      policy name=\"" + pol.getName() + "\"");
                    try {
                        marshaller.marshal(pol, writer);
                        log.append(" --> " + writer.toString().replaceAll("\\n|\\r", ""));
                    }
                    catch (final JAXBException e) {
                        e.printStackTrace();

                        // FIXME: (miwurster; 2018-03-08) Not sure if we can ignore this, but we get an exception here
                        // if TPolicy is tried to be
                        // serialized. Anyhow, such exceptions are not handled, so I assume the stack trace print is not
                        // necessary.
                        // e.printStackTrace();
                    }
                    log.append(System.lineSeparator());
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

            if (this.mapDefinitionsIDToLocationString.containsKey(csarID)) {
                log.append(System.lineSeparator() + "Print map of TOSCA Definitions locations."
                    + System.lineSeparator());
                for (final QName defID : this.mapDefinitionsIDToLocationString.get(csarID).keySet()) {
                    log.append("   " + defID + " is stored at \""
                        + this.mapDefinitionsIDToLocationString.get(csarID).get(defID).replace("\\", "/") + "\""
                        + System.lineSeparator());
                }
            }

            if (this.mapElementIDToDefinitionsID.containsKey(csarID)) {
                log.append(System.lineSeparator() + "Print map of TOSCA element IDs to Definitions ID."
                    + System.lineSeparator());
                for (final QName eleID : this.mapElementIDToDefinitionsID.get(csarID).keySet()) {
                    log.append("   " + eleID + " is contained in Definitions \""
                        + this.mapElementIDToDefinitionsID.get(csarID).get(eleID) + "\"" + System.lineSeparator());
                }
            }

        }

        this.LOG.debug(log.toString());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeDefinitions(final CSARID csarID, final TDefinitions definitions) {
        if (csarID == null || definitions == null) {
            this.LOG.error("An error has occured.");
            return;
        }

        final QName reference = new QName(definitions.getTargetNamespace(), definitions.getId());
        this.LOG.debug("Store the Definitions \"" + reference + "\".");

        // TDefinitions + subclasses need to be serializable
        this.mapCSARIDToDefinitions.computeIfAbsent(csarID, id -> new ArrayList<>()).add(definitions);
        this.referenceMap.computeIfAbsent(csarID, id -> new HashMap<>())
                         .put(reference,
                              ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(definitions));
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
    public void storeDocument(final CSARID csarID, final QName documentID, final Document doc) {
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

        this.LOG.debug("Store new document reference for CSAR \"" + csarID + "\" the reference \"" + documentID
            + "\".");

        documentMap.putIfAbsent(csarID, new HashMap<>());
        if (this.documentMap.get(csarID).containsKey(documentID)) {
            this.LOG.debug("The reference with the QName \"" + documentID.toString()
                + "\" is already stored for the CSAR \"" + csarID + "\".");
        } else {
            this.documentMap.get(csarID).put(documentID, doc);
            this.LOG.debug("Storing of Document \"" + documentID.toString() + "\" completed.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeExportedInterface(final CSARID csarID, final QName serviceTemplateID,
                                       final TExportedInterface iface) {
        this.csarIDToExportedInterface.computeIfAbsent(csarID, id -> new HashMap<>())
                                      .computeIfAbsent(serviceTemplateID, id -> new ArrayList<>()).add(iface);
    }

    /**
     * Converts to DOM Document and stores a list of WSDL files for a certain CSAR.
     *
     * @param csarID
     * @param listOfWSDL
     * @return true for success, false for error
     */
    public boolean storeListOfWSDLForCSAR(final CSARID csarID, final List<Document> listOfWSDL) {
        return this.csarIDToWSDLDocuments.computeIfAbsent(csarID, id -> new ArrayList<>()).addAll(listOfWSDL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storePlanAsynchronousBoolean(final CSARID csarID, final QName planID, final boolean checkAsynchronous) {
        this.csarIDToPlanIDToSynchronousBoolean.putIfAbsent(csarID, new HashMap<>());

        Map<QName, Boolean> planToAsync = this.csarIDToPlanIDToSynchronousBoolean.get(csarID);
        if (null == planToAsync.get(planID)) {
            planToAsync.put(planID, checkAsynchronous);
        } else {
            this.LOG.error("For the CSAR " + csarID + " and plan " + planID
                + " is already stored wheter it is a synchronous or an asynchronous plan.");
        }
    }

    public void storePlanIDForCSARAndServiceTemplate(final CSARID csarID, final QName serviceTemplateID,
                                                     final QName planID) {
        this.csarIDToServiceTemplateIDToPlanID.computeIfAbsent(csarID, id -> new HashMap<>())
                                              .computeIfAbsent(serviceTemplateID, id -> new ArrayList<>()).add(planID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeReference(final CSARID csarID, final QName nodeID, final Node node) {
        Map<QName, Node> csarMap = this.referenceMap.computeIfAbsent(csarID, id -> new HashMap<>());
        if (csarMap.containsKey(nodeID)) {
            // node is stored already
            this.LOG.debug("The reference with the QName \"" + nodeID.toString()
                + "\" is already stored for the CSAR \"" + csarID + "\".");
        } else {
            // store this node
            csarMap.put(nodeID, node);
            this.LOG.debug("Storing of Node \"" + nodeID.toString() + "\" completed.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeServiceTemplateIDForCSARID(final QName serviceTemplateID, final CSARID csarID) {
        if (serviceTemplateID == null || csarID == null) {
            this.LOG.error("An error has occured.");
            return;
        }
        this.mapCSARIDToServiceTemplateIDs.computeIfAbsent(csarID, id -> new ArrayList<>()).add(serviceTemplateID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeDefinitionsLocation(final CSARID csarID, final QName defID, final String location) {
        mapDefinitionsIDToLocationString.putIfAbsent(csarID, new HashMap<>());
        if (this.mapDefinitionsIDToLocationString.get(csarID).containsKey(defID)) {
            this.LOG.warn("Overwrite the location for the Definitions \"" + defID + "\" in the CSAR \"" + csarID
                + "\".");
        }
        this.mapDefinitionsIDToLocationString.get(csarID).put(defID, location);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefinitionsLocation(final CSARID csarID, final QName defID) {
        Map<QName, String> definitionIdToLocation =
            mapDefinitionsIDToLocationString.getOrDefault(csarID, Collections.emptyMap());
        if (definitionIdToLocation.containsKey(defID)) {
            return definitionIdToLocation.get(defID);
        }
        this.LOG.error("No location found for the Definitions \"" + defID + "\" in CSAR \"" + csarID + "\".");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeContainingDefinitionsID(final CSARID csarID, final QName elementID, final QName definitionsID) {
        mapElementIDToDefinitionsID.putIfAbsent(csarID, new HashMap<>());
        if (this.mapElementIDToDefinitionsID.get(csarID).containsKey(elementID)) {
            this.LOG.warn("Overwrite the mapping for the element \"" + elementID + "\" in the CSAR \"" + csarID
                + "\".");
        }
        this.mapElementIDToDefinitionsID.get(csarID).put(elementID, definitionsID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getContainingDefinitionsID(final CSARID csarID, final QName elementID) {
        Map<QName, QName> elementToDefinitions =
            mapElementIDToDefinitionsID.getOrDefault(csarID, Collections.emptyMap());
        if (elementToDefinitions.containsKey(elementID)) {
            return elementToDefinitions.get(elementID);
        }
        this.LOG.error("No Definitions ID found for the element \"" + elementID + "\" in CSAR \"" + csarID + "\".");
        return null;
    }

    protected void bindIXMLSerializerService(final IXMLSerializerService service) {
        if (service == null) {
            this.LOG.error("Service IXMLSerializerService is null.");
        } else {
            this.LOG.debug("Bind of the IXMLSerializerService.");
            ToscaReferenceMapper.xmlSerializerService = service;
        }
    }

    protected void unbindIXMLSerializerService(final IXMLSerializerService service) {
        this.LOG.debug("Unbind of the IXMLSerializerService.");
        ToscaReferenceMapper.xmlSerializerService = null;
    }

    @Override
    public void storePlanInputMessageID(final CSARID csarID, final QName planID, final QName messageID) {
        Map<QName, QName> planToMessage =
            this.mapCSARIDToPlanIDToInputMessageID.computeIfAbsent(csarID, id -> new HashMap<>());
        if (null != planToMessage.get(planID)) {
            this.LOG.error("There is already a message ID stored for CSAR {} and Plan {}", csarID, planID);
        } else {
            planToMessage.put(planID, messageID);
        }

    }

    @Override
    public QName getPlanInputMessageID(final CSARID csarID, final QName planID) {
        return this.mapCSARIDToPlanIDToInputMessageID.getOrDefault(csarID, Collections.emptyMap()).getOrDefault(planID,
                                                                                                                null);
    }

    @Override
    public void storeServiceTemplateBoundsPlan(final CSARID csarID, final QName serviceTemplateID,
                                               final String interfaceName, final String opName, final QName planID) {
        mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.computeIfAbsent(csarID, id -> new HashMap<>())
                                                                .computeIfAbsent(serviceTemplateID,
                                                                                 id -> new HashMap<>())
                                                                .computeIfAbsent(interfaceName, name -> new HashMap<>())
                                                                .put(opName, planID);
    }

    @Override
    public String getIntferaceNameOfPlan(final CSARID csarID, final QName planID) {
        return mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.getOrDefault(csarID, Collections.emptyMap())
                                                                       .values().stream()
                                                                       .flatMap(serviceTemplate -> serviceTemplate.entrySet()
                                                                                                                  .stream())
                                                                       .filter(entry -> entry.getValue().values()
                                                                                             .stream()
                                                                                             .anyMatch(planID::equals))
                                                                       .map(Map.Entry::getKey).findFirst().orElse(null);
    }

    @Override
    public String getIntferaceNameOfPlan(final CSARID csarID, final QName serviceTemplateID, final QName planID) {
        return mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.getOrDefault(csarID, Collections.emptyMap())
                                                                       .getOrDefault(serviceTemplateID,
                                                                                     Collections.emptyMap())
                                                                       .entrySet().stream()
                                                                       .filter(entry -> entry.getValue().values()
                                                                                             .stream()
                                                                                             .anyMatch(planID::equals))
                                                                       .map(Map.Entry::getKey).findFirst().orElse(null);
    }

    @Override
    public String getOperationNameOfPlan(final CSARID csarID, final QName planID) {
        return mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.getOrDefault(csarID, Collections.emptyMap())
                                                                       .values().stream()
                                                                       .flatMap(interfaceToOpToPlan -> interfaceToOpToPlan.values()
                                                                                                                          .stream())
                                                                       .flatMap(opToPlan -> opToPlan.entrySet()
                                                                                                    .stream())
                                                                       .filter(entry -> entry.getValue().equals(planID))
                                                                       .map(Map.Entry::getKey).findFirst().orElse(null);
    }

    @Override
    public List<String> getBoundaryInterfacesOfCSAR(final CSARID csarID) {
        return mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.getOrDefault(csarID, Collections.emptyMap())
                                                                       .values().stream()
                                                                       .flatMap(interfaceToStuff -> interfaceToStuff.keySet()
                                                                                                                    .stream())
                                                                       .collect(Collectors.toList());
    }

    @Override
    public List<String> getBoundaryInterfacesOfServiceTemplate(final CSARID csarID, final QName serviceTemplateID) {
        return mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.getOrDefault(csarID, Collections.emptyMap())
                                                                       .getOrDefault(serviceTemplateID,
                                                                                     Collections.emptyMap())
                                                                       .keySet().stream().collect(Collectors.toList());
    }

    @Override
    public List<String> getBoundaryOperationsOfCSARInterface(final CSARID csarID, final QName serviceTemplateID,
                                                             final String intName) {
        return mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.getOrDefault(csarID, Collections.emptyMap())
                                                                       .getOrDefault(serviceTemplateID,
                                                                                     Collections.emptyMap())
                                                                       .getOrDefault(intName, Collections.emptyMap())
                                                                       .keySet().stream().collect(Collectors.toList());
    }

    @Override
    public QName getBoundaryPlanOfCSARInterface(final CSARID csarID, final String intName, final String opName) {

        final Map<QName, List<TExportedInterface>> stToIntfs =
            this.csarIDToExportedInterface.getOrDefault(csarID, Collections.emptyMap());

        for (final QName serviceTemplate : stToIntfs.keySet()) {
            Optional<QName> planCandidate =
                stToIntfs.get(serviceTemplate).stream().filter(exported -> exported.getName().equals(intName))
                         .flatMap(exported -> exported.getOperation().stream())
                         .filter(op -> op.getName().equals(opName)).findFirst()
                         .map(op -> new QName(serviceTemplate.getNamespaceURI(),
                             ((TPlan) op.getPlan().getPlanRef()).getId()));
            if (planCandidate.isPresent()) {
                return planCandidate.get();
            }
        }
        return null;
    }

    @Override
    public String getNamespaceOfPlan(final CSARID csarID, final String planID) {
        return mapCSARIDToPlanNameToNamespace.getOrDefault(csarID, Collections.emptyMap()).getOrDefault(planID, null);
    }

    @Override
    public void storeNamespaceOfPlan(final CSARID csarID, final String planID, final String namespace) {
        this.mapCSARIDToPlanNameToNamespace.computeIfAbsent(csarID, id -> new HashMap<>()).put(planID, namespace);
    }

    @Override
    public void storeNodeTemplateIDForServiceTemplateAndCSAR(final CSARID csarID, final QName serviceTemplateID,
                                                             final String id) {
        final List<String> list =
            this.mapCSARIDToServiceTemplateQNameToNodeTemplateID.computeIfAbsent(csarID, i -> new HashMap<>())
                                                                .computeIfAbsent(serviceTemplateID,
                                                                                 i -> new ArrayList<>());
        if (!list.contains(id)) {
            list.add(id);
        }
    }

    @Override
    public void storeRelationshipTemplateIDForServiceTemplateANdCSAR(final CSARID csarId, final QName serviceTemplateID,
                                                                     final String id) {
        final List<String> list =
            mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.computeIfAbsent(csarId, i -> new HashMap<>())
                                                                   .computeIfAbsent(serviceTemplateID,
                                                                                    i -> new ArrayList<>());
        if (!list.contains(id)) {
            list.add(id);
        }
    }

    @Override
    public Map<QName, List<String>> getServiceTemplatesAndNodeTemplatesInCSAR(final CSARID csarID) {
        return this.mapCSARIDToServiceTemplateQNameToNodeTemplateID.get(csarID);
    }

    @Override
    public Map<QName, List<String>> getServiceTemplate2RelationshipTemplateMap(final CSARID csarID) {
        return this.mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.get(csarID);
    }

    @Override
    public void storeServiceTemplateBoundsPropertiesInformation(final CSARID csarID, final QName serviceTemplateID,
                                                                final String propertiesContent,
                                                                final PropertyMappings propertyMappings) {
        this.serviceTemplatePropertiesContent.computeIfAbsent(csarID, i -> new HashMap<>()).put(serviceTemplateID,
                                                                                                propertiesContent);
        this.serviceTemplatePropertyMappings.computeIfAbsent(csarID, i -> new HashMap<>()).put(serviceTemplateID,
                                                                                               propertyMappings);
    }

    @Override
    public String getServiceTemplateBoundsPropertiesContent(final CSARID csarID, final QName serviceTemplateID) {
        return this.serviceTemplatePropertiesContent.getOrDefault(csarID, Collections.emptyMap())
                                                    .get(serviceTemplateID);
    }

    @Override
    public List<String> getServiceTemplateBoundsPropertiesContent(final CSARID csarID) {
        return serviceTemplatePropertiesContent.getOrDefault(csarID, Collections.emptyMap()).values().stream()
                                               .collect(Collectors.toList());
    }

    @Override
    public PropertyMappings getServiceTemplateBoundsPropertyMappings(final CSARID csarID,
                                                                     final QName serviceTemplateID) {
        return serviceTemplatePropertyMappings.getOrDefault(csarID, Collections.emptyMap()).get(serviceTemplateID);
    }

    @Override
    public List<PropertyMappings> getServiceTemplateBoundsPropertyMappings(final CSARID csarID) {
        return serviceTemplatePropertyMappings.getOrDefault(csarID, Collections.emptyMap()).values().stream()
                                              .collect(Collectors.toList());
    }

    @Override
    public List<TPropertyMapping> getPropertyMappings(final CSARID id, final QName serviceTemplate) {
        final PropertyMappings propertyMappings = this.getServiceTemplateBoundsPropertyMappings(id, serviceTemplate);
        if (propertyMappings == null) {
            this.LOG.info("There are no Property Mappings for CSAR \"{}\"", id);
            return null;
        }
        return propertyMappings.getPropertyMapping();
    }

    @Override
    public void storeRelationshipTemplateIDForServiceTemplateAndCSAR(final CSARID csarID, final QName serviceTemplateID,
                                                                     final String id) {
        final List<String> list =
            mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.computeIfAbsent(csarID, i -> new HashMap<>())
                                                                   .computeIfAbsent(serviceTemplateID,
                                                                                    i -> new ArrayList<>());
        if (!list.contains(id)) {
            list.add(id);
        }

    }

    @Override
    public Map<QName, List<String>> getServiceTemplatesAndRelationshipTemplatesInCSAR(final CSARID csarID) {
        return this.mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.get(csarID);
    }
}
