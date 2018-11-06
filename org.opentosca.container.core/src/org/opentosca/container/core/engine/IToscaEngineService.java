package org.opentosca.container.core.engine;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.model.TBoundaryDefinitions;
import org.opentosca.container.core.tosca.model.TPropertyConstraint;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This interface describes functionality of resolving the references inside of ServiceTemplates in
 * a passed CSAR. This is needed to provide the data via the ToscaReferenceMapper with which
 * components resolve references inside of TOSCA files and get the required objects.
 *
 * Another provided functionality is to consolidate data of NodeTemplate and NodeType objects. This
 * is done for better access to the required informations of one or multiple TOSCA documents. The
 * implementation is at org.opentosca.model.tosca.util.
 *
 * Last functionality is to provide the tool ToscaReferenceMapper. This object provides access to
 * referenced informations.
 *
 * It is used by <br>
 * org.opentosca.containerapi which needs the consolidated data and tools. <br>
 * org.opentosca.iaengine.service needs the consolidated data. <br>
 * org.opentosca.opentoscacontrol.service.impl which implements the control for the OpenTosca
 * Container.
 */
public interface IToscaEngineService {

    /**
     * This method returns the ToscaReferenceMapper.
     *
     * @return the ToscaReferenceMapper
     */
    public IToscaReferenceMapper getToscaReferenceMapper();

    /**
     * This method resolves the ServiceTemplates of the passed CSAR and stores the contained
     * references and their counterpart inside the ToscaImportMapper.
     *
     * @param csarID CSAR ID in which the ServiceTemplate is stored.
     * @return true for success, false for one or more errors
     */
    public boolean resolveDefinitions(CSARID csarID);

    /**
     * Returns all Node Types (including the given) inside the type hierarchy of the given Node Type
     *
     * @param csarID the CSAR to look in
     * @param nodeType the QName of a Node Type
     * @return a List of QNames denoting all Node Types inside the type hierarchy of the given Node
     *         Type
     */
    public List<QName> getNodeTypeHierachy(CSARID csarID, QName nodeType);

    /**
     * This method searches inside of certain ServiceTemplate of a CSAR for referenced NodeTypes. It
     * returns a list of QNames which point to NodeTypes referenced by NodeTemplates via derivation
     * or target by RelationshipTemplates via NodeTemplates.
     *
     * @param csarID of the CSAR containing the ServiceTemplate.
     * @param serviceTemplateID of the ServiceTemplate.
     * @return List of QNames which point to NodeTypes or a empty list if no NodeType is referenced.
     */
    public List<QName> getReferencedNodeTypesOfAServiceTemplate(CSARID csarID, QName serviceTemplateID);

    /**
     * This method checks if input parameter are specified for a given interface and operation of a
     * NodeType or RelationshipType.
     *
     * @param csarID of the CSAR containing the NodeType or RelationshipType.
     * @param typeID of the NodeType or RelationshipType to check.
     * @param interfaceName of the NodeType or RelationshipType to check.
     * @param operationName of the NodeType or RelationshipType check.
     * @return <code>true</code> if input parameter are specified. Otherwise <code>false</code>.
     */
    boolean hasOperationOfATypeSpecifiedInputParams(CSARID csarID, QName typeID, String interfaceName,
                                                    String operationName);

    /**
     * This method checks if output parameter are specified for a given interface and operation of a
     * NodeType or RelationshipType.
     *
     * @param csarID of the CSAR containing the NodeType or RelationshipType.
     * @param typeID of the NodeType or RelationshipType to check.
     * @param interfaceName of the NodeType or RelationshipType to check.
     * @param operationName of the NodeType or RelationshipType to check.
     * @return <code>true</code> if output parameter are specified. Otherwise <code>false</code>.
     */
    boolean hasOperationOfATypeSpecifiedOutputParams(CSARID csarID, QName typeID, String interfaceName,
                                                     String operationName);

