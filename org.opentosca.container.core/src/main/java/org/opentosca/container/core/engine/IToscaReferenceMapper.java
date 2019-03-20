package org.opentosca.container.core.engine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties.PropertyMappings;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPropertyMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This interface provides the functionality of access to resolved data. This data is dependent to a
 * certain CSAR. Each data set is referenced by the QName of the CSAR nesting the data and the QName
 * with which it is referenced inside of a TOSCA file.
 *
 * @deprecated Should be fully replaced by accessing the Winery repository representation of a CSAR
 */
@Deprecated
public interface IToscaReferenceMapper {

  /**
   * This method stores a DOM document and its QName for a certain CSAR.
   *
   * @param csarID     ID of the CSAR in which the document is referenced.
   * @param documentID ID of the document.
   * @param doc        DOM document which shall be stored.
   * @return true means no error, false means one or more errors
   */
  public abstract void storeDocument(CSARID csarID, QName documentID, Document doc);

  /**
   * This method stores a DOM node and its QName for a certain CSAR.
   *
   * @param csarID ID of the CSAR in which the document is referenced.
   * @param nodeID ID of the node.
   * @param node   DOM node which shall be stored.
   * @return true means no error, false means one or more errors
   */
  public abstract void storeReference(CSARID csarID, QName nodeID, Node node);

  /**
   * This method stores a ServiceTemplateID for a specific CSAR.
   *
   * @param serviceTemplateID the QName of the ID of a ServiceTemplate.
   * @param csarID            the CSARID of a specific CSAR.
   */
  public void storeServiceTemplateIDForCSARID(QName serviceTemplateID, CSARID csarID);

  /**
   * This method returns the stored list of IDs of ServiceTemplates contained in a specific CSAR.
   *
   * @param csarID the CSARID of the specific CSAR.
   * @return a list of the IDs of ServiceTemplates which are contained in a specific CSAR.
   */
  public List<QName> getServiceTemplateIDsContainedInCSAR(CSARID csarID);

  /**
   * This method returns a list of Definitions contained in a specific CSAR.
   *
   * @param csarID the CSARID of the specific CSAR.
   * @return a list of the Definitions which are contained in a specific CSAR.
   */
  public List<TDefinitions> getDefinitionsOfCSAR(CSARID csarID);

  /**
   * This method returns a list of IDs of Definitions contained in a specific CSAR.
   *
   * @param csarID the CSARID of the specific CSAR.
   * @return a list of the IDs of Definitions which are contained in a specific CSAR.
   */
  public List<QName> getDefinitionIDsOfCSAR(CSARID csarID);

  /**
   * Returns the requested Document. The Document is identified via the QName of a CSAR and the QName
   * of an element inside of it. If the object is found, it is returned as DOM Document. This method
   * is used if you want to get the whole document in which a certain element is nested.
   *
   * @param csarID    of the CSAR in which the demanded document shall be.
   * @param reference to the demanded document.
   * @return The DOM Document if it is found. Null if it is not found.
   */
  public abstract Document getDOMDocumentForReference(CSARID csarID, QName reference);

  /**
   * Returns the requested Node object. The object is identified via the QName of a CSAR and its own
   * QName. If the object is found, it is returned as a DOM Node object.
   *
   * @param csarID ID of the CSAR in which the node is referenced.
   * @param nodeID ID of the node.
   * @return DOM Node object or null in case of failure
   */
  public abstract Object getReferenceAsNode(CSARID csarID, QName nodeID);

  /**
   * Returns the requested JAXB object. The object is identified via the QName of a CSAR and its own
   * QName. If the object is found, it is serialized and returned as a JAXB object of the TOSCA model.
   *
   * @param csarID ID of the CSAR in which the node is referenced.
   * @param nodeID ID of the node.
   * @return JAXB object of the type according to org.opentosca.model.tosca or null in case of failure
   */
  public abstract Object getJAXBReference(CSARID csarID, QName nodeID);

  /**
   * Checks if the ToscaReferenceMapper has stored data about a certain CSAR.
   *
   * @param csarID to identify the certain CSAR.
   * @return true if there is data stored, false if not
   */
  public abstract boolean containsCSARData(CSARID csarID);

