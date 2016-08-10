package org.opentosca.portability.service;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.portability.service.model.Artifacts;

/**
 * 
 * This interface describes functionality of resolving the Artifacts (esp.
 * references) inside of ServiceTemplates in a passed CSAR. This is a needed
 * functionality for the Plans.
 * 
 * The main function is getting the Artifacts of a nodeTemplate specified by a
 * nodeTemplateID It also offers functionality to verify to which or whether
 * (instanceOf / getNodeTypeOfNodeInstance) an NodeInstance belongs to a
 * NodeType *
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 * 
 */
public interface IPortabilityService {
	
	// TODO: this method should be implemented by a generic
	// ServiceInvoker-Interface - maybe call this interface here
	// f. ex. public String getIAEndpoint(QName nodeTemplateID, String
	// operationName)
	
	public enum ArtifactType {
		IA, DA
	}
	
	/**
	 * retrieves a TArtifact containing the specified ArtifactTypes of the
	 * nodeTemplate referenced by the <code>nodeTemplateID</code>
	 * 
	 * @param csarID
	 *            the ID of the CSAR used
	 * @param serviceTemplate
	 *            the ID (QName) of the serviceTemplate
	 * @param nodeTemplateID
	 *            the ID (QName) of the nodeTemplate
	 * @param artifactType
	 *            the Types of Artifactes which should be in the result
	 *            document. (f.ex.
	 *            <code>IPortabilityService.ArtifactType.BOTH</code> )
	 * @param deploymentArtifactName
	 *            the name of the deploymentArtifacts which should be retrieved
	 * @param interfaceName
	 *            the interfaceName of the implementationArtifact which should be retrieved
	 * @param operationName
	 *            the operationName of the implementationArtifact which should be retrieved
	 * 
	 * @return TArtifacts containing information about the artifacts specified
	 */
	public Artifacts getNodeTemplateArtifacts(CSARID csarID,
			QName serviceTemplateID, QName nodeTemplateID,
			ArtifactType artifactType, String deploymentArtifactName, String interfaceName, String operationName);
	
	/**
	 * retrieves a TArtifact containing the specified ArtifactTypes of the
	 * nodeTemplate referenced by the <code>nodeTemplateID</code>
	 * 
	 * @param csarID
	 *            the ID of the CSAR used
	 * @param serviceTemplate
	 *            the ID (QName) of the serviceTemplate
	 * @param relationshipTemplateID
	 *            the ID (QName) of the relationshipTemplate
	 * @param artifactType
	 *            the Types of Artifactes which should be in the result
	 *            document. (f.ex.
	 *            <code>IPortabilityService.ArtifactType.BOTH</code> )
	 * @param deploymentArtifactName
	 *            the name of the deploymentArtifacts for which the relationshipTemplateArtifacts should be retrieved
	 * @param interfaceName
	 *            the interfaceName of the implementationArtifact for which the relationshipTemplateArtifacts should be retrieved
	 * @param operationName
	 *            the operationName of the implementationArtifact for which the relationshipTemplateArtifacts should be retrieved
	 * 
	 * @return TArtifacts containing information about the artifacts specified
	 */
	public Artifacts getRelationshipTemplateArtifacts(CSARID csarID,
			QName serviceTemplateID, QName relationshipTemplateID,
			ArtifactType artifactType, String deploymentArtifactName, String interfaceName, String operationName);
	
	/**
	 * returns the QName of the NodeType which the NodeInstance specified by
	 * <code>NodeInstanceID</code> belongs to
	 * 
	 * @param NodeInstanceID
	 *            the ID of the NodeInstance (QName)
	 * @return QName of the NodeType
	 */
	public QName getNodeTypeOfNodeInstance(CSARID csarID, QName NodeInstanceID);
	
	/**
	 * Checks whether the given template QName is a NodeTemplate inside the
	 * referenced CSAR and ServiceTemplate
	 * 
	 * @param csarID
	 *            the CSARID of the CSAR the NodeTemplate should belong
	 * @param serviceTemplateID
	 *            the QName of the ServiceTemplate the NodeTemplate should
	 *            belong
	 * @param templateId
	 *            the QName of the Template to check whether it is a
	 *            NodeTemplate
	 * @return true if the QName denotes a NodeTemplate, else false
	 */
	public boolean isNodeTemplate(CSARID csarID, QName serviceTemplateID,
			QName templateId);
	
	/**
	 * returns whether the nodeInstanceID belongs to a given nodeType
	 * 
	 * @param csarID
	 * @param nodeInstanceID
	 * @param nodeTypeID
	 * @return
	 */
	public boolean instanceOf(CSARID csarID, QName nodeInstanceID,
			QName nodeTypeID);
	
}
