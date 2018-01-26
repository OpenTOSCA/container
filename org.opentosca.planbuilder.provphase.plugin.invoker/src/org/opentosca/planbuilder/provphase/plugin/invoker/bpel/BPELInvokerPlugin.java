/**
 *
 */
package org.opentosca.planbuilder.provphase.plugin.invoker.bpel;

import java.util.HashMap;
import java.util.Map;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.handlers.BPELInvokerPluginHandler;
import org.opentosca.planbuilder.provphase.plugin.invoker.core.InvokerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class BPELInvokerPlugin extends InvokerPlugin<BPELPlanContext> {

	private final static Logger LOG = LoggerFactory.getLogger(BPELInvokerPlugin.class);
	private BPELInvokerPluginHandler handler = new BPELInvokerPluginHandler();

	@Override
	public boolean handle(BPELPlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia) {
		try {
			return this.handler.handle(context, operation, ia);
		} catch (Exception e) {
			BPELInvokerPlugin.LOG
					.error(("Couldn't append logic to provphase of Template: " + context.getNodeTemplate()) != null
							? context.getNodeTemplate().getId()
							: context.getRelationshipTemplate().getId(), e);
			return false;
		}
	}

	@Override
	public boolean handle(BPELPlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia,
			Map<AbstractParameter, Variable> param2propertyMapping) {
		String templateId = "";
		boolean isNodeTemplate = false;
		if (context.getNodeTemplate() != null) {
			templateId = context.getNodeTemplate().getId();
			isNodeTemplate = true;
		} else {
			templateId = context.getRelationshipTemplate().getId();
		}

		Map<String, Variable> inputParams = new HashMap<String, Variable>();

		for (AbstractParameter key : param2propertyMapping.keySet()) {
			inputParams.put(key.getName(), param2propertyMapping.get(key));
		}

		try {
			return this.handler.handle(context, templateId, isNodeTemplate, operation.getName(), ia.getInterfaceName(),
					null, inputParams, new HashMap<String, Variable>(), false);
		} catch (Exception e) {
			e.printStackTrace();
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
	 *            the name of the variable containing the callbackAddress of this
	 *            BuildPlan
	 * @param internalExternalPropsInput
	 *            Mappings from TOSCA Input Parameters to Invoker Parameters
	 * @param internalExternalPropsOutput
	 *            Mappings from TOSCA Output Parameters to Invoker Parameters
	 *
	 * @return true iff adding logic for Invoker call was successful
	 */
	public boolean handle(BPELPlanContext context, String templateId, boolean isNodeTemplate, String operationName,
			String interfaceName, String callbackAddressVarName, Map<String, Variable> internalExternalPropsInput,
			Map<String, Variable> internalExternalPropsOutput, boolean appendToPrePhase) {
		try {
			return this.handler.handle(context, templateId, isNodeTemplate, operationName, interfaceName,
					callbackAddressVarName, internalExternalPropsInput, internalExternalPropsOutput, appendToPrePhase);
		} catch (Exception e) {
			BPELInvokerPlugin.LOG
					.error(("Couldn't append logic to provphase of Template: " + context.getNodeTemplate()) != null
							? context.getNodeTemplate().getId()
							: context.getRelationshipTemplate().getId(), e);
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
	 *            the name of the variable containing the callbackAddress of this
	 *            BuildPlan
	 * @param internalExternalPropsInput
	 *            Mappings from TOSCA Input Parameters to Invoker Parameters
	 * @param internalExternalPropsOutput
	 *            Mappings from TOSCA Output Parameters to Invoker Parameters
	 *
	 * @return true iff adding logic for Invoker call was successful
	 */
	public boolean handle(BPELPlanContext context, String operationName, String interfaceName,
			String callbackAddressVarName, Map<String, Variable> internalExternalPropsInput,
			Map<String, Variable> internalExternalPropsOutput) {
		try {
			return this.handler.handle(context, operationName, interfaceName, callbackAddressVarName,
					internalExternalPropsInput, internalExternalPropsOutput, false);
		} catch (Exception e) {
			BPELInvokerPlugin.LOG
					.error(("Couldn't append logic to provphase of Template: " + context.getNodeTemplate()) != null
							? context.getNodeTemplate().getId()
							: context.getRelationshipTemplate().getId(), e);
			return false;
		}
	}

	/**
	 * Adds bpel code to the given templateContext, which uploads the given
	 * ArtifactReference ref to the given server ip. The destination of the artifact
	 * will be a replica of the given csar on the home folder of the selected user.
	 * The file must be available from the openTosca container api.
	 *
	 * @param ref
	 *            the reference to upload
	 * @param templateContext
	 *            the templateContext to use
	 * @param serverIp
	 *            the ip to upload the file to
	 * @param sshUser
	 *            a variable containing the sshUser value, if null the user will be
	 *            requested from the planInput
	 * @param sshKey
	 *            a variable containing the sshKey value, if null the key will be
	 *            requested from the planInput
	 * @param templateId
	 *            the templateId the serverIp belongs to
	 * @return true iff appending all bpel code was successful
	 */
	public boolean handleArtifactReferenceUpload(AbstractArtifactReference ref, BPELPlanContext templateContext,
			Variable serverIp, Variable sshUser, Variable sshKey, String templateId) {
		try {
			return this.handler.handleArtifactReferenceUpload(ref, templateContext, serverIp, sshUser, sshKey,
					templateId, true);
		} catch (Exception e) {
			LOG.error("Couldn't load internal files", e);
			return false;
		}
	}

}
