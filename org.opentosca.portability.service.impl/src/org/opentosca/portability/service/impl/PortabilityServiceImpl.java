package org.opentosca.portability.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.portability.service.IPortabilityService;
import org.opentosca.portability.service.model.Artifacts;
import org.opentosca.portability.service.model.DeploymentArtifact;
import org.opentosca.portability.service.model.ImplementationArtifact;
import org.opentosca.toscaengine.service.IToscaEngineService;
import org.opentosca.toscaengine.service.ResolvedArtifacts;
import org.opentosca.toscaengine.service.ResolvedArtifacts.ResolvedDeploymentArtifact;
import org.opentosca.toscaengine.service.ResolvedArtifacts.ResolvedImplementationArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The portabilityService can mainly be used to get the Artifacts of a
 * nodeTemplate specified by a nodeTemplateID It also offers functionality to
 * verify to which or whether (instanceOf / getNodeTypeOfNodeInstance) an
 * NodeInstance belongs to a NodeType
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 * 
 */
public class PortabilityServiceImpl implements IPortabilityService {

	final private static Logger LOG = LoggerFactory
			.getLogger(PortabilityServiceImpl.class);

	private static IToscaEngineService toscaEngineService;

	@Override
	public Artifacts getNodeTemplateArtifacts(CSARID csarID,
			QName serviceTemplateID, QName nodeTemplateID,
			ArtifactType artifactType, String deploymentArtifactName,
			String interfaceName, String operationName) {

		// retrieve qnames of all nodeTypes => and qnames of implementations of
		// this nodeTypes
		QName nodeTypeOfNodeTemplate = toscaEngineService
				.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
						nodeTemplateID.getLocalPart());

		// TODO: null check nodeTypeOfNodeTemplate
		List<QName> nodeTypeImplementationsOfNodeType = toscaEngineService
				.getNodeTypeImplementationsOfNodeType(csarID,
						nodeTypeOfNodeTemplate);

		// for each implementation we want to get the resolvedArtifacts => for
		// each imp we get the archifactSpecificContent OR the reference from
		// the respecting ArchifactTemplate
		// the results are filtered by name afterwards (if a name filter is
		// specified)
		List<ResolvedDeploymentArtifact> filteredDAList = new ArrayList<ResolvedDeploymentArtifact>();
		List<ResolvedImplementationArtifact> filteredIAList = new ArrayList<ResolvedImplementationArtifact>();

		fillFilteredArtifactsOfNodeTypeImplByName(filteredDAList,
				filteredIAList, csarID, nodeTypeImplementationsOfNodeType,
				deploymentArtifactName, interfaceName, operationName);

		fillFilteredArtifactsOfNodeTemplateByName(filteredDAList,
				filteredIAList, csarID, nodeTemplateID, deploymentArtifactName,
				interfaceName, operationName);

		// BUILD TArtifacts-Object
		Artifacts result = buildTArtifactsResult(filteredDAList,
				filteredIAList, artifactType);