    /**
     * Checks if specified operations is bound to sourceNode.
     *
     * @param csarID of the CSAR containing the NodeType.
     * @param relationshipTypeID of the RelationshipType to check.
     * @param interfaceName of the RelationshipType to check.
     * @param operationName of the RelationshipType to check.
     * @return <code>true</code> if operation is bound to sourceNode. Otherwise <code>false</code>.
     */
    boolean isOperationOfRelationshipBoundToSourceNode(CSARID csarID, QName relationshipTypeID, String interfaceName,
                                                       String operationName);

    /**
     * This method checks if the specified interface of a NodeType or RelationshipType contains the
     * specified operation.
     *
     * @param csarID of the CSAR containing the NodeType or RelationshiptType.
     * @param typeID of the NodeType or RelationshiptType to check.
     * @param interfaceName of the NodeType or RelationshiptType to check.
     * @param operationName of the NodeType or RelationshiptType to check.
     * @return <code>true</code> if interface contains the operation. Otherwise <code>false</code>.
     */
    boolean doesInterfaceOfTypeContainOperation(CSARID csarID, QName typeID, String interfaceName,
                                                String operationName);

    /**
     * This method searches all NodeTypeImplementations/RelationshipTypeImplementations for a
     * certain NodeType/RelationshipType inside of a given CSAR.
     *
     * @param csarID of the CSAR containing the NodeTypeImplementations or
     *        RelationshipTypeImplementations.
     * @param typeID of the NodeType or RelationshipType.
     * @return List of QNames pointing to the
     *         NodeTypeImplementations/RelationshipTypeImplementations or empty list if nothing is
     *         found.
     */
    public List<QName> getTypeImplementationsOfType(CSARID csarID, QName typeID);

    /**
     * This method returns a list of the names of ImplementationArtifacts of a given
     * NodeTypeImplementation or RelationshipTypeImplementation in a given CSAR.
     *
     * @param csarID of the CSAR containing the NodeTypeImplementation or
     *        RelationshipTypeImplementation.
     * @param typeImplementationID of the NodeTypeImplementation or RelationshipTypeImplementation
     *        containing the ImplementationArtifact names.
     * @return List of String containing the names of the ImplementationArtifacts or empty list if
     *         there none.
     */
    public List<String> getImplementationArtifactNamesOfTypeImplementation(CSARID csarID, QName typeImplementationID);

    /**
     * This method returns a list of Strings of the RequiredContainerFeatures of a given
     * NodeTypeImplementation or RelationshipTypeImplementation a given CSAR.
     *
     * @param csarID of the CSAR containing the NodeTypeImplementation or
     *        RelationshipTypeImplementation.
     * @param typeImplementationID of the NodeTypeImplementation or RelationshipTypeImplementation
     *        containing the RequiredContainerFeatures.
     * @return List of String containing the URIs of the RequiredContainerFeatures or empty list if
     *         there none.
     */
    public List<String> getRequiredContainerFeaturesOfATypeImplementation(CSARID csarID, QName typeImplementationID);

    /**
     * This method returns the ArtifactType of a given ImplementationArtifact of a given
     * NodeTypeImplementation or RelationshipTypeImplementation in a given CSAR.
     *
     * @param csarID of the CSAR containing the NodeTypeImplementation or
     *        RelationshipTypeImplementation.
     * @param typeImplementationID of the NodeTypeImplementation or RelationshipTypeImplementation
     *        containing the ImplementationArtifact.
     * @param implementationArtifactName of the ImplementationArtifact
     * @return QName of the ArtifactType or <code>null</code> in case of an error
     */
    public QName getArtifactTypeOfAImplementationArtifactOfATypeImplementation(CSARID csarID,
                                                                               QName typeImplementationID,
                                                                               String implementationArtifactName);

    /**
     * This method returns the ArtifactTemplate of a given ImplementationArtifact of a given
     * NodeTypeImplementation or RelationshipTypeImplementation in a given CSAR.
     *
     * @param csarID of the CSAR containing the NodeTypeImplementation or
     *        RelationshipTypeImplementation.
     * @param typeImplementationID of the NodeTypeImplementation or RelationshipTypeImplementation
     *        containing the ImplementationArtifact.
     * @param implementationArtifactName of the ImplementationArtifact
     * @return QName of the ArtifactTemplate or <code>null</code> in case of an error
     */
    public QName getArtifactTemplateOfAImplementationArtifactOfATypeImplementation(CSARID csarID,
                                                                                   QName typeImplementationID,
                                                                                   String implementationArtifactName);