  /**
   * Checks if the ToscaReferenceMapper has stored a specific reference for a CSAR.
   *
   * @param csarID    to identify the certain CSAR.
   * @param reference the specific reference
   * @return true if there is data stored, false if not
   */
  public boolean containsReferenceInsideCSAR(CSARID csarID, QName reference);

  /**
   * Stores a Definitions for a specific CSAR.
   *
   * @param csarID      the ID of the CSAR.
   * @param definitions the Definitions.
   */
  public void storeDefinitions(CSARID csarID, TDefinitions definitions);

  /**
   * Stores an exported interface for a CSAR.
   *
   * @param csarID            the ID of the CSAR.
   * @param serviceTemplateID the ID of the ServiceTemplate for which the interface is
   * @param iface             the exported interface.
   */
  public void storeExportedInterface(CSARID csarID, QName serviceTemplateID, TExportedInterface iface);

  /**
   * Returns the list of exported interfaces of a CSAR.
   *
   * @param csarID the ID of the CSAR.
   * @return a list of the exported interfaces of the given CSAR.
   */
  public Map<QName, List<TExportedInterface>> getExportedInterfacesOfCSAR(CSARID csarID);

  /**
   * Returns the persistence which maps a CSARID to a ServiceTemplateID to a PlanID.
   *
   * @return the map.
   */
  public Map<CSARID, Map<QName, List<QName>>> getMapCsarIDToServiceTemplateIDToPlanID();

  /**
   * Returns a map of PlanTypes to a map of plan ids to plan for a certain CSAR.
   *
   * @param csarID
   * @return map
   */
  public Map<PlanTypes, LinkedHashMap<QName, TPlan>> getCSARIDToPlans(CSARID csarID);

  /**
   * This method stores whether the plan is synchronous or asynchronous.
   *
   * @param csarID            The CSARID which owns the plan.
   * @param planID            The QName pointing to the plan.
   * @param checkAsynchronous false for synchronous, true for asynchronous
   */
  public void storePlanAsynchronousBoolean(CSARID csarID, QName planID, boolean checkAsynchronous);

  /**
   * This method shows if a plan is synchronous or asynchronous.
   *
   * @param csarID The CSARID which owns the plan.
   * @param planID The QName pointing to the plan.
   * @return false for synchronous plan, true for asynchronous plan, null if no informations are
   * stored
   */
  public Boolean isPlanAsynchronous(CSARID csarID, QName planID);

  /**
   * Returns a PublicPlan if found.
   *
   * @param csarID
   * @param planID
   * @return the PublicPlan if found, null instead.
   */
  public TPlan getPlanForCSARIDAndPlanID(CSARID csarID, QName planID);

  /**
   * Debug output.
   */
  public void printStoredData();

  /**
   * Returns the requested Consolidated Policies.
   *
   * @param csarID     The CSARID.
   * @param templateID The QName pointing to the template.
   * @return the Consolidated Policies or null of none are found.
   */
  public TPolicies getPolicies(CSARID csarID, QName templateID);

  /**
   * Puts the Consolidated Policies of a ServiceTemplate or NodeTemplate into the storage.
   *
   * @param csarID     the CSARID
   * @param templateID the QName of a ServiceTemplate or NodeTemplate
   * @param policies   the ConsolidatedPolicies object
   */
  public void storeConsolidatedPolicies(CSARID csarID, QName templateID, TPolicies policies);

  /**
   * Stores the location inside of a CSAR for a Definitions file.
   *
   * @param defID
   * @param location
   */
  public void storeDefinitionsLocation(CSARID csarID, QName defID, String location);

  /**
   * Returns the location of a Definitions file for a given DefinitionsID.
   *
   * @param defID
   * @return String location or null in case of error like not found
   */
  public String getDefinitionsLocation(CSARID csarID, QName defID);