		return result;
	}

	@Override
	public Artifacts getRelationshipTemplateArtifacts(CSARID csarID,
			QName serviceTemplateID, QName relationshipTemplateID,
			ArtifactType artifactType, String deplArtifactName,
			String interfaceName, String operationName) {

		// retrieve qnames of all nodeTypes => and qnames of implementations of
		// this nodeTypes
		QName relationshipTypeOfNodeTemplate = toscaEngineService
				.getRelationshipTypeOfRelationshipTemplate(csarID,
						serviceTemplateID,
						relationshipTemplateID.getLocalPart());
		// TODO: null check nodeTypeOfNodeTemplate
		List<QName> relationshipTypeImplementationsOfNodeType = toscaEngineService
				.getRelationshipTypeImplementationsOfRelationshipType(csarID,
						relationshipTypeOfNodeTemplate);

		// for each implementation we want to get the resolvedArtifacts => for
		// each imp we get the archifactSpecificContent OR the reference from
		// the respecting ArchifactTemplate
		// the results are filtered by name afterwards (if a name filter is
		// specified)
		List<ResolvedDeploymentArtifact> filteredDAList = new ArrayList<ResolvedDeploymentArtifact>();
		List<ResolvedImplementationArtifact> filteredIAList = new ArrayList<ResolvedImplementationArtifact>();

		fillFilteredArtifactsOfRelationshipTypeImplByName(filteredDAList,
				filteredIAList, csarID,
				relationshipTypeImplementationsOfNodeType, deplArtifactName,
				interfaceName, operationName);

		// BUILD TArtifacts-Object
		Artifacts result = buildTArtifactsResult(filteredDAList,
				filteredIAList, artifactType);

		return result;
	}

	/**
	 * @param filteredDAList
	 *            list of the DeploymentArtifacts
	 * @param filteredIAList
	 *            list of the ImplementationArtifacts
	 * @param artifactType
	 *            ArtifactType which should be contained in the results
	 * @see ArtifactType
	 * @return TArtifacts object which contains the artifacts of both given
	 *         lists
	 */
	private Artifacts buildTArtifactsResult(
			List<ResolvedDeploymentArtifact> filteredDAList,
			List<ResolvedImplementationArtifact> filteredIAList,
			ArtifactType artifactType) {

		Artifacts result = new Artifacts();
		// handling of different requests DAs / IAs or BOTH!
		if (ArtifactType.DA.equals(artifactType)) {

			List<DeploymentArtifact> deploymentArtifacts = new ArrayList<DeploymentArtifact>();

			for (ResolvedDeploymentArtifact da : filteredDAList) {
				// generate the resultElement
				DeploymentArtifact newDA = null;
				if (da.getArtifactSpecificContent() != null) {
					newDA = new DeploymentArtifact(da.getName(), da.getType()
							.toString(), da.getArtifactSpecificContent());
				} else {
					List<String> references = da.getReferences();
					newDA = new DeploymentArtifact(da.getName(), da.getType()
							.toString(), references);

				}
				deploymentArtifacts.add(newDA);
			}

			result.setDeploymentArtifact(deploymentArtifacts);

		}

		if ((ArtifactType.IA).equals(artifactType)) {
			List<ImplementationArtifact> implArtifacts = new ArrayList<ImplementationArtifact>();

			for (ResolvedImplementationArtifact ia : filteredIAList) {
				// generate the resultElement
				ImplementationArtifact newIA = null;
				if (ia.getArtifactSpecificContent() != null) {
					newIA = new ImplementationArtifact(ia.getOperationName(),
							ia.getInterfaceName(), ia.getType().toString(),
							ia.getArtifactSpecificContent());
				} else {
					// we need to get the references
					newIA = new ImplementationArtifact(ia.getOperationName(),
							ia.getInterfaceName(), ia.getType().toString(),
							ia.getReferences());
				}
				implArtifacts.add(newIA);
			}

			result.setImplementationArtifact(implArtifacts);

		}

		return result;
	}

	/**
	 * This method fills the both supplied lists (=> <b>MODIFIES</b> them).
	 * Therefore it queries the toscaEngineService for the NodeTemplate of the
	 * <code>CSARID</code> and filters them by the <code>artifactName</code>
	 * 
	 * @param filteredDAList
	 *            list of resolvedArtifacts where the filtered DAs will be
	 *            STORED!!!
	 * @param filteredIAList
	 *            list of resolvedArtifacts where the filtered IAs will be
	 *            STORED!!
	 * @param csarID
	 *            csarID of the CSAR
	 * @param nodeTemplateID
	 *            NodeTemplate ID
	 * @param deploymentArtifactNameFilter
	 *            Filter which will be applied (.equals) to the resolved
	 *            DeploymentArtifacts
	 * @param interfaceNameFilter
	 *            Filter which will be applied (.equals) to the resolved
	 *            ImplArtifacts
	 * @param operationNameFilter
	 *            Filter which will be applied (.equals) to the resolved
	 *            ImplArtifacts
	 */
	private void fillFilteredArtifactsOfNodeTemplateByName(
			List<ResolvedDeploymentArtifact> filteredDAList,
			List<ResolvedImplementationArtifact> filteredIAList, CSARID csarID,
			QName nodeTemplateID, String deploymentArtifactName,
			String interfaceName, String operationName) {
		
		ResolvedArtifacts resolvedTemp = toscaEngineService
				.getResolvedArtifactsOfNodeTemplate(csarID, nodeTemplateID);

		// filter IAs by artifactName if necessary (only add if name not
		// specified or matching)
		for (ResolvedImplementationArtifact resolvedIArtifact : resolvedTemp
				.getImplementationArtifacts()) {

			// a filter matches if the filter itself is NULL or it equals to
			// the objects value
			boolean matchesInterfaceName = false;
			boolean matchesOperationName = false;

			// check interfaceName
			if ((interfaceName == null)
					|| interfaceName.equals(resolvedIArtifact
							.getInterfaceName())) {
				matchesInterfaceName = true;
			}

			// check operationName
			if ((operationName == null)
					|| operationName.equals(resolvedIArtifact
							.getOperationName())) {
				matchesOperationName = true;
			}

			// if both filters could be matched => add to list
			if (matchesInterfaceName && matchesOperationName) {
				filteredIAList.add(resolvedIArtifact);
			}
		}

		// filter DAs by artifactName if necessary (only add if name not
		// specified or matching)
		for (ResolvedDeploymentArtifact resolvedDArtifact : resolvedTemp
				.getDeploymentArtifacts()) {
			if ((null == deploymentArtifactName)
					|| deploymentArtifactName.equals("")
					|| deploymentArtifactName.equals(resolvedDArtifact
							.getName())) {
				filteredDAList.add(resolvedDArtifact);
			}
		}
	}

	/**
	 * This method fills the both supplied lists (=> <b>MODIFIES</b> them).
	 * Therefore it queries the toscaEngineService for all the
	 * nodeTypeImplementations of the given <code>nodeTypeImplementations</code>
	 * of the <code>CSARID</code> and filters them by the
	 * <code>artifactName</code>
	 * 
	 * @param filteredDAList
	 *            list of resolvedArtifacts where the filtered DAs will be
	 *            STORED!!!
	 * @param filteredIAList
	 *            list of resolvedArtifacts where the filtered IAs will be
	 *            STORED!!
	 * @param csarID
	 *            csarID of the CSAR
	 * @param nodeTypeImplementations
	 *            List of nodeTypeImplementations for which the Artifacts will
	 *            be resolved
	 * @param deploymentArtifactNameFilter
	 *            Filter which will be applied (.equals) to the resolved
	 *            DeploymentArtifacts
	 * @param interfaceNameFilter
	 *            Filter which will be applied (.equals) to the resolved
	 *            ImplArtifacts
	 * @param operationNameFilter
	 *            Filter which will be applied (.equals) to the resolved
	 *            ImplArtifacts
	 */
	private void fillFilteredArtifactsOfNodeTypeImplByName(
			List<ResolvedDeploymentArtifact> filteredDAList,
			List<ResolvedImplementationArtifact> filteredIAList, CSARID csarID,
			List<QName> nodeTypeImplementations,
			String deploymentArtifactNameFilter, String interfaceNameFilter,
			String operationNameFilter) {

		for (QName ntImplQName : nodeTypeImplementations) {
			// add the list of ImplementationArtifactNames of the
			// NodeTypeImplementation to the iaNames-List
			// if name is specified only add matching artifactNames

			ResolvedArtifacts resolvedTemp = toscaEngineService
					.getResolvedArtifactsOfNodeTypeImplementation(csarID,
							ntImplQName);

			// filter IAs by artifactName if necessary (only add if name not
			// specified or matching)
			for (ResolvedImplementationArtifact resolvedIArtifact : resolvedTemp
					.getImplementationArtifacts()) {

				// a filter matches if the filter itself is NULL or it equals to
				// the objects value
				boolean matchesInterfaceName = false;
				boolean matchesOperationName = false;

				// check interfaceName
				if ((interfaceNameFilter == null)
						|| interfaceNameFilter.equals(resolvedIArtifact
								.getInterfaceName())) {
					matchesInterfaceName = true;
				}

				// check operationName
				if ((operationNameFilter == null)
						|| operationNameFilter.equals(resolvedIArtifact
								.getOperationName())) {
					matchesOperationName = true;
				}

				// if both filters could be matched => add to list
				if (matchesInterfaceName && matchesOperationName) {
					filteredIAList.add(resolvedIArtifact);
				}
			}

			// filter DAs by artifactName if necessary (only add if name not
			// specified or matching)
			for (ResolvedDeploymentArtifact resolvedDArtifact : resolvedTemp
					.getDeploymentArtifacts()) {
				if ((null == deploymentArtifactNameFilter)
						|| deploymentArtifactNameFilter.equals("")
						|| deploymentArtifactNameFilter
								.equals(resolvedDArtifact.getName())) {
					filteredDAList.add(resolvedDArtifact);
				}
			}

		}

	}

	/**
	 * This method fills the both supplied lists (=> <b>MODIFIES</b> them).
	 * Therefore it queries the toscaEngineService for all the
	 * relationshipTypeImplementations of the given
	 * <code>relationTypeImplementations</code> of the <code>CSARID</code> and
	 * filters them by the <code>artifactName</code>
	 * 
	 * @param filteredDAList
	 *            list of resolvedArtifacts where the filtered DAs will be
	 *            STORED!!!
	 * @param filteredIAList
	 *            list of resolvedArtifacts where the filtered IAs will be
	 *            STORED!!
	 * @param csarID
	 *            csarID of the CSAR
	 * @param relationshipTypeImplementations
	 *            List of relationshipTypeImplementations for which the
	 *            Artifacts will be resolved
	 * @param artifactNameFilter
	 *            Filter which will be applied (.equals) to the resolved
	 *            Artifacts
	 */
	private void fillFilteredArtifactsOfRelationshipTypeImplByName(
			List<ResolvedDeploymentArtifact> filteredDAList,
			List<ResolvedImplementationArtifact> filteredIAList, CSARID csarID,
			List<QName> relationshipTypeImplementations,
			String deploymentArtifactNameFilter, String interfaceNameFilter,
			String operationNameFilter) {
		for (QName ntImplQName : relationshipTypeImplementations) {
			// add the list of ImplementationArtifactNames of the
			// NodeTypeImplementation to the iaNames-List
			// if name is specified only add matching artifactNames

			ResolvedArtifacts resolvedTemp = toscaEngineService
					.getResolvedArtifactsOfRelationshipTypeImplementation(
							csarID, ntImplQName);

			// if artifactName is not specified we add all Names - otherwise we
			// need to filter

			// filter IAs by artifactName if necessary (only add if name not
			// specified or matching)
			// filter IAs by artifactName if necessary (only add if name not
			// specified or matching)
			for (ResolvedImplementationArtifact resolvedIArtifact : resolvedTemp
					.getImplementationArtifacts()) {

				// a filter matches if the filter itself is NULL or it equals to
				// the objects value
				boolean matchesInterfaceName = false;
				boolean matchesOperationName = false;

				// check interfaceName
				if ((interfaceNameFilter == null)
						|| interfaceNameFilter.equals(resolvedIArtifact
								.getInterfaceName())) {
					matchesInterfaceName = true;
				}

				// check operationName
				if ((operationNameFilter == null)
						|| operationNameFilter.equals(resolvedIArtifact
								.getOperationName())) {
					matchesOperationName = true;
				}

				// if both filters could be matched => add to list
				if (matchesInterfaceName && matchesOperationName) {
					filteredIAList.add(resolvedIArtifact);
				}
			}

			// filter DAs by artifactName if necessary (only add if name not
			// specified or matching)
			for (ResolvedDeploymentArtifact resolvedDArtifact : resolvedTemp
					.getDeploymentArtifacts()) {
				if ((deploymentArtifactNameFilter == null)
						|| deploymentArtifactNameFilter
								.equals(resolvedDArtifact.getName())) {
					filteredDAList.add(resolvedDArtifact);
				}
			}

		}

	}

	@Override
	public QName getNodeTypeOfNodeInstance(CSARID csarID, QName NodeInstanceID) {
		// TODO: implement: this will need to use the instanceDataEngine because
		// we need to get the NodeTemplateID for a
		// NodeInstanceID and only the instanceDataEngine can do that!
		return null;
	}

	@Override
	public boolean instanceOf(CSARID csarID, QName nodeInstanceID,
			QName nodeTypeID) {
		// TODO implement
		return false;
	}

	public void bindToscaEngineService(IToscaEngineService toscaEngineService) {
		if (toscaEngineService == null) {
			PortabilityServiceImpl.LOG.error("Can't bind ToscaEngine Service.");
		} else {
			PortabilityServiceImpl.toscaEngineService = toscaEngineService;
			PortabilityServiceImpl.LOG.info("ToscaEngine-Service bound.");
		}
	}

	public void unbindToscaEngineService(IToscaEngineService toscaEngineService) {
		PortabilityServiceImpl.toscaEngineService = null;
		PortabilityServiceImpl.LOG.info("ToscaEngine-Service unbound.");

	}

	@Override
	public boolean isNodeTemplate(CSARID csarID, QName serviceTemplateID,
			QName templateId) {
		return toscaEngineService.doesNodeTemplateExist(csarID,
				serviceTemplateID, templateId.getLocalPart());
	}

}
