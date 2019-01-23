package org.opentosca.container.portability.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.ResolvedArtifacts;
import org.opentosca.container.core.engine.ResolvedArtifacts.ResolvedDeploymentArtifact;
import org.opentosca.container.core.engine.ResolvedArtifacts.ResolvedImplementationArtifact;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.portability.IPortabilityService;
import org.opentosca.container.portability.model.Artifacts;
import org.opentosca.container.portability.model.DeploymentArtifact;
import org.opentosca.container.portability.model.ImplementationArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The portabilityService can mainly be used to get the Artifacts of a nodeTemplate specified by a
 * nodeTemplateID It also offers functionality to verify to which or whether (instanceOf /
 * getNodeTypeOfNodeInstance) an NodeInstance belongs to a NodeType
 */
public class PortabilityServiceImpl implements IPortabilityService {

    final private static Logger LOG = LoggerFactory.getLogger(PortabilityServiceImpl.class);

    private static IToscaEngineService toscaEngineService;


    @Override
    public Artifacts getNodeTemplateArtifacts(final CSARID csarID, final QName serviceTemplateID,
                                              final QName nodeTemplateID, final ArtifactType artifactType,
                                              final String deploymentArtifactName, final String interfaceName,
                                              final String operationName) {

        // retrieve qnames of all nodeTypes => and qnames of implementations of
        // this nodeTypes
        final QName nodeTypeOfNodeTemplate =
            toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID, nodeTemplateID.getLocalPart());

        // TODO: null check nodeTypeOfNodeTemplate
        final List<QName> nodeTypeImplementationsOfNodeType =
            toscaEngineService.getTypeImplementationsOfType(csarID, nodeTypeOfNodeTemplate);

        // for each implementation we want to get the resolvedArtifacts => for
        // each imp we get the archifactSpecificContent OR the reference from
        // the respecting ArchifactTemplate
        // the results are filtered by name afterwards (if a name filter is
        // specified)
        final List<ResolvedDeploymentArtifact> filteredDAList = new ArrayList<>();
        final List<ResolvedImplementationArtifact> filteredIAList = new ArrayList<>();

        fillFilteredArtifactsOfNodeTypeImplByName(filteredDAList, filteredIAList, csarID,
                                                  nodeTypeImplementationsOfNodeType, deploymentArtifactName,
                                                  interfaceName, operationName);

        fillFilteredArtifactsOfNodeTemplateByName(filteredDAList, filteredIAList, csarID, nodeTemplateID,
                                                  deploymentArtifactName, interfaceName, operationName);

        // BUILD TArtifacts-Object
        final Artifacts result = buildTArtifactsResult(filteredDAList, filteredIAList, artifactType);

