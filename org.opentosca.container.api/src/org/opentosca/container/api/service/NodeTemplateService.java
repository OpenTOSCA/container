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

import com.google.common.collect.Lists;

public class NodeTemplateService {
	private static Logger logger = LoggerFactory.getLogger(InstanceService.class);

	private CsarService csarService;
	private IToscaEngineService toscaEngineService;

	/**
	 * Gets a collection of node templates associated to a given service template.
	 * 
	 * @param csarId
	 *            The id of the CSAR
	 * @param serviceTemplateId
	 *            The id of the service template within the given CSAR
	 * @return A collection of node templates stored within the given service
	 *         template.
	 * @throws NotFoundException
	 *             if the CSAR does not contain the specified service template id
	 */
	public List<NodeTemplateDTO> getNodeTemplatesOfServiceTemplate(String csarId, String serviceTemplateId)
			throws NotFoundException {
		final CSARContent csarContent = this.csarService.findById(csarId);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), serviceTemplateId)) {
			logger.info("Service template \"" + serviceTemplateId + "\" could not be found");
			throw new NotFoundException("Service template \"" + serviceTemplateId + "\" could not be found");
		}

		final List<String> nodeTemplateIds = toscaEngineService
				.getNodeTemplatesOfServiceTemplate(csarContent.getCSARID(), QName.valueOf(serviceTemplateId));
		final List<NodeTemplateDTO> nodeTemplates = Lists.newArrayList();
		NodeTemplateDTO currentNodeTemplate;

		for (final String id : nodeTemplateIds) {
			currentNodeTemplate = createNodeTemplate(csarContent.getCSARID(), QName.valueOf(serviceTemplateId), id);
			nodeTemplates.add(currentNodeTemplate);
		}

		return nodeTemplates;
	}

	/**
	 * Gets the node template specified by its id
	 * 
	 * @param csarId
	 *            The id of the CSAR
	 * @param serviceTemplateId
	 *            The id of the service template within the given CSAR
	 * @param nodeTemplateId
	 *            The id of the node template we want to get and that belongs to the
	 *            specified service template
	 * @return The node template specified by the given id
	 * @throws NotFoundException
	 *             If either the CSAR does not contain the specified service
	 *             template or if the service template does not contain the
	 *             specified node template
	 */
	public NodeTemplateDTO getNodeTemplateById(String csarId, String serviceTemplateId, String nodeTemplateId)
			throws NotFoundException {
		final CSARContent csarContent = this.csarService.findById(csarId);
		final CSARID idOfCsar = csarContent.getCSARID();
		final QName serviceTemplateQName = QName.valueOf(serviceTemplateId);

		if (!this.csarService.hasServiceTemplate(idOfCsar, serviceTemplateId)) {
			logger.info("Service template \"" + serviceTemplateId + "\" could not be found");
			throw new NotFoundException("Service template \"" + serviceTemplateId + "\" could not be found");
		}

		if (!this.toscaEngineService.getNodeTemplatesOfServiceTemplate(idOfCsar, serviceTemplateQName)
				.contains(nodeTemplateId)) {
			logger.info("Node template \"" + nodeTemplateId + "\" could not be found");
			throw new NotFoundException("Service template \"" + nodeTemplateId + "\" could not be found");
		}

		return createNodeTemplate(idOfCsar, serviceTemplateQName, nodeTemplateId);

	}

	/**
	 * Checks whether the specified service template contains a given node template.
	 * It also checks that the CSAR contains the service template
	 * 
	 * @param csarId
	 *            The id of the CSAR
	 * @param serviceTemplateId
	 *            the id of the service template
	 * @param nodeTemplateId
	 *            the id of the node template to check for
	 * @return <code>true</code> when the CSAR contains the service template and the
	 *         service template contains the node template, otherwise
	 *         <code>false</code>
	 */
	public boolean hasNodeTemplate(String csarId, String serviceTemplateId, String nodeTemplateId) {
		try {
			return this.getNodeTemplateIdsOfServiceTemplate(csarId, serviceTemplateId).contains(nodeTemplateId);
		} catch (NotFoundException e) {
			return false;
		}
	}

	private NodeTemplateDTO createNodeTemplate(CSARID csarId, QName serviceTemplateId, String nodeTemplateId) {
		final NodeTemplateDTO currentNodeTemplate = new NodeTemplateDTO();
		currentNodeTemplate.setId(nodeTemplateId);
		currentNodeTemplate.setName(nodeTemplateId);
		currentNodeTemplate.setNodeType(this.toscaEngineService
				.getNodeTypeOfNodeTemplate(csarId, serviceTemplateId, nodeTemplateId).toString());

		return currentNodeTemplate;
	}

	/**
	 * Gets a collection of node template ids associated to a given service
	 * template.
	 * 
	 * @param csarId
	 *            The id of the CSAR
	 * @param serviceTemplateId
	 *            the id of the service template within the given CSAR
	 * @return A collection of node template ids stored within the given service
	 *         template.
	 * @throws NotFoundException
	 *             if the CSAR does not contain the specified service template id
	 */
	private List<String> getNodeTemplateIdsOfServiceTemplate(String csarId, String serviceTemplateId)
			throws NotFoundException {
		final CSARContent csarContent = this.csarService.findById(csarId);
		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), serviceTemplateId)) {
			logger.info("Service template \"" + serviceTemplateId + "\" could not be found");
			throw new NotFoundException("Service template \"" + serviceTemplateId + "\" could not be found");
		}

		return toscaEngineService.getNodeTemplatesOfServiceTemplate(csarContent.getCSARID(),
				QName.valueOf(serviceTemplateId));
	}

	/* Service Injection */
	/*********************/
	public void setCsarService(CsarService csarService) {
		this.csarService = csarService;
	}

	public void setToscaEngineService(IToscaEngineService toscaEngineService) {
		this.toscaEngineService = toscaEngineService;
	}
}
