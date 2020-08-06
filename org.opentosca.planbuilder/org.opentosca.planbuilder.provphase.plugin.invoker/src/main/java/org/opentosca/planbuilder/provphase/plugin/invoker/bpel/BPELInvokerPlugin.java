/**
 *
 */
package org.opentosca.planbuilder.provphase.plugin.invoker.bpel;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderCompensationOperationPlugin;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderProvPhaseParamOperationPlugin;
import org.opentosca.planbuilder.core.plugins.choreography.IPlanBuilderChoreographyPlugin;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope.BPELScopePhaseType;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.handlers.BPELInvokerPluginHandler;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.handlers.BPELNotifyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
@Component
public class BPELInvokerPlugin implements IPlanBuilderProvPhaseOperationPlugin<BPELPlanContext>,
    IPlanBuilderProvPhaseParamOperationPlugin<BPELPlanContext>,
    IPlanBuilderCompensationOperationPlugin<BPELPlanContext>,
    IPlanBuilderChoreographyPlugin<BPELPlanContext> {

    private static final Logger LOG = LoggerFactory.getLogger(BPELInvokerPlugin.class);
    private static final String PLUGIN_ID = "OpenTOSCA ProvPhase Plugin for the ServiceInvoker v0.1";

    private final BPELInvokerPluginHandler handler = new BPELInvokerPluginHandler();
    private final BPELNotifyHandler choreohandler = new BPELNotifyHandler();

    public void addLogActivity(final BPELPlanContext context, final String message, final BPELPlanContext.Phase phase) {
        this.handler.appendLOGMessageActivity(context, message, phase);
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                          final AbstractImplementationArtifact ia) {
        try {
            return this.handler.handle(context, operation, ia);
        } catch (final Exception e) {
            LOG.error("Couldn't append logic to provphase of Template: "
                    + context.getNodeTemplate() != null ? context.getNodeTemplate().getId()
                    : context.getRelationshipTemplate().getId(),
                e);
            return false;
        }
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                          final AbstractImplementationArtifact ia,
                          final Map<AbstractParameter, Variable> param2propertyMapping,
                          Element elementToAppendTo) {
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
                inputParams, new HashMap<String, Variable>(), elementToAppendTo);
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
                inputParams, new HashMap<String, Variable>(), context.getProvisioningPhaseElement());
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
                          final String operationName, final String interfaceName,
                          final Map<String, Variable> internalExternalPropsInput,
                          final Map<String, Variable> internalExternalPropsOutput, Element elementToAppendTo) {
        try {
            return this.handler.handle(context, templateId, isNodeTemplate, operationName, interfaceName,
                internalExternalPropsInput, internalExternalPropsOutput, elementToAppendTo);
        } catch (final Exception e) {
            LOG.error("Couldn't append logic to provphase of Template: "
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
                internalExternalPropsInput, internalExternalPropsOutput, context.getProvisioningPhaseElement());
        } catch (final Exception e) {
            LOG.error("Couldn't append logic to provphase of Template: "
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
     * @param infraTemplate the templateId the serverIp belongs to
     * @return true iff appending all bpel code was successful
     */
    public boolean handleArtifactReferenceUpload(final AbstractArtifactReference ref,
                                                 final BPELPlanContext templateContext, final PropertyVariable serverIp,
                                                 final PropertyVariable sshUser, final PropertyVariable sshKey,
                                                 final AbstractNodeTemplate infraTemplate, Element elementToAppendTo) {
        try {
            return this.handler.handleArtifactReferenceUpload(ref, templateContext, serverIp, sshUser, sshKey,
                infraTemplate, elementToAppendTo);
        } catch (final Exception e) {
            LOG.error("Couldn't load internal files", e);
            return false;
        }
    }

    @Override
    public boolean handleSendNotify(final BPELPlanContext context) {

        // Currently stuff like storeSaveEndpoint for freezing is breaking the matching of all possible
        // operations here, therefore we send all available properties -> TODO/FIXME
        final Map<String, PropertyVariable> propMatching =
            this.choreohandler.matchOperationParamertsToProperties(context);

        final Collection<PropertyVariable> propertiesToSend = new HashSet<>();
        context.getNodeTemplates().forEach(x -> propertiesToSend.addAll(context.getPropertyVariables(x)));
        final Map<String, Variable> params = this.choreohandler.mapToParamMap(propertiesToSend);

        this.choreohandler.addChoreographyParameters(context, params);

        try {
            return this.choreohandler.handleSendNotify(context, params, context.getProvisioningPhaseElement());
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean handleReceiveNotify(final BPELPlanContext context) {
        final Collection<PropertyVariable> properties = this.choreohandler.getPartnerPropertyVariables(context);

        final Map<String, Variable> params = this.choreohandler.mapToParamMap(properties);

        this.choreohandler.addChoreographyParameters(context, params);

        try {
            return this.choreohandler.handleReceiveNotify(context, params, context.getProvisioningPhaseElement());
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean canHandleSendNotify(final BPELPlanContext context) {
        return this.choreohandler.isValidForSendNotify(context);
    }

    @Override
    public boolean canHandleReceiveNotify(final BPELPlanContext context) {
        return this.choreohandler.isValidForReceiveNotify(context);
    }

    @Override
    public boolean canHandleNotifyPartners(final BPELPlanContext context) {
        // we can do this always, basically
        return true;
    }

    @Override
    public boolean handleNotifyPartners(final BPELPlanContext context) {

        try {
            return this.choreohandler.handleNotifyPartners(context);
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
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
                inputParams, outputParams, context.getProvisioningPhaseElement());
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                          final AbstractImplementationArtifact ia,
                          final Map<AbstractParameter, Variable> param2propertyMapping,
                          final Map<AbstractParameter, Variable> param2PropertyOutputMapping, Element elementToAppendTo) {
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
                outputParams, elementToAppendTo);
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                          final AbstractImplementationArtifact ia,
                          final Map<AbstractParameter, Variable> param2propertyMapping,
                          final AbstractOperation compensationOperation,
                          final AbstractImplementationArtifact compensationIa,
                          final Map<AbstractParameter, Variable> compensationParam2VariableMapping) {
        // TODO Auto-generated method stub

        return false;
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                          final AbstractImplementationArtifact ia,
                          final Map<AbstractParameter, Variable> param2propertyMapping,
                          final AbstractOperation compensationOperation,
                          final AbstractImplementationArtifact compensationIa,
                          final Map<AbstractParameter, Variable> compensationParam2VariableMapping,
                          final BPELScopePhaseType phase) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                          final AbstractImplementationArtifact ia,
                          final Map<AbstractParameter, Variable> param2propertyMapping,
                          final Map<AbstractParameter, Variable> param2PropertyOutputMapping,
                          final AbstractOperation compensationOperation,
                          final AbstractImplementationArtifact compensationIa,
                          final Map<AbstractParameter, Variable> compensationParam2VariableMapping) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractOperation operation,
                          final AbstractImplementationArtifact ia,
                          final Map<AbstractParameter, Variable> param2propertyMapping,
                          final Map<AbstractParameter, Variable> param2PropertyOutputMapping,
                          final AbstractOperation compensationOperation,
                          final AbstractImplementationArtifact compensationIa,
                          final Map<AbstractParameter, Variable> compensationParam2VariableMapping,
                          final BPELScopePhaseType phase) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canHandle(final QName operationArtifactType) {
        return true;
    }

    @Override
    public String getID() {
        return PLUGIN_ID;
    }
}
