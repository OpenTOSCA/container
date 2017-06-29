package org.opentosca.container.api.service;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.ServerErrorException;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.PlanDTO;
import org.opentosca.container.api.dto.PlanInstanceDTO;
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.BuildCorrelationToInstanceMapping;
import org.opentosca.container.control.IOpenToscaControlService;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.impl.plan.CorrelationHandler;
import org.opentosca.container.core.impl.plan.PlanLogHandler;
import org.opentosca.container.core.impl.plan.ServiceProxy;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.model.instance.State;
import org.opentosca.container.core.service.ICSARInstanceManagementService;
import org.opentosca.container.core.service.IPlanLogHandler;
import org.opentosca.container.core.tosca.extension.PlanInvocationEvent;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.extension.TParameter;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.opentosca.container.core.tosca.model.TPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class PlanService {

	private static Logger logger = LoggerFactory.getLogger(PlanService.class);

	private static final List<PlanTypes> ALL_PLAN_TYPES = Lists.newArrayList(PlanTypes.APPLICATION, PlanTypes.BUILD, PlanTypes.OTHERMANAGEMENT, PlanTypes.TERMINATION);
	
	@SuppressWarnings("unused")
	private IToscaEngineService engineService;

	private IToscaReferenceMapper referenceMapper;

	private IOpenToscaControlService controlService;

	private ICSARInstanceManagementService csarInstanceService;
	
	private final CorrelationHandler correlationHandler = ServiceProxy.correlationHandler;

	private final IPlanLogHandler logHandler = PlanLogHandler.instance;

	private final BuildCorrelationToInstanceMapping instanceMapper = BuildCorrelationToInstanceMapping.instance;
	
	
	public List<TPlan> getPlansByType(final List<PlanTypes> planTypes, final CSARID id) {
		logger.debug("Requesting plans of type \"{}\" for CSAR \"{}\"...", planTypes, id);
		final List<TPlan> plans = Lists.newArrayList();
		final Map<PlanTypes, LinkedHashMap<QName, TPlan>> plansOfCsar = this.referenceMapper.getCSARIDToPlans(id);
		for (final PlanTypes planType : planTypes) {
			final LinkedHashMap<QName, TPlan> plansOfType = plansOfCsar.get(planType);
			if (plansOfType == null) {
				logger.warn("CSAR \"" + id.getFileName() + "\" does not have a plan of type \"" + planType.toString() + "\"");
				continue;
			}
			plans.addAll(plansOfType.values());
		}
		return plans;
	}

	public TPlan getPlan(final String name, final CSARID id) {
		final List<TPlan> plans = this.getPlansByType(ALL_PLAN_TYPES, id);
		for (final TPlan plan : plans) {
			if ((plan.getId() != null) && plan.getId().equalsIgnoreCase(name)) {
				return plan;
			}
		}
		return null;
	}

	public String invokePlan(final CSARID csarId, final QName serviceTemplate, final TPlan plan, final List<TParameter> parameters) {

		final PlanDTO dto = new PlanDTO(plan);
		
		final String namespace = this.referenceMapper.getNamespaceOfPlan(csarId, plan.getId());
		dto.setId(new QName(namespace, plan.getId()).toString());
		dto.setInputParameters(parameters);
		
		try {
			return this.controlService.invokePlanInvocation(csarId, serviceTemplate, -1, PlanDTO.Converter.convert(dto));
		} catch (final UnsupportedEncodingException e) {
			throw new ServerErrorException(500, e);
		}
	}

	public List<PlanInstanceDTO> getPlanInstances(final List<ServiceInstance> serviceInstances, final List<PlanTypes> planTypes) {
		
		final List<PlanInstanceDTO> planInstances = Lists.newArrayList();
		
		for (final ServiceInstance si : serviceInstances) {
			
			final ServiceTemplateInstanceID id = new ServiceTemplateInstanceID(si.getCSAR_ID(), si.getServiceTemplateID(), si.getDBId());
			final List<String> correlations = this.correlationHandler.getActiveCorrelationsOfInstance(id);
			
			for (final String correlationId : correlations) {
				final TPlanDTO plan = this.correlationHandler.getPlanDTOForCorrelation(id, correlationId);
				if (plan == null) {
					continue;
				}
				final PlanDTO planDto = PlanDTO.Converter.convert(plan);
				
				if (planTypes.contains(PlanTypes.isPlanTypeURI(planDto.getPlanType()))) {
					final PlanInstanceDTO pi = new PlanInstanceDTO();
					
					pi.setId(correlationId);
					pi.setState(State.Plan.UNKNOWN);
					
					final List<PlanInstanceDTO.LogEntry> logs = this.logHandler.getLogsOfPlanInstance(correlationId).entrySet().stream().map(e -> {
						return new PlanInstanceDTO.LogEntry(e.getKey(), e.getValue());
					}).collect(Collectors.toList());
					pi.setLogs(logs);
					
					PlanInvocationEvent event;
					
					// Collect output parameters
					event = this.csarInstanceService.getPlanFromHistory(correlationId);
					if (event != null) {
						pi.setOutput(event.getOutputParameter().stream().map(p -> {
							return new TParameter(p);
						}).collect(Collectors.toList()));
					}
					
					// Determine state of plan instance
					event = this.csarInstanceService.getPlanForCorrelationId(correlationId);
					if (event != null) {
						pi.setState(this.determinePlanInstanceState(id.getCsarId(), correlationId));
						if (event.isHasFailed()) {
							pi.setState(State.Plan.FAILED);
						}
					}

					planInstances.add(pi);
				}
			}
		}
		
		return planInstances;
	}

	public boolean hasPlan(final CSARID csarId, final List<PlanTypes> planTypes, final String plan) {
		final TPlan p = this.getPlan(plan, csarId);
		if (p == null) {
			return false;
		}
		if (planTypes.contains(PlanTypes.isPlanTypeURI(p.getPlanType()))) {
			return true;
		}
		return false;
	}

	public boolean hasPlanInstance(final String correlationId) {
		return this.instanceMapper.knowsCorrelationId(correlationId);
	}
	
	public Integer getServiceTemplateInstanceId(final String correlationId) {
		if (this.hasPlanInstance(correlationId)) {
			return this.instanceMapper.getServiceTemplateInstanceIdForBuildPlanCorrelation(correlationId);
		}
		return null;
	}
	
	private State.Plan determinePlanInstanceState(final CSARID csarId, final String correlationId) {
		final List<String> finishedCorrelations = this.csarInstanceService.getFinishedCorrelations(csarId);
		final List<String> activeCorrelations = this.csarInstanceService.getActiveCorrelations(csarId);
		if ((finishedCorrelations != null) && finishedCorrelations.contains(correlationId)) {
			return State.Plan.FINISHED;
		}
		if ((activeCorrelations != null) && activeCorrelations.contains(correlationId)) {
			return State.Plan.RUNNING;
		}
		return State.Plan.UNKNOWN;
	}

	public void setEngineService(final IToscaEngineService engineService) {
		this.engineService = engineService;
		// We cannot inject an instance of {@link IToscaReferenceMapper} since
		// it is manually created in our default implementation of {@link
		// IToscaEngineService}
		this.referenceMapper = engineService.getToscaReferenceMapper();
	}

	public void setControlService(final IOpenToscaControlService controlService) {
		this.controlService = controlService;
	}

	public void setCsarInstanceService(final ICSARInstanceManagementService csarInstanceService) {
		this.csarInstanceService = csarInstanceService;
	}
}
