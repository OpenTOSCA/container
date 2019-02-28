package org.opentosca.container.api.service;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ServiceTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTemplateService.class);

    @Inject
    private CsarStorageService csarStorage;

    public Set<String> getServiceTemplatesOfCsar(final String csarId) {
        final Csar csarContent = this.csarStorage.findById(new CsarId(csarId));
        return csarContent.serviceTemplates().stream().map(TServiceTemplate::getId).collect(Collectors.toSet());
    }

    public Document getPropertiesOfServiceTemplate(final CsarId csarId, final QName serviceTemplateId) {
        logger.debug("Getting ServiceTemplate properties for " + serviceTemplateId + " in " + csarId);
        final Csar csarContent = this.csarStorage.findById(csarId);
        final TBoundaryDefinitions boundaryDefs = csarContent.serviceTemplates().stream()
            // FIXME that predicate seems problematic
            .filter(template -> template.getId().equals(serviceTemplateId.toString()))
            .findFirst()
            .map(template -> template.getBoundaryDefinitions())
            .orElseThrow(() -> new NoSuchElementException(String.format("Could not find serviceTemplate with id [%s] on csar [%s]", serviceTemplateId, csarId)));
        
        if (boundaryDefs != null && boundaryDefs.getProperties() != null) {
            logger.debug("Properties found in BoundaryDefinitions for ST {}", serviceTemplateId);
            final Element propertiesElement = (Element) boundaryDefs.getProperties().getAny();
            if (null != propertiesElement && null != propertiesElement.getOwnerDocument()) {
                return propertiesElement.getOwnerDocument();
            } else {
                logger.debug("No properties element found!");
            }
        }

        return null;
    }

    /**
     * Checks whether the specified csarId exists and that it contains the specified service template
     *
     * @param csarId
     * @param serviceTemplateQName
     * @return the CSARID that corresponds to the passed parameter
     * @throws NotFoundException if either the CSAR is not found or if does not contain the specified
     *         service template.
     */
    public CsarId checkServiceTemplateExistence(final String csarId,
                                                final String serviceTemplateQName) throws NotFoundException {
        final CsarId assumedId = new CsarId(csarId);
        final Csar csarContent = this.csarStorage.findById(assumedId);// throws exception if not found!

        if (!csarContent.serviceTemplates().stream().anyMatch(template -> template.getId().equals(serviceTemplateQName))) {
            final String msg = "Service template \"" + serviceTemplateQName + "\" could not be found";
            logger.info(msg);
            throw new NotFoundException(msg);
        }

        return assumedId;
    }

    /* Service Injection */
    /*********************/
    public void bindStorage(final CsarStorageService storage) {
        this.csarStorage = storage;
    }
}