    /**
     * This method returns the Node of the specified reference within the specified csarID.
     *
     * @param csarID of the CSAR.
     * @param reference of which the XML should be returned.
     * @return Node of the reference.
     */
    public Node getReferenceAsNode(CSARID csarID, QName reference);

    /**
     * This method returns a node containing the InputParameter of Elements of the specified
     * operation of the specified interface of the specified NodeType or RelationshipType within the
     * specified csarID.
     *
     * @param csarID of the CSAR.
     * @param typeID of the NodeType or RelationshipType containing the operation.
     * @param interfaceName of the operation.
     * @param operationName of which the parameters should be returned.
     * @return Node containing the InputParameters.
     */
    public Node getInputParametersOfATypeOperation(CSARID csarID, QName typeID, String interfaceName,
                                                   String operationName);

    /**
     * This method returns a List of Strings containing the InputParameter of Elements of the
     * specified operation of the specified interface of the specified NodeType or RelationshipType
     * within the specified csarID.
     *
     * @param csarID of the CSAR.
     * @param typeID of the NodeType or RelationshipType containing the operation.
     * @param interfaceName of the operation.
     * @param operationName of which the parameters should be returned.
     * @return A List of String containing the InputParameters.
     */
    public List<String> getInputParametersOfTypeOperation(CSARID csarID, QName typeId, String interfaceName,
                                                          String operationName);

    /**
     * This method returns a List of Strings containing the InputParameter of Elements of the
     * specified operation of the specified interface of the specified NodeType or RelationshipType
     * within the specified csarID.
     *
     * @param csarID of the CSAR.
     * @param typeID of the NodeType or RelationshipType containing the operation.
     * @param interfaceName of the operation.
     * @param operationName of which the parameters should be returned.
     * @return A List of String containing the OutputParameters.
     */
    public List<String> getOutputParametersOfTypeOperation(CSARID csarID, QName typeId, String interfaceName,
                                                           String operationName);

    /**
     * This method returns a node containing the OutputParameter of Elements of the specified
     * operation of the specified interface of the specified NodeType or RelationshipType within the
     * specified csarID.
     *
     * @param csarID of the CSAR.
     * @param typeID of the NodeType or RelationshipType containing the operation.
     * @param interfaceName of the operation.
     * @param operation name of which the parameters should be returned.
     * @return Node containing the OutputParameters.
     */
    public Node getOutputParametersOfATypeOperation(CSARID csarID, QName typeID, String interfaceName,
                                                    String operationName);

    /**
     * This method returns the specific content of a given ImplementationArtifact of a given
     * NodeTypeImplementation or RelationshipTypeImplementation in a given CSAR.
     *
     * @param csarID of the CSAR containing the NodeTypeImplementation or
     *        RelationshipTypeImplementation.
     * @param typeImplementationID of the NodeTypeImplementation or RelationshipTypeImplementation
     *        containing the ImplementationArtifact.
     * @param implementationArtifactName of the ImplementationArtifact
     * @return Document which represents the specific content or <code>null</code> in case of an
     *         error. A new document object is created for each method call.
     */
    public Document getArtifactSpecificContentOfAImplementationArtifact(CSARID csarID, QName typeImplementationID,
                                                                        String implementationArtifactName);

    /**
     * This method returns the implemented interface of a given ImplementationArtifact of a given
     * NodeTypeImplementation or RelationshipTypeImplementation in a given CSAR.
     *
     * @param csarID of the CSAR containing the NodeTypeImplementation or
     *        RelationshipTypeImplementation.
     * @param typeImplementationID of the NodeTypeImplementation or RelationshipTypeImplementation
     *        containing the ImplementationArtifact.
     * @param implementationArtifactName of the ImplementationArtifact
     * @return String which represents the implemented interface or <code>null</code> in case of an
     *         error or no interface is specified.
     */
    public String getInterfaceOfAImplementationArtifactOfATypeImplementation(CSARID csarID, QName typeImplementationID,
                                                                             String implementationArtifactName);

