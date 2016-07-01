/**
 *
 */
package org.opentosca.planbuilder.provphase.plugin.invoker;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.plugins.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.handlers.Handler;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Plugin implements IPlanBuilderProvPhaseOperationPlugin {

	private final static Logger LOG = LoggerFactory.getLogger(Plugin.class);
	private Handler handler = new Handler();

	@Override
	public String getID() {
		return "OpenTOSCA ProvPhase Plugin for the ServiceInvoker v0.1";
	}

	@Override
	public boolean canHandle(QName operationArtifactType) {
		// we can handle every type except scripts
		if (operationArtifactType
				.equals(new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ScriptArtifact"))
				| operationArtifactType.equals(new QName("http://example.com/ToscaTypes", "Ansible"))) {
			return false;
		}
		return true;
	}

	@Override
	public boolean handle(TemplatePlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia) {
		try {
			return this.handler.handle(context, operation, ia);
		} catch (IOException e) {
			Plugin.LOG.error(("Couldn't append logic to provphase of Template: " + context.getNodeTemplate()) != null
					? context.getNodeTemplate().getId() : context.getRelationshipTemplate().getId(), e);
			return false;
		}
	}

	/**
	 * Method for adding a single call to the invoker with the given context
	 *
	 * @param context
	 *            the TemplateContext of the Template to call the Operation on
	 * @param operationName
	 *            the Operation to call on the Template
	 * @param interfaceName
	 *            the name of the interface the operation belongs to
	 * @param callbackAddressVarName
	 *            the name of the variable containing the callbackAddress of
	 *            this BuildPlan
	 * @param internalExternalPropsInput
	 *            Mappings from TOSCA Input Parameters to Invoker Parameters
	 * @param internalExternalPropsOutput
	 *            Mappings from TOSCA Output Parameters to Invoker Parameters
	 *
	 * @return true iff adding logic for Invoker call was successful
	 */
	public boolean handle(TemplatePlanContext context, String operationName, String interfaceName,
			String callbackAddressVarName, Map<String, Variable> internalExternalPropsInput,
			Map<String, Variable> internalExternalPropsOutput) {
		try {
			return this.handler.handle(context, operationName, interfaceName, callbackAddressVarName,
					internalExternalPropsInput, internalExternalPropsOutput, false);
		} catch (IOException e) {
			Plugin.LOG.error(("Couldn't append logic to provphase of Template: " + context.getNodeTemplate()) != null
					? context.getNodeTemplate().getId() : context.getRelationshipTemplate().getId(), e);
			return false;
		}
	}

	/**
	 * Method for adding a single call to the invoker with the given context and
	 * specified nodeTemplate
	 *
	 * @param context
	 *            the TemplateContext of the Template to call the Operation on
	 * @param templateId
	 *            the Id of the Template the operation belongs to
	 * @param isNodeTemplate
	 *            whether the template is a NodeTemplate or RelationshipTemplate
	 * @param operationName
	 *            the Operation to call on the Template
	 * @param interfaceName
	 *            the name of the interface the operation belongs to
	 * @param callbackAddressVarName
	 *            the name of the variable containing the callbackAddress of
	 *            this BuildPlan
	 * @param internalExternalPropsInput
	 *            Mappings from TOSCA Input Parameters to Invoker Parameters
	 * @param internalExternalPropsOutput
	 *            Mappings from TOSCA Output Parameters to Invoker Parameters
	 *
	 * @return true iff adding logic for Invoker call was successful
	 */
	public boolean handle(TemplatePlanContext context, String templateId, boolean isNodeTemplate, String operationName,
			String interfaceName, String callbackAddressVarName, Map<String, Variable> internalExternalPropsInput,
			Map<String, Variable> internalExternalPropsOutput, boolean appendToPrePhase) {
		try {
			return this.handler.handle(context, templateId, isNodeTemplate, operationName, interfaceName,
					callbackAddressVarName, internalExternalPropsInput, internalExternalPropsOutput, appendToPrePhase);
		} catch (IOException e) {
			Plugin.LOG.error(("Couldn't append logic to provphase of Template: " + context.getNodeTemplate()) != null
					? context.getNodeTemplate().getId() : context.getRelationshipTemplate().getId(), e);
			return false;
		}

	}

	/**
	 * Adds bpel code to the given templateContext, which uploads the given
	 * ArtifactReference ref to the given server ip. The destination of the
	 * artifact will be a replica of the given csar on the home folder of the
	 * selected user. The file must be available from the openTosca container
	 * api.
	 *
	 * @param ref
	 *            the reference to upload
	 * @param templateContext
	 *            the templateContext to use
	 * @param serverIp
	 *            the ip to upload the file to
	 * @param sshUser
	 *            a variable containing the sshUser value, if null the user will
	 *            be requested from the planInput
	 * @param sshKey
	 *            a variable containing the sshKey value, if null the key will
	 *            be requested from the planInput
	 * @param templateId
	 *            the templateId the serverIp belongs to
	 * @return true iff appending all bpel code was successful
	 */
	public boolean handleArtifactReferenceUpload(AbstractArtifactReference ref, TemplatePlanContext templateContext,
			Variable serverIp, Variable sshUser, Variable sshKey, String templateId) {
		try {
			return this.handler.handleArtifactReferenceUpload(ref, templateContext, serverIp, sshUser, sshKey,
					templateId, true);
		} catch (IOException e) {
			LOG.error("Couldn't load internal files", e);
			return false;
		}
	}

}
