package org.opentosca.containerapi.portability;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.instancedata.exception.GenericRestException;
import org.opentosca.containerapi.instancedata.utilities.ArtifactAbsolutizer;
import org.opentosca.containerapi.osgi.servicegetter.PortabilityServiceHandler;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.portability.service.IPortabilityService.ArtifactType;
import org.opentosca.portability.service.model.Artifacts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This represents a List of Implementation and/or DeploymentArtifacts
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 * 
 */
public class ArtifactsListResource {

	private static final Logger LOG = LoggerFactory
			.getLogger(ArtifactsListResource.class);

	@GET
	@Produces(MediaType.APPLICATION_XML)
	// example:
	// localhost:1337/containerapi/portability/artifacts?csarID=TestLifeCycle.csar&targetNamespace=org.opentosca.demo&serviceTemplateID=TestLifeCycleDemo_ServiceTemplate&templateID=TestLifeCycleDemoNodeTemplate&artifactType=DA
	public Response getArtifacts(
			@Context UriInfo uriInfo,
			@QueryParam("csarID") String csarID,
			@QueryParam("targetNamespace") String namespace,
			@QueryParam("serviceTemplateID") String serviceTemplateID,
			@QueryParam("templateID") String templateID,
			@QueryParam("artifactType") String artifactType,
			@QueryParam("deploymentArtifactName") String deploymentArtifactName,
			@QueryParam("interfaceName") String interfaceName,
			@QueryParam("operationName") String operationName) {

		if (Utilities.areEmpty(csarID, namespace, serviceTemplateID,
				templateID, artifactType)) {
			throw new GenericRestException(
					Status.BAD_REQUEST,
					"one of the required parameters: csarID, targetNamespace, serviceTemplateID, templateID, artifactType was not set");
		}

		CSARID csarID_csarID;
		QName qname_serviceTemplateID;
		QName qname_templateID;
		ArtifactType artType = null;

		csarID_csarID = new CSARID(csarID);
		qname_serviceTemplateID = new QName(namespace, serviceTemplateID);
		qname_templateID = new QName(namespace, templateID);
		// map artifactType to enumeration
		artType = ArtifactType.valueOf(artifactType.toUpperCase());

		if (PortabilityServiceHandler.getPortabilityService().isNodeTemplate(
				csarID_csarID, qname_serviceTemplateID, qname_templateID)) {

			LOG.trace(templateID + " is a NodeTemplate.");

			Artifacts artifacts = PortabilityServiceHandler
					.getPortabilityService().getNodeTemplateArtifacts(
							csarID_csarID, qname_serviceTemplateID,
							qname_templateID, artType, deploymentArtifactName,
							interfaceName, operationName);
			// use absolutizer to build absolute paths
			ArtifactAbsolutizer.absolutize(uriInfo, csarID, artifacts);
			// return

			LOG.trace("everything done and ok");

			return Response.ok(artifacts).build();
		} else {

			LOG.trace(templateID + " is a RelationshipTemplate.");

			Artifacts artifacts = PortabilityServiceHandler
					.getPortabilityService().getRelationshipTemplateArtifacts(
							csarID_csarID, qname_serviceTemplateID,
							qname_templateID, artType, deploymentArtifactName,
							interfaceName, operationName);
			// use absolutizer to build absolute paths
			ArtifactAbsolutizer.absolutize(uriInfo, csarID, artifacts);
			// return
			return Response.ok(artifacts).build();
		}
	}
}