    /**
     * This method returns the implemented operation of a given ImplementationArtifact of a given
     * NodeTypeImplementation or RelationshipTypeImplementation in a given CSAR.
     *
     * @param csarID of the CSAR containing the NodeTypeImplementation or
     *        RelationshipTypeImplementation.
     * @param typeImplementationID of the NodeTypeImplementation or RelationshipTypeImplementation
     *        containing the ImplementationArtifact.
     * @param implementationArtifactName of the ImplementationArtifact
     * @return String which represents the implemented operation or <code>null</code> in case of an
     *         error or no operation is specified.
     */
    public String getOperationOfAImplementationArtifactOfATypeImplementation(CSARID csarID, QName typeImplementationID,
                                                                             String implementationArtifactName);

    /**
     * This method returns the ArtiactSpecificContent of a deploymentArtifact of a given
     * NodeTypeImplementation or NodeTemplate in a given CSAR.
     *
     * @param csarID of the CSAR containing the ArtifactTemplate.
     * @param reference the QName reference to a NodeTemplate or NodeTypeImplementation.
     * @param deploymentArtifactName the Name of the deploymentArtifact
     * @return Document of the ArtifactSpecificContent. A new document object is created for each
     *         method call.
     */
    public Document getArtifactSpecificContentOfADeploymentArtifact(CSARID csarID, QName nodeTypeImplementationID,
                                                                    String deploymentArtifactName);

    /**
     * This method returns the content of the Properties Element of an ArtifactTemplate.
     *
     * @param csarID of the CSAR containing the ArtifactTemplate.
     * @param artifactTemplateID of the requested ArtifactTemplate.
     * @return Document which represents the Properties content or null in case of an error.
     */
    public Document getPropertiesOfAArtifactTemplate(CSARID csarID, QName artifactTemplateID);

    /**
     * This method returns the List of PropertyConstraints of a ArtifactTemplate.
     *
     * @param csarID of the CSAR containing the ArtifactTemplate.
     * @param artifactTemplateID of the requested ArtifactTemplate.
     * @return Document which represents the PropertyConstraints or an empty list.
     */
    public List<TPropertyConstraint> getPropertyConstraintsOfAArtifactTemplate(CSARID csarID, QName artifactTemplateID);

    /**
     * This method returns a list of artifacts of a given ArtifactTemplate.
     *
     * @param csarID of the CSAR containing the ArtifactTemplate.
     * @param artifactTemplateID of the requested ArtifactTemplate.
     * @return List of artifacts or empty list if there are no artifact references or all artifact
     *         references are invalid / not supported.
     */
    public List<AbstractArtifact> getArtifactsOfAArtifactTemplate(CSARID csarID, QName artifactTemplateID);

    /**
     * This method returns the QName of the NodeType which is the type of the NodeTemplate defined
     * by the parameters.
     *
     * @param csarID of the CSAR containing the ArtifactTemplate.
     * @param serviceTemplateID of the ServiceTemplate containing the NodeTemplate.
     * @param nodeTemplateID the String value of the attribute ID of the NodeTemplate.
     * @return of the NodeType or null in case of an error
     */
    public QName getNodeTypeOfNodeTemplate(CSARID csarID, QName serviceTemplateID, String nodeTemplateID);


    /**
     * This method returns the QName of the NodeType which is the type of the NodeTemplate defined
     * by the parameters.
     *
     * @param csarID of the CSAR containing the ArtifactTemplate.
     * @param serviceTemplateID of the ServiceTemplate containing the NodeTemplate.
     * @param relationshipTemplateID the String value of the attribute ID of the
     *        RelationshipTemplate.
     * @return of the NodeType or null in case of an error
     */
    public QName getRelationshipTypeOfRelationshipTemplate(CSARID csarID, QName serviceTemplateID,
                                                           String relationshipTemplateID);

