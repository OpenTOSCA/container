package org.opentosca.container.api.service;

import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.NodeTemplateDTO;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;

// TODO it is assumed that the name of the node template is the same as its id.
/**
 * Provides data access functionality to retrieve node templates based on a service template.
 * Throughout the class, it is assumed that the passed service template id belongs to the passed
 * CSAR, i.e., it is assumed that a check that this is true is performed earlier.
 *
 * @author Ghareeb Falazi
 *
 */
public class NodeTemplateService {
    private static Logger logger = LoggerFactory.getLogger(InstanceService.class);

    private CsarService csarService;
    private IToscaEngineService toscaEngineService;

    /**
     * Gets a collection of node templates associated to a given service template.
     *
     * @param csarId The id of the CSAR
     * @param serviceTemplateQName The QName of the service template within the given CSAR
     * @return A collection of node templates stored within the given service template.
     */
    public List<NodeTemplateDTO> getNodeTemplatesOfServiceTemplate(final String csarId,
                    final String serviceTemplateQName) {
        final CSARContent csarContent = this.csarService.findById(csarId);
        final List<String> nodeTemplateIds = this.toscaEngineService.getNodeTemplatesOfServiceTemplate(
            csarContent.getCSARID(), QName.valueOf(serviceTemplateQName));
        final List<NodeTemplateDTO> nodeTemplates = Lists.newArrayList();
        NodeTemplateDTO currentNodeTemplate;

        for (final String id : nodeTemplateIds) {
            currentNodeTemplate = createNodeTemplate(csarContent.getCSARID(), QName.valueOf(serviceTemplateQName), id);
            nodeTemplates.add(currentNodeTemplate);
        }

        return nodeTemplates;
    }

    /**
     * Gets the node template specified by its id
     *
     * @param csarId The id of the CSAR
     * @param serviceTemplateQName The QName of the service template within the given CSAR
     * @param nodeTemplateId The id of the node template we want to get and that belongs to the
     *        specified service template
     * @return The node template specified by the given id
     * @throws NotFoundException If the service template does not contain the specified node template
     */
    public NodeTemplateDTO getNodeTemplateById(final String csarId, final QName serviceTemplateQName,
                    final String nodeTemplateId)
        throws NotFoundException {
        final CSARContent csarContent = this.csarService.findById(csarId);
        final CSARID idOfCsar = csarContent.getCSARID();

        if (!this.toscaEngineService.getNodeTemplatesOfServiceTemplate(idOfCsar, serviceTemplateQName)
                                    .contains(nodeTemplateId)) {
            logger.info("Node template \"" + nodeTemplateId + "\" could not be found");
            throw new NotFoundException("Node template \"" + nodeTemplateId + "\" could not be found");
        }

        return createNodeTemplate(idOfCsar, serviceTemplateQName, nodeTemplateId);
    }

    /**
     * Checks whether the specified service template contains a given node template.
     *
     * @param csarId The id of the CSAR
     * @param serviceTemplateQName the QName of the service template
     * @param nodeTemplateId the id of the node template to check for
     * @return <code>true</code> when the CSAR contains the service template and the service template
     *         contains the node template, otherwise <code>false</code>
     */
    public boolean hasNodeTemplate(final String csarId, final QName serviceTemplateQName, final String nodeTemplateId) {
        return this.getNodeTemplateIdsOfServiceTemplate(csarId, serviceTemplateQName.toString())
                   .contains(nodeTemplateId);
    }


    /**
     * Gets the properties (as an XML document) of a given node template.
     *
     * @param csarId
     * @param serviceTemplateQName
     * @param nodeTemplateId
     * @return
     */
    public Document getPropertiesOfNodeTemplate(final String csarId, final QName serviceTemplateQName,
                    final String nodeTemplateId) {
        final CSARContent csarContent = this.csarService.findById(csarId);
        final CSARID idOfCsar = csarContent.getCSARID();

        if (!this.toscaEngineService.getNodeTemplatesOfServiceTemplate(idOfCsar, serviceTemplateQName)
                                    .contains(nodeTemplateId)) {
            logger.info("Node template \"" + nodeTemplateId + "\" could not be found");
            throw new NotFoundException("Node template \"" + nodeTemplateId + "\" could not be found");
        }

        final Document properties = this.toscaEngineService.getPropertiesOfNodeTemplate(idOfCsar, serviceTemplateQName,
            nodeTemplateId);

        return properties;
    }

    // TODO Careful! this method assumes that the namespace of a node template is the same namespace as
    // its parent service template!
    /**
     * Creates a new instance of the NodeTemplateDTO class. It fetches the qualified name of node type
     * of the node template.
     *
     * @param csarId
     * @param serviceTemplateQName
     * @param nodeTemplateIde
     * @return
     */
    private NodeTemplateDTO createNodeTemplate(final CSARID csarId, final QName serviceTemplateQName,
                    final String nodeTemplateId) {
        final NodeTemplateDTO currentNodeTemplate = new NodeTemplateDTO();
        currentNodeTemplate.setId(nodeTemplateId);
        currentNodeTemplate.setName(nodeTemplateId);
        currentNodeTemplate.setNodeType(
            this.toscaEngineService.getNodeTypeOfNodeTemplate(csarId, serviceTemplateQName, nodeTemplateId).toString());

        return currentNodeTemplate;
    }

    /**
     * Gets a collection of node template ids associated to a given service template.
     *
     * @param csarId The id of the CSAR
     * @param serviceTemplateQName the QName of the service template within the given CSAR
     * @return A collection of node template ids stored within the given service template.
     */
    private List<String> getNodeTemplateIdsOfServiceTemplate(final String csarId, final String serviceTemplateQName) {
        final CSARContent csarContent = this.csarService.findById(csarId);

        return this.toscaEngineService.getNodeTemplatesOfServiceTemplate(csarContent.getCSARID(),
            QName.valueOf(serviceTemplateQName));
    }

    /* Service Injection */
    /*********************/
    public void setCsarService(final CsarService csarService) {
        this.csarService = csarService;
    }

    public void setToscaEngineService(final IToscaEngineService toscaEngineService) {
        this.toscaEngineService = toscaEngineService;
    }
}
