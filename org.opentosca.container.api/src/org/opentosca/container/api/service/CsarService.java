package org.opentosca.container.api.service;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.selfservice.Application;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class CsarService {

	private static Logger logger = LoggerFactory.getLogger(CsarService.class);
	
	private ICoreFileService fileService;
	
	private IToscaEngineService engineService;
	
	
	/**
	 * Loads all available CSARs as {@link CSARContent}
	 *
	 * @return Set of {@link CSARContent} objects
	 */
	public Set<CSARContent> findAll() {
		logger.debug("Requesting all CSARs...");
		final Set<CSARContent> csarSet = Sets.newHashSet();
		for (final CSARID id : this.fileService.getCSARIDs()) {
			try {
				csarSet.add(this.findById(id));
			} catch (final Exception e) {
				logger.error("Error while loading CSAR with ID \"{}\": {}", id, e.getMessage(), e);
				throw new ServerErrorException(Response.serverError().build());
			}
		}
		return csarSet;
	}
	
	/**
	 * Loads a CSAR as {@link CSARContent} by a given id
	 *
	 * @param id The id of the CSAR
	 * @return The CSAR as {@link CSARContent}
	 */
	public CSARContent findById(final CSARID id) {
		logger.debug("Requesting CSAR \"{}\"...", id);
		try {
			return this.fileService.getCSAR(id);
		} catch (final UserException e) {
			logger.info("CSAR \"" + id.getFileName() + "\" could not be found");
			throw new NotFoundException("CSAR \"" + id.getFileName() + "\" could not be found");
		}
	}
	
	/**
	 * Loads a CSAR as {@link CSARContent} by a given id
	 *
	 * @param id The id of the CSAR
	 * @return The CSAR as {@link CSARContent}
	 */
	public CSARContent findById(final String id) {
		return this.findById(new CSARID(id));
	}

	/**
	 * Reads the self-service metadata of a CSAR and returns it as a Java object
	 *
	 * @param csarContent The content object of the CSAR
	 * @return The self-service metadata as Java object
	 */
	public Application getSelfserviceMetadata(final CSARContent csarContent) {
		try (final InputStream is = csarContent.getDirectory("SELFSERVICE-Metadata").getFile("data.xml").getFileAsInputStream()) {
			final JAXBContext jaxbContext = JAXBContext.newInstance(Application.class);
			final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			return (Application) jaxbUnmarshaller.unmarshal(is);
		} catch (final Exception e) {
			logger.error("Could not serialize data.xml from CSAR", e);
			throw new ServerErrorException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Returns a set of strings representing service templates contained in a
	 * CSAR
	 *
	 * @param id The id of the CSAR
	 * @return A Set of String objects representing service templates
	 */
	public Set<String> getServiceTemplates(final CSARID id) {
		logger.debug("Requesting ServiceTemplates of CSAR \"{}\"...", id);
		final List<QName> result = this.engineService.getServiceTemplatesInCSAR(id);
		return result.stream().filter(Objects::nonNull).map(item -> item.toString()).collect(Collectors.toSet());
	}
	
	/**
	 * Utility to check if a given CSAR has a certain ServiceTemplate attached
	 *
	 * @param id The id of the CSAR
	 * @param name The QName as string of the ServiceTemplate
	 * @return true or false
	 */
	public boolean hasServiceTemplate(final CSARID id, final String name) {
		return this.getServiceTemplates(id).contains(name);
	}
	
	public void setFileService(final ICoreFileService fileService) {
		this.fileService = fileService;
	}
	
	public void setEngineService(final IToscaEngineService engineService) {
		this.engineService = engineService;
	}
}