    /**
     * This method returns the Document which contains the default Properties of the NodeTemplate
     * defined by the parameter
     *
     * @param csarID of the CSAR containing the NodeTemplate or RelationshipTemplate.
     * @param serviceTemplateID of the ServiceTemplate containing the NodeTemplate or
     *        RelationshipTemplate.
     * @param templateID the String value of the attribute ID of the NodeTemplate or
     *        RelationshipTemplate.
     * @return Document containing the Properties (may be empty) or <code>null</null> in case of an
     *         error
     */
    public Document getPropertiesOfTemplate(CSARID csarID, QName serviceTemplateID, String templateID);

    /**
     * This method returns a Document which contains the PropertiesDefinition of the NodeType
     * defined by the parameter
     *
     * @return Document containing the PropertiesDefinition (may be empty) or null in case of an
     *         error
     */
    public Document getPropertiesDefinitionOfNodeType(CSARID csarID, QName nodeTypeID);

    /**
     * Returns the the resolved artifacts.
     *
     * @param csarID of the CSAR containing the NodeTemplate.
     * @param nodeTemplateID QName of the nodeTemplate (ID)
     * @return Resolved Artifacts containing all resolvedArtifacts
     */
    public ResolvedArtifacts getResolvedArtifactsOfNodeTemplate(CSARID csarID, QName nodeTemplateID);

    /**
     * Returns the the resolved artifacts.
     *
     * @param csarID of the CSAR containing the NodeTypeImplementation.
     * @param nodeTypeImplementationID QName of the nodeTypeImplementation (ID)
     * @return Resolved Artifacts containing all resolvedArtifacts
     */
    public ResolvedArtifacts getResolvedArtifactsOfNodeTypeImplementation(CSARID csarID,
                                                                          QName nodeTypeImplementationID);

    /**
     * Returns the the resolved artifacts.
     *
     * @param csarID of the CSAR containing the NodeTypeImplementation.
     * @param relationshipTypeImplementationID QName of the relationshipTypeImplementation (ID)
     * @return Resolved Artifacts containing all resolvedArtifacts
     */
    public ResolvedArtifacts getResolvedArtifactsOfRelationshipTypeImplementation(CSARID csarID,
                                                                                  QName relationshipTypeImplementationID);

    /**
     * This function deletes the content about a certain CSAR, identified due the CSARID given as a
     * parameter.
     *
     * @param csarID the ID of a CSAR which stored informations shall be deleted.
     * @return true for success, false for one or more errors
     */
    public boolean clearCSARContent(CSARID csarID);

    /**
     * This function tries to invoke the <code>getName()</code> method of the jaxb-class belonging
     * to the reference
     *
     * @param csarID of the CSAR containing the reference
     * @param reference the qualifiedName of the reference of which the name attribute should be
     *        retrieved
     * @return the return value of the call to getName-Method() of the jaxb-class<br>
     *
     *         <b>null</b> - if any error occured (f.ex. no jaxb-implementation was found or the
     *         reference didnt exist)
     */
    public String getNameOfReference(CSARID csarID, QName reference);

    /**
     * This function retrieves the min and maxInstances of the nodeTemplates in the specified
     * serviceTemplate
     *
     * @param csarID of the CSAR containing the nodeTemplate
     * @param serviceTemplateID of the serviceTemplate
     * @return minInstances and maxInstances values of the nodeTemplates in the specified
     *         serviceTemplate
     */
    public NodeTemplateInstanceCounts getInstanceCountsOfNodeTemplatesByServiceTemplateID(CSARID csarID,
                                                                                          QName serviceTemplateID);

    /**
     * checks if a nodeTemplate exits.
     *
     * @param csarID csarID of the CSAR containing the reference
     * @param serviceTemplateID of the ServiceTemplate containing the NodeTemplate.
     * @param nodeTemplateID the String value of the attribute ID (=local part) of the NodeTemplate.
     *        (Namespace is internally retrieved from serviceTemplateID)
     * @return true if the nodeTemplate exists, false otherwise
     */
    boolean doesNodeTemplateExist(CSARID csarID, QName serviceTemplateID, String nodeTemplateID);

    /**
     * checks if a relationshipTemplate exists.
     *
     * @param csarId csarID of the CSAR containing the reference
     * @param serviceTemplateID serviceTempalteId to look for the Relationship Template
     * @param relationshipTemplateID the Id of the Relationship Template to look for
     * @return true if the Relationship Template exists withing the Service Template of the
     *         referenced CSAR
     */
    boolean doesRelationshipTemplateExist(CSARID csarId, QName serviceTemplateID, String relationshipTemplateID);

