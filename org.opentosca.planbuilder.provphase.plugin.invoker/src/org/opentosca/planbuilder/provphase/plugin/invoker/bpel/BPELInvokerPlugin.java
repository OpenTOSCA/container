/**
 *
 */
package org.opentosca.planbuilder.provphase.plugin.invoker.bpel;

import java.io.IOException;
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
    private final BPELInvokerPluginHandler handler = new BPELInvokerPluginHandler();

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                    final AbstractImplementationArtifact ia) {
        try {
            return this.handler.handle(context, operation, ia);
        } catch (final Exception e) {
            BPELInvokerPlugin.LOG.error("Couldn't append logic to provphase of Template: "
                + context.getNodeTemplate() != null ? context.getNodeTemplate().getId()
                                                    : context.getRelationshipTemplate().getId(),
                e);
            return false;
        }
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                    final AbstractImplementationArtifact ia,
                    final Map<AbstractParameter, Variable> param2propertyMapping, final boolean appendToPrePhase) {
        String templateId = "";
        boolean isNodeTemplate = false;
        if (context.getNodeTemplate() != null) {
            templateId = context.getNodeTemplate().getId();
            isNodeTemplate = true;
        } else {
            templateId = context.getRelationshipTemplate().getId();
        }

        final Map<String, Variable> inputParams = new HashMap<>();

        for (final AbstractParameter key : param2propertyMapping.keySet()) {
            inputParams.put(key.getName(), param2propertyMapping.get(key));
        }

        try {
            return this.handler.handle(context, templateId, isNodeTemplate, operation.getName(), ia.getInterfaceName(),
                null, inputParams, new HashMap<String, Variable>(), appendToPrePhase);
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                    final AbstractImplementationArtifact ia,
                    final Map<AbstractParameter, Variable> param2propertyMapping) {
        String templateId = "";
        boolean isNodeTemplate = false;
        if (context.getNodeTemplate() != null) {
            templateId = context.getNodeTemplate().getId();
            isNodeTemplate = true;
        } else {
            templateId = context.getRelationshipTemplate().getId();
        }

        final Map<String, Variable> inputParams = new HashMap<>();

        for (final AbstractParameter key : param2propertyMapping.keySet()) {
            inputParams.put(key.getName(), param2propertyMapping.get(key));
        }

        try {
            return this.handler.handle(context, templateId, isNodeTemplate, operation.getName(), ia.getInterfaceName(),
                null, inputParams, new HashMap<String, Variable>(), false);
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Method for adding a single call to the invoker with the given context and specified nodeTemplate
     *
     * @param context the TemplateContext of the Template to call the Operation on
     * @param templateId the Id of the Template the operation belongs to
     * @param isNodeTemplate whether the template is a NodeTemplate or RelationshipTemplate
     * @param operationName the Operation to call on the Template
     * @param interfaceName the name of the interface the operation belongs to
     * @param callbackAddressVarName the name of the variable containing the callbackAddress of this
     *        BuildPlan
     * @param internalExternalPropsInput Mappings from TOSCA Input Parameters to Invoker Parameters
     * @param internalExternalPropsOutput Mappings from TOSCA Output Parameters to Invoker Parameters
     *
     * @return true iff adding logic for Invoker call was successful
     */
    public boolean handle(final BPELPlanContext context, final String templateId, final boolean isNodeTemplate,
                    final String operationName, final String interfaceName, final String callbackAddressVarName,
                    final Map<String, Variable> internalExternalPropsInput,
                    final Map<String, Variable> internalExternalPropsOutput, final boolean appendToPrePhase) {
        try {
            return this.handler.handle(context, templateId, isNodeTemplate, operationName, interfaceName,
                callbackAddressVarName, internalExternalPropsInput, internalExternalPropsOutput, appendToPrePhase);
        } catch (final Exception e) {
            BPELInvokerPlugin.LOG.error("Couldn't append logic to provphase of Template: "
                + context.getNodeTemplate() != null ? context.getNodeTemplate().getId()
                                                    : context.getRelationshipTemplate().getId(),
                e);
            return false;
        }

    }

    /**
     * Method for adding a single call to the invoker with the given context
     *
     * @param context the TemplateContext of the Template to call the Operation on
     * @param operationName the Operation to call on the Template
     * @param interfaceName the name of the interface the operation belongs to
     * @param callbackAddressVarName the name of the variable containing the callbackAddress of this
     *        BuildPlan
     * @param internalExternalPropsInput Mappings from TOSCA Input Parameters to Invoker Parameters
     * @param internalExternalPropsOutput Mappings from TOSCA Output Parameters to Invoker Parameters
     *
     * @return true iff adding logic for Invoker call was successful
     */
    public boolean handle(final BPELPlanContext context, final String operationName, final String interfaceName,
                    final String callbackAddressVarName, final Map<String, Variable> internalExternalPropsInput,
                    final Map<String, Variable> internalExternalPropsOutput) {
        try {
            return this.handler.handle(context, operationName, interfaceName, callbackAddressVarName,
                internalExternalPropsInput, internalExternalPropsOutput, false);
        } catch (final Exception e) {
            BPELInvokerPlugin.LOG.error("Couldn't append logic to provphase of Template: "
                + context.getNodeTemplate() != null ? context.getNodeTemplate().getId()
                                                    : context.getRelationshipTemplate().getId(),
                e);
            return false;
        }
    }

    /**
     * Adds bpel code to the given templateContext, which uploads the given ArtifactReference ref to the
     * given server ip. The destination of the artifact will be a replica of the given csar on the home
     * folder of the selected user. The file must be available from the openTosca container api.
     *
     * @param ref the reference to upload
     * @param templateContext the templateContext to use
     * @param serverIp the ip to upload the file to
     * @param sshUser a variable containing the sshUser value, if null the user will be requested from
     *        the planInput
     * @param sshKey a variable containing the sshKey value, if null the key will be requested from the
     *        planInput
     * @param templateId the templateId the serverIp belongs to
     * @return true iff appending all bpel code was successful
     */
    public boolean handleArtifactReferenceUpload(final AbstractArtifactReference ref,
                    final BPELPlanContext templateContext, final Variable serverIp, final Variable sshUser,
                    final Variable sshKey, final String templateId) {
        try {
            return this.handler.handleArtifactReferenceUpload(ref, templateContext, serverIp, sshUser, sshKey,
                templateId, true);
        } catch (final Exception e) {
            LOG.error("Couldn't load internal files", e);
            return false;
        }
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                    final AbstractImplementationArtifact ia,
                    final Map<AbstractParameter, Variable> param2propertyMapping,
                    final Map<AbstractParameter, Variable> param2PropertyOutputMapping) {
        String templateId = "";
        boolean isNodeTemplate = false;
        if (context.getNodeTemplate() != null) {
            templateId = context.getNodeTemplate().getId();
            isNodeTemplate = true;
        } else {
            templateId = context.getRelationshipTemplate().getId();
        }

        final Map<String, Variable> inputParams = new HashMap<>();
        final Map<String, Variable> outputParams = new HashMap<>();

        for (final AbstractParameter key : param2propertyMapping.keySet()) {
            inputParams.put(key.getName(), param2propertyMapping.get(key));
        }
        for (final AbstractParameter key : param2PropertyOutputMapping.keySet()) {
            outputParams.put(key.getName(), param2PropertyOutputMapping.get(key));
        }

        try {
            return this.handler.handle(context, templateId, isNodeTemplate, operation.getName(), ia.getInterfaceName(),
                null, inputParams, outputParams, false);
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                    final AbstractImplementationArtifact ia,
                    final Map<AbstractParameter, Variable> param2propertyMapping,
                    final Map<AbstractParameter, Variable> param2PropertyOutputMapping,
                    final boolean appendToPrePhase) {
        final Map<String, Variable> inputParams = new HashMap<>();
        final Map<String, Variable> outputParams = new HashMap<>();

        for (final AbstractParameter key : param2propertyMapping.keySet()) {
            inputParams.put(key.getName(), param2propertyMapping.get(key));
        }
        for (final AbstractParameter key : param2PropertyOutputMapping.keySet()) {
            outputParams.put(key.getName(), param2PropertyOutputMapping.get(key));
        }

        try {
            return this.handler.handle(context, operation.getName(), ia.getInterfaceName(), null, inputParams,
                outputParams, appendToPrePhase);
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

}
