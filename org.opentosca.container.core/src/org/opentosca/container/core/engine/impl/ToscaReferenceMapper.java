package org.opentosca.container.core.engine.impl;

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

import org.opentosca.container.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.opentosca.container.core.mapping.CSARIDToDefinitionsMap;
import org.opentosca.container.core.mapping.CSARIDToServiceTemplateIDsMap;
import org.opentosca.container.core.mapping.CsarIDToPlanTypeToPlanNameToPlan;
import org.opentosca.container.core.mapping.CsarIDToPolicies;
import org.opentosca.container.core.mapping.CsarIDToServiceTemplateIDToPlanID;
import org.opentosca.container.core.mapping.CsarIDToWSDLDocuments;
import org.opentosca.container.core.mapping.DocumentMap;
import org.opentosca.container.core.mapping.MapQNameNode;
import org.opentosca.container.core.mapping.ReferenceMap;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.model.TBoundaryDefinitions;
import org.opentosca.container.core.tosca.model.TBoundaryDefinitions.Policies;
import org.opentosca.container.core.tosca.model.TBoundaryDefinitions.Properties.PropertyMappings;
import org.opentosca.container.core.tosca.model.TDefinitions;
import org.opentosca.container.core.tosca.model.TExportedInterface;
import org.opentosca.container.core.tosca.model.TExportedOperation;
import org.opentosca.container.core.tosca.model.TPlan;
import org.opentosca.container.core.tosca.model.TPolicy;
import org.opentosca.container.core.tosca.model.TPropertyMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ToscaReferenceMapper implements IToscaReferenceMapper {

    // services
    private static IXMLSerializerService xmlSerializerService;

    // logger
    private final Logger LOG = LoggerFactory.getLogger(ToscaReferenceMapper.class);

    // internal data structures
    private ReferenceMap referenceMap;
    private DocumentMap documentMap;
    private CSARIDToDefinitionsMap mapCSARIDToDefinitions;
    private CSARIDToServiceTemplateIDsMap mapCSARIDToServiceTemplateIDs;
    private CsarIDToPlanTypeToPlanNameToPlan csarIDToPlanTypeToIntegerToPlan;
    private CsarIDToWSDLDocuments csarIDToWSDLDocuments;
    private CsarIDToServiceTemplateIDToPlanID csarIDToServiceTemplateIDToPlanID;
    private final Map<CSARID, Map<QName, Boolean>> csarIDToPlanIDToSynchronousBoolean = new HashMap<>();
    private final Map<CSARID, Map<QName, List<TExportedInterface>>> csarIDToExportedInterface = new HashMap<>();
    private final CsarIDToPolicies csarIDToPolicies = new CsarIDToPolicies();
    private final Map<CSARID, Map<QName, String>> mapDefinitionsIDToLocationString = new HashMap<>();
    private final Map<CSARID, Map<QName, QName>> mapElementIDToDefinitionsID = new HashMap<>();
    private final Map<CSARID, Map<QName, QName>> mapCSARIDToPlanIDToInputMessageID = new HashMap<>();

    // private Map<CSARID, Map<QName, Map<QName, String>>>
    // mapCsarIdToServiceTemplateIdToPlanIdToInterfaceName = new HashMap<CSARID,
    // Map<QName, Map<QName, String>>>();
    // private Map<CSARID, Map<QName, Map<QName, String>>>
    // mapCSARIDToServiceTemplateIdToPlanIDToOperationName = new HashMap<CSARID,
    // Map<QName, Map<QName, String>>>();
    private final Map<CSARID, Map<QName, Map<String, Map<String, QName>>>> mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan =
        new HashMap<>();

    private final Map<CSARID, Map<String, String>> mapCSARIDToPlanNameToNamespace = new HashMap<>();

    private final Map<CSARID, Map<QName, List<String>>> mapCSARIDToServiceTemplateQNameToNodeTemplateID =
        new HashMap<>();

    private final Map<CSARID, Map<QName, List<String>>> mapCSARIDToServiceTemplateQNameToRelationshipTemplateID =
        new HashMap<>();

    private final Map<CSARID, Map<QName, String>> serviceTemplatePropertiesContent = new HashMap<>();
    private final Map<CSARID, Map<QName, PropertyMappings>> serviceTemplatePropertyMappings = new HashMap<>();


    public ToscaReferenceMapper() {
        setup();
    }

    /**
     * This function deletes all stored references of a certain CSAR.
     *
     * @param csarID
     * @return true for success, false for an error
     */
    public boolean clearCSARContent(final CSARID csarID) {

        this.LOG.debug("Delete the content of \"" + csarID + "\".");

        setup();
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

        if (containsCSARData(csarID)) {
            return false;
        }
        return true;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TBoundaryDefinitions.Policies getPolicies(final CSARID csarID, final QName templateID) {
        return this.csarIDToPolicies.get(csarID, templateID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeConsolidatedPolicies(final CSARID csarID, final QName templateID, final Policies policies) {
        this.csarIDToPolicies.put(csarID, templateID, policies);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsCSARData(final CSARID csarID) {
        setup();

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
        setup();
        if (containsCSARData(csarID)) {
            if (this.referenceMap.get(csarID).containsKey(reference)) {
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
        if (!this.csarIDToPlanTypeToIntegerToPlan.get(csarID).containsKey(PlanTypes.BUILD)) {
            this.csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.BUILD, new LinkedHashMap<QName, TPlan>());
            this.csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.TERMINATION,
                                                                 new LinkedHashMap<QName, TPlan>());
            this.csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.OTHERMANAGEMENT,
                                                                 new LinkedHashMap<QName, TPlan>());
            this.csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.APPLICATION,
                                                                 new LinkedHashMap<QName, TPlan>());
        }

        return this.csarIDToPlanTypeToIntegerToPlan.get(csarID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<QName> getDefinitionIDsOfCSAR(final CSARID csarID) {
        setup();

        final List<QName> listOfIDs = new ArrayList<>();

        if (this.mapCSARIDToDefinitions.containsKey(csarID)) {

            for (final TDefinitions def : this.mapCSARIDToDefinitions.get(csarID)) {

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
    public List<TDefinitions> getDefinitionsOfCSAR(final CSARID csarID) {
        setup();
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
    public Document getDOMDocumentForReference(final CSARID csarID, final QName reference) {
        setup();
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

        // nothing found
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<QName, List<TExportedInterface>> getExportedInterfacesOfCSAR(final CSARID csarID) {
        if (this.csarIDToExportedInterface.containsKey(csarID)) {
            return this.csarIDToExportedInterface.get(csarID);
        } else {
            return new HashMap<>();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getReferenceAsNode(final CSARID csarID, final QName nodeID) {
        setup();

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
        setup();

        this.LOG.debug("Lookup for the node \"" + nodeID + "\" inside of the CSAR \"" + csarID + "\".");

        if (this.referenceMap.containsKey(csarID)) {

            // The passed ID of a CSAR is found.
            // this.LOG.info("References for the CSAR with the QName \"" +
            // csarID.toString() + "\" found.");
            if (this.referenceMap.get(csarID).containsKey(nodeID)) {

                // The passed reference is found.
                // this.LOG.info("Reference with the QName \"" +
                // nodeID.toString() + "\" found.");
                final Node node = this.referenceMap.get(csarID).get(nodeID);

                if (AvailableToscaElements.getElementName(node.getLocalName()).getElementClass() != null) {
                    // The name of the node implies that is marshalable into one
                    // of the JAXB classes of TOSCA.
                    return ToscaReferenceMapper.xmlSerializerService.getXmlSerializer()
                                                                    .unmarshal(node,
                                                                               AvailableToscaElements.getElementName(node.getLocalName())
                                                                                                     .getElementClass());
                } else {
                    this.LOG.error("The reference is not a JAXB element.");
                }

            } else {
                this.LOG.error("Reference with the QName \"" + nodeID.toString() + "\" was not found for the CSAR \""
                    + csarID + "\".");
            }
        } else {
            this.LOG.error("No references for the CSAR with the QName \"" + csarID.toString() + "\" found.");
        }

        return null;
    }

    public List<Document> getListOfWSDLForCSAR(final CSARID csarID) {
        if (this.csarIDToWSDLDocuments.containsKey(csarID)) {
            return this.csarIDToWSDLDocuments.get(csarID);
        }
        return new ArrayList<>();
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

        if (!this.csarIDToPlanTypeToIntegerToPlan.containsKey(csarID)) {
            this.csarIDToPlanTypeToIntegerToPlan.put(csarID, new HashMap<PlanTypes, LinkedHashMap<QName, TPlan>>());
        }
        if (!this.csarIDToPlanTypeToIntegerToPlan.get(csarID).containsKey(PlanTypes.BUILD)) {
            this.csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.BUILD, new LinkedHashMap<QName, TPlan>());
            this.csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.TERMINATION,
                                                                 new LinkedHashMap<QName, TPlan>());
            this.csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.OTHERMANAGEMENT,
                                                                 new LinkedHashMap<QName, TPlan>());
            this.csarIDToPlanTypeToIntegerToPlan.get(csarID).put(PlanTypes.APPLICATION,
                                                                 new LinkedHashMap<QName, TPlan>());
        }

        for (final PlanTypes type : this.csarIDToPlanTypeToIntegerToPlan.get(csarID).keySet()) {
            for (final QName planName : this.csarIDToPlanTypeToIntegerToPlan.get(csarID).get(type).keySet()) {
                final TPlan plan = this.csarIDToPlanTypeToIntegerToPlan.get(csarID).get(type).get(planName);
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
    public List<QName> getServiceTemplateIDsContainedInCSAR(final CSARID csarID) {
        setup();
        return this.mapCSARIDToServiceTemplateIDs.get(csarID);
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
        setup();
        String string = "";
        final String ls = System.getProperty("line.separator");

        final StringBuilder builder = new StringBuilder();

        builder.append("Debug output of the stored data of the TOSCA resolving." + ls);

        if (null == this.referenceMap.keySet() || this.referenceMap.keySet().size() == 0) {
            builder.append("No data about CSARs stored yet.");
            this.LOG.debug(builder.toString());
            return;
        }

        for (final CSARID csarID : this.referenceMap.keySet()) {

            builder.append(ls + "Print all stored references of \"" + csarID + "\"." + ls);
            for (final QName ref : this.referenceMap.get(csarID).keySet()) {

                if (this.referenceMap.get(csarID).get(ref) == null) {
                    builder.append("ERROR: There is no data stored for the reference \"" + ref + "\"." + ls);
                } else {
                    string =
                        ToscaReferenceMapper.xmlSerializerService.getXmlSerializer()
                                                                 .docToString(this.referenceMap.get(csarID).get(ref),
                                                                              true);
                    string = string.replace(ls, "");
                    builder.append("       " + ref + " --> " + string + ls);
                }
            }

            if (this.documentMap.containsKey(csarID)) {
                builder.append(ls + "Print all stored documents of \"" + csarID + "\"." + ls);
                for (final QName ref : this.documentMap.get(csarID).keySet()) {

                    if (this.documentMap.get(csarID).get(ref) == null) {
                        builder.append("ERROR: There is no data stored for the reference \"" + ref + "\"." + ls);
                    } else {
                        string =
                            ToscaReferenceMapper.xmlSerializerService.getXmlSerializer()
                                                                     .docToString(this.documentMap.get(csarID).get(ref),
                                                                                  true);
                        string = string.replace(ls, "");
                        builder.append("       " + ref + " --> " + string + ls);
                    }
                }
            } else {
                builder.append("ERROR: There is no document stored for \"" + csarID + "\"." + ls);
            }

            builder.append(ls + "Print all due the BoundaryDefinitions defined PublicPlans" + ls);
            for (final PlanTypes type : this.csarIDToPlanTypeToIntegerToPlan.get(csarID).keySet()) {
                builder.append("   type: " + type + ls);
                for (final QName planID : this.csarIDToPlanTypeToIntegerToPlan.get(csarID).get(type).keySet()) {
                    final TPlan pp = this.csarIDToPlanTypeToIntegerToPlan.get(csarID).get(type).get(planID);
                    builder.append("      name: " + planID + " PublicPlan QName: " + pp.getId() + ls);
                }
            }

            builder.append(ls + "Print all stored plan IDs of this CSAR:" + ls);
            if (null != this.csarIDToServiceTemplateIDToPlanID.get(csarID)) {
                for (final QName serviceTemplateID : this.csarIDToServiceTemplateIDToPlanID.get(csarID).keySet()) {
                    for (final QName planID : this.csarIDToServiceTemplateIDToPlanID.get(csarID)
                                                                                    .get(serviceTemplateID)) {
                        builder.append("       Plan \"" + planID + "\" is inside of ServiceTemplate \""
                            + serviceTemplateID + "\"" + ls);
                    }
                }
            } else {
                builder.append("       nothing found ..." + ls);
            }

            builder.append(ls
                + "Print all stored informations about synchronous (false) and asynchronous (true) plans of CSAR \""
                + csarID + "\":" + ls);
            if (null != this.csarIDToPlanIDToSynchronousBoolean.get(csarID)) {
                for (final QName planID : this.csarIDToPlanIDToSynchronousBoolean.get(csarID).keySet()) {
                    builder.append("    Plan \"" + planID + "\" is asynchronous? "
                        + this.csarIDToPlanIDToSynchronousBoolean.get(csarID).get(planID) + ls);
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

            builder.append(ls + "Print list of the mapping of a TemplateID to Consolidated Policies" + ls);
            for (final QName templateID : this.csarIDToPolicies.getTemplateIDs(csarID)) {
                final TBoundaryDefinitions.Policies pols = this.csarIDToPolicies.get(csarID, templateID);
                builder.append("   " + templateID + " mapps to following policies." + ls);
                for (final TPolicy pol : pols.getPolicy()) {
                    builder.append("      policy name=\"" + pol.getName() + "\"");
                    try {
                        marshaller.marshal(pol, writer);
                        builder.append(" --> " + writer.toString().replaceAll("\\n|\\r", ""));
                    }
                    catch (final JAXBException e) {
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

            if (this.mapDefinitionsIDToLocationString.containsKey(csarID)) {
                builder.append(ls + "Print map of TOSCA Definitions locations." + ls);
                for (final QName defID : this.mapDefinitionsIDToLocationString.get(csarID).keySet()) {
                    builder.append("   " + defID + " is stored at \""
                        + this.mapDefinitionsIDToLocationString.get(csarID).get(defID).replace("\\", "/") + "\"" + ls);
                }
            }

            if (this.mapElementIDToDefinitionsID.containsKey(csarID)) {
                builder.append(ls + "Print map of TOSCA element IDs to Definitions ID." + ls);
                for (final QName eleID : this.mapElementIDToDefinitionsID.get(csarID).keySet()) {
                    builder.append("   " + eleID + " is contained in Definitions \""
                        + this.mapElementIDToDefinitionsID.get(csarID).get(eleID) + "\"" + ls);
                }
            }

        }

        this.LOG.debug(builder.toString());

    }

    /**
     * This method initializes the data structures in which the the DOM Nodes and Documents are stored
     * if not done already.
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
        if (null == this.csarIDToPlanTypeToIntegerToPlan) {
            this.csarIDToPlanTypeToIntegerToPlan = new CsarIDToPlanTypeToPlanNameToPlan();
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
    public void storeDefinitions(final CSARID csarID, final TDefinitions definitions) {

        setup();
        if (csarID != null && definitions != null) {

            final QName reference = new QName(definitions.getTargetNamespace(), definitions.getId());
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
            this.referenceMap.get(csarID)
                             .put(reference,
                                  ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(definitions));

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
    public void storeDocument(final CSARID csarID, final QName documentID, final Document doc) {

        setup();

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

        if (!this.documentMap.containsKey(csarID)) {
            this.documentMap.put(csarID, new HashMap<QName, Document>());
        }

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
        if (!this.csarIDToExportedInterface.containsKey(csarID)) {
            this.csarIDToExportedInterface.put(csarID, new HashMap<QName, List<TExportedInterface>>());
        }
        if (!this.csarIDToExportedInterface.get(csarID).containsKey(serviceTemplateID)) {
            this.csarIDToExportedInterface.get(csarID).put(serviceTemplateID, new ArrayList<TExportedInterface>());
        }
        this.csarIDToExportedInterface.get(csarID).get(serviceTemplateID).add(iface);
    }

    /**
     * Converts to DOM Document and stores a list of WSDL files for a certain CSAR.
     *
     * @param csarID
     * @param listOfWSDL
     * @return true for success, false for error
     */
    public boolean storeListOfWSDLForCSAR(final CSARID csarID, final List<Document> listOfWSDL) {

        if (!this.csarIDToWSDLDocuments.containsKey(csarID)) {
            this.csarIDToWSDLDocuments.put(csarID, new ArrayList<Document>());
        }

        for (final Document doc : listOfWSDL) {
            this.csarIDToWSDLDocuments.get(csarID).add(doc);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storePlanAsynchronousBoolean(final CSARID csarID, final QName planID, final boolean checkAsynchronous) {
        if (null == this.csarIDToPlanIDToSynchronousBoolean.get(csarID)) {
            this.csarIDToPlanIDToSynchronousBoolean.put(csarID, new HashMap<QName, Boolean>());
        }
        if (null == this.csarIDToPlanIDToSynchronousBoolean.get(csarID).get(planID)) {
            this.csarIDToPlanIDToSynchronousBoolean.get(csarID).put(planID, checkAsynchronous);
        } else {
            this.LOG.error("For the CSAR " + csarID + " and plan " + planID
                + " is already stored wheter it is a synchronous or an asynchronous plan.");
        }
    }

    public void storePlanIDForCSARAndServiceTemplate(final CSARID csarID, final QName serviceTemplateID,
                                                     final QName planID) {
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
    public void storeReference(final CSARID csarID, final QName nodeID, final Node node) {
        setup();

        MapQNameNode csarMap;

        if (this.referenceMap.containsKey(csarID)) {
            // CSARID is known
            csarMap = this.referenceMap.get(csarID);
            if (csarMap.containsKey(nodeID)) {
                // node is stored already
                this.LOG.debug("The reference with the QName \"" + nodeID.toString()
                    + "\" is already stored for the CSAR \"" + csarID + "\".");
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
    public void storeServiceTemplateIDForCSARID(final QName serviceTemplateID, final CSARID csarID) {
        setup();
        if (serviceTemplateID != null && csarID != null) {
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
    public void storeDefinitionsLocation(final CSARID csarID, final QName defID, final String location) {
        if (!this.mapDefinitionsIDToLocationString.containsKey(csarID)) {
            this.mapDefinitionsIDToLocationString.put(csarID, new HashMap<QName, String>());
        }
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
    public void storeContainingDefinitionsID(final CSARID csarID, final QName elementID, final QName definitionsID) {
        if (!this.mapElementIDToDefinitionsID.containsKey(csarID)) {
            this.mapElementIDToDefinitionsID.put(csarID, new HashMap<QName, QName>());
        }
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
        if (this.mapElementIDToDefinitionsID.containsKey(csarID)) {
            if (this.mapElementIDToDefinitionsID.get(csarID).containsKey(elementID)) {
                return this.mapElementIDToDefinitionsID.get(csarID).get(elementID);
            }
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
        if (!this.mapCSARIDToPlanIDToInputMessageID.containsKey(csarID)) {
            this.mapCSARIDToPlanIDToInputMessageID.put(csarID, new HashMap<QName, QName>());
        }
        if (null != this.mapCSARIDToPlanIDToInputMessageID.get(csarID).get(planID)) {
            this.LOG.error("There is already a message ID stored for CSAR {} and Plan {}", csarID, planID);
        } else {
            this.mapCSARIDToPlanIDToInputMessageID.get(csarID).put(planID, messageID);
        }

    }

    @Override
    public QName getPlanInputMessageID(final CSARID csarID, final QName planID) {
        try {
            return this.mapCSARIDToPlanIDToInputMessageID.get(csarID).get(planID);
        }
        catch (final NullPointerException e) {
            this.LOG.error("There is no message ID stored for CSAR {} and Plan {}", csarID, planID);
            return null;
        }
    }

    @Override
    public void storeServiceTemplateBoundsPlan(final CSARID csarID, final QName serviceTemplateID,
                                               final String interfaceName, final String opName, final QName planID) {

        if (null == this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID)) {
            this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.put(csarID,
                                                                              new HashMap<QName, Map<String, Map<String, QName>>>());
        }
        if (null == this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)) {
            this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID)
                                                                         .put(serviceTemplateID,
                                                                              new HashMap<String, Map<String, QName>>());
        }
        if (null == this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)
                                                                                 .get(interfaceName)) {
            this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)
                                                                         .put(interfaceName,
                                                                              new HashMap<String, QName>());
        }
        this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)
                                                                     .get(interfaceName).put(opName, planID);
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
    public String getIntferaceNameOfPlan(final CSARID csarID, final QName planID) {

        if (!this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.containsKey(csarID)) {
            return null;
        }
        for (final QName st : this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).keySet()) {
            for (final String intf : this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st)
                                                                                                  .keySet()) {
                for (final String op : this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st)
                                                                                                    .get(intf)
                                                                                                    .keySet()) {
                    if (this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st).get(intf)
                                                                                     .get(op).equals(planID)) {
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
    public String getIntferaceNameOfPlan(final CSARID csarID, final QName serviceTemplateID, final QName planID) {

        if (!this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.containsKey(csarID)) {
            return null;
        }
        if (!this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).containsKey(serviceTemplateID)) {
            return null;
        }

        for (final String intf : this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID)
                                                                                              .get(serviceTemplateID)
                                                                                              .keySet()) {
            for (final String op : this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID)
                                                                                                .get(serviceTemplateID)
                                                                                                .get(intf).keySet()) {
                if (this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)
                                                                                 .get(intf).get(op).equals(planID)) {
                    return intf;
                }
            }
        }
        return null;
    }

    @Override
    public String getOperationNameOfPlan(final CSARID csarID, final QName planID) {
        if (!this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.containsKey(csarID)) {
            return null;
        }
        for (final QName st : this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).keySet()) {
            for (final String intf : this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st)
                                                                                                  .keySet()) {
                for (final String op : this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st)
                                                                                                    .get(intf)
                                                                                                    .keySet()) {
                    if (this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st).get(intf)
                                                                                     .get(op).equals(planID)) {
                        return op;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<String> getBoundaryInterfacesOfCSAR(final CSARID csarID) {
        final List<String> list = new ArrayList<>();

        if (!this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.containsKey(csarID)) {
            return null;
        }
        for (final QName st : this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).keySet()) {
            for (final String intf : this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(st)
                                                                                                  .keySet()) {
                list.add(intf);
            }
        }

        return list;
    }

    @Override
    public List<String> getBoundaryInterfacesOfServiceTemplate(final CSARID csarID, final QName serviceTemplateID) {
        final List<String> list = new ArrayList<>();

        if (!this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.containsKey(csarID)) {
            return null;
        }
        if (!this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).containsKey(serviceTemplateID)) {
            return null;
        }

        for (final String intf : this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID)
                                                                                              .get(serviceTemplateID)
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
    public List<String> getBoundaryOperationsOfCSARInterface(final CSARID csarID, final QName serviceTemplateID,
                                                             final String intName) {
        final List<String> list = new ArrayList<>();

        if (!this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.containsKey(csarID)) {
            return list;
        }
        if (!this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).containsKey(serviceTemplateID)) {
            return list;
        }
        if (!this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID).get(serviceTemplateID)
                                                                          .containsKey(intName)) {
            return list;
        }

        for (final String op : this.mapCSARIDToServiceTemplateIdToInterfaceToOperationToPlan.get(csarID)
                                                                                            .get(serviceTemplateID)
                                                                                            .get(intName).keySet()) {
            list.add(op);
        }

        return list;
    }

    @Override
    public QName getBoundaryPlanOfCSARInterface(final CSARID csarID, final String intName, final String opName) {

        final Map<QName, List<TExportedInterface>> stToIntfs = this.csarIDToExportedInterface.get(csarID);

        if (null != stToIntfs) {
            for (final QName serviceTemplate : stToIntfs.keySet()) {
                for (final TExportedInterface intf : stToIntfs.get(serviceTemplate)) {
                    if (intf.getName().equals(intName)) {
                        for (final TExportedOperation op : intf.getOperation()) {
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
    public String getNamespaceOfPlan(final CSARID csarID, final String planID) {
        if (null != this.mapCSARIDToPlanNameToNamespace.get(csarID)) {
            return this.mapCSARIDToPlanNameToNamespace.get(csarID).get(planID);
        }
        return null;
    }

    @Override
    public void storeNamespaceOfPlan(final CSARID csarID, final String planID, final String namespace) {
        if (!this.mapCSARIDToPlanNameToNamespace.containsKey(csarID)) {
            this.mapCSARIDToPlanNameToNamespace.put(csarID, new HashMap<String, String>());
        }
        this.mapCSARIDToPlanNameToNamespace.get(csarID).put(planID, namespace);
    }

    @Override
    public void storeNodeTemplateIDForServiceTemplateAndCSAR(final CSARID csarID, final QName serviceTemplateID,
                                                             final String id) {
        if (!this.mapCSARIDToServiceTemplateQNameToNodeTemplateID.containsKey(csarID)) {
            this.mapCSARIDToServiceTemplateQNameToNodeTemplateID.put(csarID, new HashMap<QName, List<String>>());
        }
        final Map<QName, List<String>> map = this.mapCSARIDToServiceTemplateQNameToNodeTemplateID.get(csarID);
        if (!map.containsKey(serviceTemplateID)) {
            map.put(serviceTemplateID, new ArrayList<String>());
        }
        final List<String> list = map.get(serviceTemplateID);
        if (!list.contains(id)) {
            list.add(id);
        }
    }

    @Override
    public void storeRelationshipTemplateIDForServiceTemplateANdCSAR(final CSARID csarId, final QName serviceTemplateID,
                                                                     final String id) {
        if (!this.mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.containsKey(csarId)) {
            this.mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.put(csarId,
                                                                             new HashMap<QName, List<String>>());
        }
        final Map<QName, List<String>> map = this.mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.get(csarId);
        if (!map.containsKey(serviceTemplateID)) {
            map.put(serviceTemplateID, new ArrayList<String>());
        }
        final List<String> list = map.get(serviceTemplateID);
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
        if (null == this.serviceTemplatePropertiesContent.get(csarID)) {
            this.serviceTemplatePropertiesContent.put(csarID, new HashMap<QName, String>());
        }
        this.serviceTemplatePropertiesContent.get(csarID).put(serviceTemplateID, propertiesContent);
        if (null == this.serviceTemplatePropertyMappings.get(csarID)) {
            this.serviceTemplatePropertyMappings.put(csarID, new HashMap<QName, PropertyMappings>());
        }
        this.serviceTemplatePropertyMappings.get(csarID).put(serviceTemplateID, propertyMappings);
    }

    @Override
    public String getServiceTemplateBoundsPropertiesContent(final CSARID csarID, final QName serviceTemplateID) {
        final Map<QName, String> properties = this.serviceTemplatePropertiesContent.get(csarID);
        if (properties != null) {
            return properties.get(serviceTemplateID);
        }
        return null;
    }

    @Override
    public List<String> getServiceTemplateBoundsPropertiesContent(final CSARID csarID) {
        final List<String> ret = new ArrayList<>();
        for (final QName st : this.serviceTemplatePropertiesContent.get(csarID).keySet()) {
            ret.add(this.serviceTemplatePropertiesContent.get(csarID).get(st));
        }
        return ret;
    }

    @Override
    public PropertyMappings getServiceTemplateBoundsPropertyMappings(final CSARID csarID,
                                                                     final QName serviceTemplateID) {
        final Map<QName, PropertyMappings> properties = this.serviceTemplatePropertyMappings.get(csarID);
        if (properties != null) {
            return properties.get(serviceTemplateID);
        }
        return null;
    }

    @Override
    public List<PropertyMappings> getServiceTemplateBoundsPropertyMappings(final CSARID csarID) {
        final List<PropertyMappings> ret = new ArrayList<>();

        for (final QName st : this.serviceTemplatePropertyMappings.get(csarID).keySet()) {
            ret.add(this.serviceTemplatePropertyMappings.get(csarID).get(st));
        }

        return ret;
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
        if (!this.mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.containsKey(csarID)) {
            this.mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.put(csarID,
                                                                             new HashMap<QName, List<String>>());
        }
        final Map<QName, List<String>> map = this.mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.get(csarID);
        if (!map.containsKey(serviceTemplateID)) {
            map.put(serviceTemplateID, new ArrayList<String>());
        }
        final List<String> list = map.get(serviceTemplateID);
        if (!list.contains(id)) {
            list.add(id);
        }

    }

    @Override
    public Map<QName, List<String>> getServiceTemplatesAndRelationshipTemplatesInCSAR(final CSARID csarID) {
        return this.mapCSARIDToServiceTemplateQNameToRelationshipTemplateID.get(csarID);
    }
}