    /**
     * Returns the ID of the target NodeTemplate if one exists.
     *
     * @param csarID
     * @param serviceTemplateID
     * @param relationshipTemplateID
     * @return the String or null in case of error or none existence
     */
    public String getTargetNodeTemplateIDOfRelationshipTemplate(CSARID csarID, QName serviceTemplateID,
                                                                String relationshipTemplateID);

    /**
     * Returns the ID of the source NodeTemplate if one exists.
     *
     * @param csarID
     * @param serviceTemplateID
     * @param relationshipTemplateID
     * @return the String or null in case of error or none existence
     */
    public String getSourceNodeTemplateIDOfRelationshipTemplate(CSARID csarID, QName serviceTemplateID,
                                                                String relationshipTemplateID);

    /**
     * Returns the ID of the related NodeTemplate if one exists.
     *
     * @param csarID
     * @param serviceTemplateID
     * @param nodeTemplateID
     * @param relationshipTypeName
     * @return the String or null in case of error or none existence
     */
    public String getRelatedNodeTemplateID(CSARID csarID, QName serviceTemplateID, String nodeTemplateID,
                                           QName relationshipType);

    /**
     * This method returns the abstract artifact of a plan reference.
     *
     * @param csar CSAR in which the plan is
     * @param planId reference to the TOSCA Plan element
     * @return the abstract artifact or null in case of an error
     */
    public AbstractArtifact getPlanModelReferenceAbstractArtifact(CSARContent csar, QName planId);

    /**
     * Returns the name of a Plan element.
     *
     * @param csarID the ID of the CSAR
     * @param planId the ID of the Plan element
     * @return the String of the name attribute inside the Plan element
     */
    public String getPlanName(CSARID csarID, QName planId);

    /**
     * Returns the defined references of an ArtifacTtemplate.
     *
     * @param csarID
     * @param artifactTemplate
     *
     * @return references or null if ArtifacTemplate can not be found or hasn't specified references
     */
    public List<String> getArtifactReferenceWithinArtifactTemplate(CSARID csarID, QName artifactTemplate);

    /**
     *
     * Returns the Type of the specified ArtifactTemplate.
     *
     * @param csarID
     * @param artifactTemplate
     * @return the Type of the specified ArtifactTemplate or null if ArtifacTemplate can not be
     *         found
     */
    public QName getArtifactTypeOfArtifactTemplate(CSARID csarID, QName artifactTemplate);

    /**
     * This method returns a list of the names of DeploymentArtifacts of a given
     * NodeTypeImplementation in a given CSAR.
     *
     * @param csarID of the CSAR containing the NodeTypeImplementation.
     * @param nodeTypeImplementationID of the NodeTypeImplementation containing the
     *        ImplementationArtifact names.
     * @return List of String containing the names of the DeploymentArtifacts or empty list if there
     *         none.
     */
    public List<String> getDeploymentArtifactNamesOfNodeTypeImplementation(CSARID csarID,
                                                                           QName nodeTypeImplementationID);

    /**
     * This method returns the ArtifactTemplate of a given DeploymentArtifact of a given
     * NodeTypeImplementation in a given CSAR.
     *
     * @param csarID of the CSAR containing the NodeTypeImplementation.
     * @param nodeTypeImplementationID of the NodeTypeImplementation containing the
     *        DeploymentArtifact.
     * @param deploymentArtifactName of the DeploymentArtifact
     * @return QName of the ArtifactType or null in case of an error
     */
    public QName getArtifactTemplateOfADeploymentArtifactOfANodeTypeImplementation(CSARID csarID,
                                                                                   QName nodeTypeImplementationID,
                                                                                   String deploymentArtifactName);

    /**
     * This method returns the IDs of the derived Node Type Implementation hierarchy given through
     * the DerivedFrom property.
     *
     * @param nodeTypeImplementationId a QName of a Node Type Implementation
     * @return a List of QName with at least the Node Type Implementation Id given as input
     */
    public List<QName> getNodeTypeImplementationTypeHierarchy(CSARID csarID, QName nodeTypeImplementationId);