  /**
   * Stores the the Definitions ID for an element inside a CSAR.
   *
   * @param csarID        which CSAR is containing the stored Definitions ID
   * @param elementID     which element is inside the stored Definitions ID
   * @param definitionsID the Definitions ID
   */
  public void storeContainingDefinitionsID(CSARID csarID, QName elementID, QName definitionsID);

  /**
   * Returns the Definitions ID for a Definitions containing a element with the given elementID inside
   * a CSAR.
   *
   * @param csarID    which CSAR is containing the stored Definitions ID
   * @param elementID which element is inside the stored Definitions ID
   * @return the Definitions ID or null in case of error like not found
   */
  public QName getContainingDefinitionsID(CSARID csarID, QName elementID);

  /**
   * Stores the message element id of a plan, parsed from a WSDL.
   *
   * @param csarID
   * @param planID
   */
  public void storePlanInputMessageID(CSARID csarID, QName planID, QName messageID);

  /**
   * Returns the message element id of a plan, parsed from a WSDL.
   *
   * @param csarID
   * @param planID
   * @return
   */
  public QName getPlanInputMessageID(CSARID csarID, QName planID);

  // public void storeOperationNameForPlan(CSARID csarID, QName
  // serviceTemplateID, QName planID, String interfaceName, String
  // operationName);

  public String getIntferaceNameOfPlan(CSARID csarID, QName planID);

  public String getIntferaceNameOfPlan(CSARID csarID, QName serviceTemplateID, QName planID);

  public String getOperationNameOfPlan(CSARID csarID, QName planID);

  public List<String> getBoundaryInterfacesOfCSAR(CSARID csarID);

  public List<String> getBoundaryInterfacesOfServiceTemplate(CSARID csarID, QName serviceTemplateID);

  public List<String> getBoundaryOperationsOfCSARInterface(CSARID csarID, QName serviceTemplateID, String intName);

  public QName getBoundaryPlanOfCSARInterface(CSARID csarID, String intName, String opName);

  public String getNamespaceOfPlan(CSARID csarID, String planID);

  public void storeNamespaceOfPlan(CSARID csarID, String planID, String namespace);

  public void storeNodeTemplateIDForServiceTemplateAndCSAR(CSARID csarID, QName serviceTemplateID, String id);

  public void storeRelationshipTemplateIDForServiceTemplateAndCSAR(CSARID csarID, QName serviceTemplateId, String id);

  public Map<QName, List<String>> getServiceTemplatesAndNodeTemplatesInCSAR(CSARID csarID);

  public Map<QName, List<String>> getServiceTemplatesAndRelationshipTemplatesInCSAR(CSARID csarID);

  public void storeServiceTemplateBoundsPropertiesInformation(CSARID csarID, QName serviceTemplateID,
                                                              String propertiesContent,
                                                              PropertyMappings propertyMappings);

  public String getServiceTemplateBoundsPropertiesContent(CSARID csarID, QName serviceTemplateID);

  public PropertyMappings getServiceTemplateBoundsPropertyMappings(CSARID csarID, QName serviceTemplateID);

  public List<String> getServiceTemplateBoundsPropertiesContent(CSARID csarID);

  public List<PropertyMappings> getServiceTemplateBoundsPropertyMappings(CSARID csarID);

  void storeServiceTemplateBoundsPlan(CSARID csarID, QName serviceTemplateID, String interfaceName, String opName,
                                      QName planID);

  List<TPropertyMapping> getPropertyMappings(CSARID id, QName serviceTemplate);

  // public void storeInterfaceNameForPlan(CSARID csarID, QName
  // serviceTemplateID, QName planID, String interfaceName);
  // public void setBoundaryInterfaceForCSARIDPlan(CSARID csarID, QName
  // serviceTemplateID, QName planID, String ifaceName);
  // public void setBoundaryOperationForCSARIDPlan(CSARID csarID, QName
  // serviceTemplateID, QName planID, String opName);

  public void storeRelationshipTemplateIDForServiceTemplateANdCSAR(final CSARID csarId, final QName serviceTemplateID,
                                                                   final String id);

  public Map<QName, List<String>> getServiceTemplate2RelationshipTemplateMap(final CSARID csarID);

}
