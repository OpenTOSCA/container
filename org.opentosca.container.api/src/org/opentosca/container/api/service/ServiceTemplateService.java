package org.opentosca.container.api.service;

import java.util.Set;

import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.opentosca.container.api.controller.ServiceTemplateController;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.model.TBoundaryDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ServiceTemplateService {

	private static Logger logger = LoggerFactory.getLogger(ServiceTemplateController.class);
	private CsarService csarService;
	private IToscaEngineService toscaEngineService;

	public Set<String> getServiceTemplatesOfCsar(final String csarId) {
		final CSARContent csarContent = this.csarService.findById(csarId);

		return this.csarService.getServiceTemplates(csarContent.getCSARID());
	}

	public Document getPropertiesOfServicTemplate(final CSARID csarId, final QName serviceTemplateId) {
		logger.debug("Getting ServiceTemplate properties for " + serviceTemplateId + " in " + csarId);
		final TBoundaryDefinitions boundaryDefs = toscaEngineService.getBoundaryDefinitionsOfServiceTemplate(csarId,
				serviceTemplateId);

		if ((boundaryDefs != null) && (boundaryDefs.getProperties() != null)) {

			logger.debug("Properties found in BoundaryDefinitions for ST {}", serviceTemplateId);
			final Element propertiesElement = (Element) boundaryDefs.getProperties().getAny();

			if ((null != propertiesElement) && (null != propertiesElement.getOwnerDocument())) {
				return propertiesElement.getOwnerDocument();
			} else {
				logger.debug("No properties element found!");
			}
		}

		return null;
	}

	/**
	 * Checks whether the specified csarId exists and that it contains the specified service template
	 * @param csarId
	 * @param serviceTemplateQName
	 * @return the CSARID that corresponds to the passed parameter
	 * @throws NotFoundException if either the CSAR is not found or if does not contain the specified service template.
	 */
	public CSARID checkServiceTemplateExistence(final String csarId, final String serviceTemplateQName)
			throws NotFoundException {
		final CSARContent csarContent = this.csarService.findById(csarId);// throws exception if not found!

		if (!this.csarService.hasServiceTemplate(csarContent.getCSARID(), serviceTemplateQName)) {
			final String msg = "Service template \"" + serviceTemplateQName + "\" could not be found";
			logger.info(msg);
			throw new NotFoundException(msg);
		}

		return csarContent.getCSARID();
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