    /**
     * Returns the QNames of the ServiceTemplates in a CSAR.
     *
     * @param csarID
     * @return List of Service Template QNames
     */
    public List<QName> getServiceTemplatesInCSAR(CSARID csarID);

    /**
     * Returns the List of Node Template IDs in a Service Template.
     *
     * @param csarID the Id of the CSAR to look for the Service Template
     * @param serviceTemplate the ID of the target Service Template
     * @return List of Node Template IDs
     */
    public List<String> getNodeTemplatesOfServiceTemplate(CSARID csarID, QName serviceTemplate);

    /**
     * Returns the List of Relationship Template IDs in a Service Template.
     *
     * @param csarID
     * @param serviceTemplate
     * @return List of Relationship Template IDs
     */
    public List<String> getRelationshipTemplatesOfServiceTemplate(CSARID csarId, QName serviceTemplate);

    /**
     * Returns the BoundaryDefinitions of the referenced ServiceTemplate
     *
     * @param csarId the CSAR the ServiceTemplate belongs to
     * @param serviceTemplateId a QName denoting a ServiceTemplate
     * @return a BoundaryDefinitions
     */
    public TBoundaryDefinitions getBoundaryDefinitionsOfServiceTemplate(CSARID csarId, QName serviceTemplateId);

    /**
     * Returns the id's of capabilities defined within the referenced nodeTemplateId
     *
     * @param csarId the id of the CSAR to look in
     * @param serviceTemplateId the id of the Service Template inside the CSAR
     * @param nodeTemplateId the id of the Node Template to get its capabilities
     * @return a List of QNames denoting Capabilities of the given Node Template
     */
    public List<QName> getNodeTemplateCapabilities(CSARID csarId, QName serviceTemplateId, String nodeTemplateId);

    /**
     * Returns the id's of requirements defined within the referenced nodeTemplateId
     *
     * @param csarId the id of the CSAR to look in
     * @param serviceTemplateId the id of the Service Template inside the CSAR
     * @param nodeTemplateId the id of the Node Template to get its requirements
     * @return a List of QNames denoting Requirements of the given Node Template
     */
    public List<QName> getNodeTemplateRequirements(CSARID csarId, QName serviceTemplateId, String nodeTemplateId);

    /**
     * Returns the id of the referenced target Relationship Template
     *
     * @param csarId the id of the CSAR to look in
     * @param serviceTemplateId the id of the Service Template inside the CSAR
     * @param relationshipTemplateId the id of the Relationship Template to get its capabilities
     * @return a QName denoting the entity on the target of the given Relationship Template
     */
    public QName getRelationshipTemplateTarget(CSARID csarId, QName serviceTemplateId, String relationshipTemplateId);

    /**
     * Returns the id of the referenced source of this Relationship l3Template
     *
     * @param csarId the id of the CSAR to look in
     * @param serviceTemplateId the id of the Service Template inside the CSAR
     * @param relationshipTemplateId the id of the Relationship Template to get its requirements
     * @return a QName denoting the entity on the source of the given Relationship Template
     */
    public QName getRelationshipTemplateSource(CSARID csarId, QName serviceTemplateId, String relationshipTemplateId);

    /**
     * Returns a list of operation names of the referenced Node Type's interface
     *
     * @param csarId the CSAR to look in
     * @param nodeTypeId the id of the Node Type to look in
     * @param interfaceName the name of the interface to look in
     * @return a List of Strings representing the operation names of the referenced Node Type
     *         interface
     */
    public List<String> getOperationNamesOfNodeTypeInterface(CSARID csarId, QName nodeTypeId, String interfaceName);

    /**
     * Returns a list of interface names of the referenced Node Type
     *
     * @param csarId the CSAR to look in
     * @param nodeTypeId the id of the Node Type to look in
     * @return a List of String representing the interface names of the referenced Node Type
     */
    public List<String> getInterfaceNamesOfNodeType(CSARID csarId, QName nodeTypeId);
}
