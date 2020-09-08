package org.opentosca.container.core.plan;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.engine.management.IManagementBus;
import org.opentosca.container.core.model.choreography.SituationRule;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.IPlanInvocationEngine;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * The Implementation of the Engine. Also deals with OSGI events for communication with the mock-up Servicebus.
 * <p>
 * Copyright 2013 Christian Endres
 *
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 */
@Service
@NonNullByDefault
public class PlanInvocationEngine implements IPlanInvocationEngine {

    private static final Logger LOG = LoggerFactory.getLogger(PlanInvocationEngine.class);
    private static final PlanInstanceRepository planRepo = new PlanInstanceRepository();

    private final IManagementBus managementBus;
    private final CsarStorageService csarStorage;
    private final RulesChecker rulesChecker;
    private final ChoreographyHandler choreographyHandler;

    @Inject
    public PlanInvocationEngine(IManagementBus managementBus,
                                CsarStorageService csarStorage,
                                RulesChecker rulesChecker,
                                ChoreographyHandler choreographyHandler) {
        this.managementBus = managementBus;
        this.csarStorage = csarStorage;
        this.rulesChecker = rulesChecker;
        this.choreographyHandler = choreographyHandler;
    }

    @Override
    public String createCorrelationId() {
        // generate CorrelationId for the plan execution
        while (true) {
            final String correlationId = String.valueOf(System.currentTimeMillis());

            try {
                PlanInstance instance = planRepo.findByCorrelationId(correlationId);
                if (instance == null) {
                    return correlationId;
                }
                this.LOG.debug("CorrelationId {} already in use.", correlationId);
            } catch (final NoResultException e) {
                return correlationId;
            }
        }
    }

    @Override
    public void invokePlan(CsarId csarID, QName serviceTemplateId, long serviceTemplateInstanceID, TPlanDTO plan, String correlationID) {
        final Csar csar = csarStorage.findById(csarID);
        final TServiceTemplate serviceTemplate;
        try {
            serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateId);
        } catch (NotFoundException e) {
            LOG.warn("Could not find ServiceTemplate associated with id {} in given CSAR {}!",
                serviceTemplateId, csar.id().csarName());
            // FIXME mark plan invocation as failed?
            return;
        }
        invokePlan(csarID, serviceTemplate, serviceTemplateInstanceID, plan, correlationID);
    }

    @Override
    public void invokePlan(final CsarId csarID, final TServiceTemplate serviceTemplate, long serviceTemplateInstanceID,
                           final TPlanDTO givenPlan, String correlationId) {

        final Csar csar = csarStorage.findById(csarID);

        if (rulesChecker.areRulesContained(csar)) {
            if (rulesChecker.check(csar, serviceTemplate, givenPlan.getInputParameters())) {
                LOG.debug("Deployment Rules are fulfilled. Continuing the provisioning.");
            } else {
                LOG.debug("Deployment Rules are not fulfilled. Aborting the provisioning.");
                return;
            }
        }

        if (choreographyHandler.isChoreography(serviceTemplate)) {
            LOG.debug("ServiceTemplate is part of choreography!");

            // add general header fields which are required to notify partners
            Map<String, Object> eventValues = new HashMap<>();
            eventValues.put("CSARID", csarID);
            eventValues.put("SERVICETEMPLATEID_QNAME", new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId()));
            eventValues.put("PLANCORRELATIONID_STRING", createCorrelationId());
            eventValues.put("APP_CHOREO_ID", choreographyHandler.getAppChorId(serviceTemplate));

            // select the participating partners of the choreography based on the available situation rules
            List<SituationRule> situationRules = choreographyHandler.getSituationRules(serviceTemplate);

            if (situationRules.isEmpty()) {
                // notify all defined partners for choreographies without selection rules
                LOG.debug("No situation rules defined. Processing choreography with all partners!");
                eventValues.put("CHOREOGRAPHY_PARTNERS",  choreographyHandler.getPartnerEndpoints(serviceTemplate).stream().map(TTag::getName).collect(Collectors.joining(",")));
            } else{
                LOG.debug("Found {} situation rules for choreography. Selecting partners by rules...", situationRules.size());
                // TODO: make decision for participants
                eventValues.put("CHOREOGRAPHY_PARTNERS", choreographyHandler.getPartnerEndpoints(serviceTemplate).stream().map(TTag::getName).collect(Collectors.joining(",")));
            }

            managementBus.notifyPartners(eventValues);
            return;
        }

        LOG.info("Invoke the Plan {} of type {} of CSAR {}", givenPlan.getId(), givenPlan.getPlanType(), csarID);

        final HashMap<String, String> input = new HashMap<>();
        for (final TParameterDTO param : givenPlan.getInputParameters().getInputParameter()) {
            if (Objects.isNull(param.getValue())) {
                input.put(param.getName(), "");
            } else {
                input.put(param.getName(), param.getValue());
            }
        }

        // send the message to the service bus
        final Map<String, Object> eventValues = new Hashtable<>();
        eventValues.put("CSARID", csarID);
        eventValues.put("SERVICETEMPLATEID", QName.valueOf(serviceTemplate.getId()));
        eventValues.put("PLANID", givenPlan.getId());
        eventValues.put("PLANLANGUAGE", givenPlan.getPlanLanguage());
        eventValues.put("SERVICEINSTANCEID", serviceTemplateInstanceID);
        eventValues.put("MESSAGEID", correlationId);
        // the planRef is an xsd:IDREF as per Tosca-v1.0.xsd, and therefore an unqualified name
        // FIXME adapt TPlanDTO to match Tosca XSD
        TExportedOperation operation = ToscaEngine.getReferencingOperationWithin(serviceTemplate, givenPlan.getId().getLocalPart());
        eventValues.put("OPERATIONNAME", operation.getName());
        eventValues.put("INPUTS", input);

        // no callback, because plan output updates are handled in management bus
        managementBus.invokePlan(eventValues);
    }
}
