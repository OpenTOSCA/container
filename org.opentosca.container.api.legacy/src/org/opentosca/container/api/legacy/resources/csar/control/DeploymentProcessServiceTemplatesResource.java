package org.opentosca.container.api.legacy.resources.csar.control;

import static org.opentosca.container.api.legacy.osgi.servicegetter.IOpenToscaControlServiceHandler.getOpenToscaControlService;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentProcessServiceTemplatesResource {
    private static final Logger LOG = LoggerFactory.getLogger(DeploymentProcessOperationsResource.class);

    private final String sep = "&";
    private final CSARID csarid;

    public DeploymentProcessServiceTemplatesResource(final CSARID csarid) {
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
        final List<QName> ServiceTemplates = getOpenToscaControlService().getAllContainedServiceTemplates(this.csarid);
        for (final QName serviceTemplate : ServiceTemplates) {
            sTemplates = sTemplates + this.sep + serviceTemplate.toString();
        }
        sTemplates = sTemplates.substring(1);
        return Response.ok(sTemplates).build();
    }
}
