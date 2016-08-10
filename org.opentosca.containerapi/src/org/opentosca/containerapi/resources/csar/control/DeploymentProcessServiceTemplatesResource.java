package org.opentosca.containerapi.resources.csar.control;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentProcessServiceTemplatesResource {
	
	private static final Logger LOG = LoggerFactory.getLogger(DeploymentProcessOperationsResource.class);
	
	private String sep = "&";
	private CSARID csarid;
	
	
	public DeploymentProcessServiceTemplatesResource(CSARID csarid) {
		this.csarid = csarid;
		DeploymentProcessServiceTemplatesResource.LOG.info("{} created: {}", this.getClass(), this);
		
	}
	
	/**
	 * 
	 * @return all available Operations as String separated by "&"
	 */
	@GET
	@Produces(ResourceConstants.TEXT_PLAIN)
	public Response getServiceTemplates() {
		DeploymentProcessServiceTemplatesResource.LOG.info("Get Request on DeploymentProcessOperationsResource");
		String sTemplates = "";
		List<QName> ServiceTemplates = IOpenToscaControlServiceHandler.getOpenToscaControlService().getAllContainedServiceTemplates(this.csarid);
		for (QName serviceTemplate : ServiceTemplates) {
			sTemplates = sTemplates + this.sep + serviceTemplate.toString();
		}
		sTemplates = sTemplates.substring(1);
		
		// for (DeploymentProcessOperation operation : this.operations) {
		// operations = operations + this.sep + operation.toString();
		// }
		return Response.ok(sTemplates).build();
	}
}