        return result;
    }

    @Override
    public Artifacts getRelationshipTemplateArtifacts(final CSARID csarID, final QName serviceTemplateID,
                                                      final QName relationshipTemplateID,
                                                      final ArtifactType artifactType, final String deplArtifactName,
                                                      final String interfaceName, final String operationName) {

        // retrieve qnames of all nodeTypes => and qnames of implementations of
        // this nodeTypes
        final QName relationshipTypeOfNodeTemplate =
            toscaEngineService.getRelationshipTypeOfRelationshipTemplate(csarID, serviceTemplateID,
                                                                         relationshipTemplateID.getLocalPart());
        // TODO: null check nodeTypeOfNodeTemplate
        final List<QName> relationshipTypeImplementationsOfNodeType =
            toscaEngineService.getTypeImplementationsOfType(csarID, relationshipTypeOfNodeTemplate);

        // for each implementation we want to get the resolvedArtifacts => for
        // each imp we get the archifactSpecificContent OR the reference from
        // the respecting ArchifactTemplate
        // the results are filtered by name afterwards (if a name filter is
        // specified)
        final List<ResolvedDeploymentArtifact> filteredDAList = new ArrayList<>();
        final List<ResolvedImplementationArtifact> filteredIAList = new ArrayList<>();

        fillFilteredArtifactsOfRelationshipTypeImplByName(filteredDAList, filteredIAList, csarID,
                                                          relationshipTypeImplementationsOfNodeType, deplArtifactName,
                                                          interfaceName, operationName);

        // BUILD TArtifacts-Object
        final Artifacts result = buildTArtifactsResult(filteredDAList, filteredIAList, artifactType);

        return result;
    }

    /**
     * @param filteredDAList list of the DeploymentArtifacts
     * @param filteredIAList list of the ImplementationArtifacts
     * @param artifactType ArtifactType which should be contained in the results
     * @see ArtifactType
     * @return TArtifacts object which contains the artifacts of both given lists
     */
    private Artifacts buildTArtifactsResult(final List<ResolvedDeploymentArtifact> filteredDAList,
                                            final List<ResolvedImplementationArtifact> filteredIAList,
                                            final ArtifactType artifactType) {

        final Artifacts result = new Artifacts();
        // handling of different requests DAs / IAs or BOTH!
        if (ArtifactType.DA.equals(artifactType)) {

            final List<DeploymentArtifact> deploymentArtifacts = new ArrayList<>();

            for (final ResolvedDeploymentArtifact da : filteredDAList) {
                // generate the resultElement
                DeploymentArtifact newDA = null;
                if (da.getArtifactSpecificContent() != null) {
                    newDA =
                        new DeploymentArtifact(da.getName(), da.getType().toString(), da.getArtifactSpecificContent());
                } else {
                    final List<String> references = da.getReferences();
                    newDA = new DeploymentArtifact(da.getName(), da.getType().toString(), references);

                }
                deploymentArtifacts.add(newDA);
            }

            result.setDeploymentArtifact(deploymentArtifacts);

        }

        if (ArtifactType.IA.equals(artifactType)) {
            final List<ImplementationArtifact> implArtifacts = new ArrayList<>();

            for (final ResolvedImplementationArtifact ia : filteredIAList) {
                // generate the resultElement
                ImplementationArtifact newIA = null;
                if (ia.getArtifactSpecificContent() != null) {
                    newIA = new ImplementationArtifact(ia.getOperationName(), ia.getInterfaceName(),
                        ia.getType().toString(), ia.getArtifactSpecificContent());
                } else {
                    // we need to get the references
                    newIA = new ImplementationArtifact(ia.getOperationName(), ia.getInterfaceName(),
                        ia.getType().toString(), ia.getReferences());
                }
                implArtifacts.add(newIA);
            }

            result.setImplementationArtifact(implArtifacts);

        }

        return result;
    }

    /**
     * This method fills the both supplied lists (=> <b>MODIFIES</b> them). Therefore it queries the
     * toscaEngineService for the NodeTemplate of the <code>CSARID</code> and filters them by the
     * <code>artifactName</code>
     *
     * @param filteredDAList list of resolvedArtifacts where the filtered DAs will be STORED!!!
     * @param filteredIAList list of resolvedArtifacts where the filtered IAs will be STORED!!
     * @param csarID csarID of the CSAR
     * @param nodeTemplateID NodeTemplate ID
     * @param deploymentArtifactNameFilter Filter which will be applied (.equals) to the resolved
     *        DeploymentArtifacts
     * @param interfaceNameFilter Filter which will be applied (.equals) to the resolved
     *        ImplArtifacts
     * @param operationNameFilter Filter which will be applied (.equals) to the resolved
     *        ImplArtifacts
     */
    private void fillFilteredArtifactsOfNodeTemplateByName(final List<ResolvedDeploymentArtifact> filteredDAList,
                                                           final List<ResolvedImplementationArtifact> filteredIAList,
                                                           final CSARID csarID, final QName nodeTemplateID,
                                                           final String deploymentArtifactName,
                                                           final String interfaceName, final String operationName) {

        final ResolvedArtifacts resolvedTemp =
            toscaEngineService.getResolvedArtifactsOfNodeTemplate(csarID, nodeTemplateID);

        // filter IAs by artifactName if necessary (only add if name not
        // specified or matching)
        for (final ResolvedImplementationArtifact resolvedIArtifact : resolvedTemp.getImplementationArtifacts()) {

            // a filter matches if the filter itself is NULL or it equals to
            // the objects value
            boolean matchesInterfaceName = false;
            boolean matchesOperationName = false;

            // check interfaceName
            if (interfaceName == null || interfaceName.equals(resolvedIArtifact.getInterfaceName())) {
                matchesInterfaceName = true;
            }

            // check operationName
            if (operationName == null || operationName.equals(resolvedIArtifact.getOperationName())) {
                matchesOperationName = true;
            }

            // if both filters could be matched => add to list
            if (matchesInterfaceName && matchesOperationName) {
                filteredIAList.add(resolvedIArtifact);
            }
        }

        // filter DAs by artifactName if necessary (only add if name not
        // specified or matching)
        for (final ResolvedDeploymentArtifact resolvedDArtifact : resolvedTemp.getDeploymentArtifacts()) {
            if (null == deploymentArtifactName || deploymentArtifactName.equals("")
                || deploymentArtifactName.equals(resolvedDArtifact.getName())) {
                filteredDAList.add(resolvedDArtifact);
            }
        }
    }

    /**
     * This method fills the both supplied lists (=> <b>MODIFIES</b> them). Therefore it queries the
     * toscaEngineService for all the nodeTypeImplementations of the given
     * <code>nodeTypeImplementations</code> of the <code>CSARID</code> and filters them by the
     * <code>artifactName</code>
     *
     * @param filteredDAList list of resolvedArtifacts where the filtered DAs will be STORED!!!
     * @param filteredIAList list of resolvedArtifacts where the filtered IAs will be STORED!!
     * @param csarID csarID of the CSAR
     * @param nodeTypeImplementations List of nodeTypeImplementations for which the Artifacts will
     *        be resolved
     * @param deploymentArtifactNameFilter Filter which will be applied (.equals) to the resolved
     *        DeploymentArtifacts
     * @param interfaceNameFilter Filter which will be applied (.equals) to the resolved
     *        ImplArtifacts
     * @param operationNameFilter Filter which will be applied (.equals) to the resolved
     *        ImplArtifacts
     */
    private void fillFilteredArtifactsOfNodeTypeImplByName(final List<ResolvedDeploymentArtifact> filteredDAList,
                                                           final List<ResolvedImplementationArtifact> filteredIAList,
                                                           final CSARID csarID,
                                                           final List<QName> nodeTypeImplementations,
                                                           final String deploymentArtifactNameFilter,
                                                           final String interfaceNameFilter,
                                                           final String operationNameFilter) {

        for (final QName ntImplQName : nodeTypeImplementations) {
            // add the list of ImplementationArtifactNames of the
            // NodeTypeImplementation to the iaNames-List
            // if name is specified only add matching artifactNames

            final ResolvedArtifacts resolvedTemp =
                toscaEngineService.getResolvedArtifactsOfNodeTypeImplementation(csarID, ntImplQName);

            // filter IAs by artifactName if necessary (only add if name not
            // specified or matching)
            for (final ResolvedImplementationArtifact resolvedIArtifact : resolvedTemp.getImplementationArtifacts()) {

                // a filter matches if the filter itself is NULL or it equals to
                // the objects value
                boolean matchesInterfaceName = false;
                boolean matchesOperationName = false;

                // check interfaceName
                if (interfaceNameFilter == null || interfaceNameFilter.equals(resolvedIArtifact.getInterfaceName())) {
                    matchesInterfaceName = true;
                }

                // check operationName
                if (operationNameFilter == null || operationNameFilter.equals(resolvedIArtifact.getOperationName())) {
                    matchesOperationName = true;
                }

                // if both filters could be matched => add to list
                if (matchesInterfaceName && matchesOperationName) {
                    filteredIAList.add(resolvedIArtifact);
                }
            }

            // filter DAs by artifactName if necessary (only add if name not
            // specified or matching)
            for (final ResolvedDeploymentArtifact resolvedDArtifact : resolvedTemp.getDeploymentArtifacts()) {
                if (null == deploymentArtifactNameFilter || deploymentArtifactNameFilter.equals("")
                    || deploymentArtifactNameFilter.equals(resolvedDArtifact.getName())) {
                    filteredDAList.add(resolvedDArtifact);
                }
            }

        }

    }

    /**
     * This method fills the both supplied lists (=> <b>MODIFIES</b> them). Therefore it queries the
     * toscaEngineService for all the relationshipTypeImplementations of the given
     * <code>relationTypeImplementations</code> of the <code>CSARID</code> and filters them by the
     * <code>artifactName</code>
     *
     * @param filteredDAList list of resolvedArtifacts where the filtered DAs will be STORED!!!
     * @param filteredIAList list of resolvedArtifacts where the filtered IAs will be STORED!!
     * @param csarID csarID of the CSAR
     * @param relationshipTypeImplementations List of relationshipTypeImplementations for which the
     *        Artifacts will be resolved
     * @param artifactNameFilter Filter which will be applied (.equals) to the resolved Artifacts
     */
    private void fillFilteredArtifactsOfRelationshipTypeImplByName(final List<ResolvedDeploymentArtifact> filteredDAList,
                                                                   final List<ResolvedImplementationArtifact> filteredIAList,
                                                                   final CSARID csarID,
                                                                   final List<QName> relationshipTypeImplementations,
                                                                   final String deploymentArtifactNameFilter,
                                                                   final String interfaceNameFilter,
                                                                   final String operationNameFilter) {
        for (final QName ntImplQName : relationshipTypeImplementations) {
            // add the list of ImplementationArtifactNames of the
            // NodeTypeImplementation to the iaNames-List
            // if name is specified only add matching artifactNames

            final ResolvedArtifacts resolvedTemp =
                toscaEngineService.getResolvedArtifactsOfRelationshipTypeImplementation(csarID, ntImplQName);

            // if artifactName is not specified we add all Names - otherwise we
            // need to filter

            // filter IAs by artifactName if necessary (only add if name not
            // specified or matching)
            // filter IAs by artifactName if necessary (only add if name not
            // specified or matching)
            for (final ResolvedImplementationArtifact resolvedIArtifact : resolvedTemp.getImplementationArtifacts()) {

                // a filter matches if the filter itself is NULL or it equals to
                // the objects value
                boolean matchesInterfaceName = false;
                boolean matchesOperationName = false;

                // check interfaceName
                if (interfaceNameFilter == null || interfaceNameFilter.equals(resolvedIArtifact.getInterfaceName())) {
                    matchesInterfaceName = true;
                }

                // check operationName
                if (operationNameFilter == null || operationNameFilter.equals(resolvedIArtifact.getOperationName())) {
                    matchesOperationName = true;
                }

                // if both filters could be matched => add to list
                if (matchesInterfaceName && matchesOperationName) {
                    filteredIAList.add(resolvedIArtifact);
                }
            }

            // filter DAs by artifactName if necessary (only add if name not
            // specified or matching)
            for (final ResolvedDeploymentArtifact resolvedDArtifact : resolvedTemp.getDeploymentArtifacts()) {
                if (deploymentArtifactNameFilter == null
                    || deploymentArtifactNameFilter.equals(resolvedDArtifact.getName())) {
                    filteredDAList.add(resolvedDArtifact);
                }
            }

        }

    }

    @Override
    public QName getNodeTypeOfNodeInstance(final CSARID csarID, final QName NodeInstanceID) {
        // TODO: implement: this will need to use the instanceDataEngine because
        // we need to get the NodeTemplateID for a
        // NodeInstanceID and only the instanceDataEngine can do that!
        return null;
    }

    @Override
    public boolean instanceOf(final CSARID csarID, final QName nodeInstanceID, final QName nodeTypeID) {
        // TODO implement
        return false;
    }

    public void bindToscaEngineService(final IToscaEngineService toscaEngineService) {
        if (toscaEngineService == null) {
            PortabilityServiceImpl.LOG.error("Can't bind ToscaEngine Service.");
        } else {
            PortabilityServiceImpl.toscaEngineService = toscaEngineService;
            PortabilityServiceImpl.LOG.info("ToscaEngine-Service bound.");
        }
    }

    public void unbindToscaEngineService(final IToscaEngineService toscaEngineService) {
        PortabilityServiceImpl.toscaEngineService = null;
        PortabilityServiceImpl.LOG.info("ToscaEngine-Service unbound.");

    }

    @Override
    public boolean isNodeTemplate(final CSARID csarID, final QName serviceTemplateID, final QName templateId) {
        return toscaEngineService.doesNodeTemplateExist(csarID, serviceTemplateID, templateId.getLocalPart());
    }

}
