package org.opentosca.container.api.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.glassfish.jersey.uri.UriComponent;
import org.opentosca.container.api.dto.plans.PlanDTO;
import org.opentosca.container.api.dto.plans.PlanInstanceDTO;
import org.opentosca.container.api.dto.plans.PlanInstanceListDTO;
import org.opentosca.container.api.dto.plans.PlanListDTO;
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.BuildCorrelationToInstanceMapping;
import org.opentosca.container.api.util.JsonUtil;
import org.opentosca.container.api.util.UriUtils;
import org.opentosca.container.control.IOpenToscaControlService;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.impl.plan.CorrelationHandler;
import org.opentosca.container.core.impl.plan.PlanLogHandler;
import org.opentosca.container.core.impl.plan.ServiceProxy;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.State;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.service.ICSARInstanceManagementService;
import org.opentosca.container.core.service.IPlanLogHandler;
import org.opentosca.container.core.tosca.extension.PlanInvocationEvent;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.extension.TParameter;
import org.opentosca.container.core.tosca.model.TBoolean;
import org.opentosca.container.core.tosca.model.TPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class PlanService {

	private static Logger logger = LoggerFactory.getLogger(PlanService.class);

	private static final PlanTypes[] ALL_PLAN_TYPES = PlanTypes.values();

	@SuppressWarnings("unused")
	private IToscaEngineService engineService;

	private IToscaReferenceMapper referenceMapper;

	private IOpenToscaControlService controlService;

	private ICSARInstanceManagementService csarInstanceService;

	private final CorrelationHandler correlationHandler = ServiceProxy.correlationHandler;

	private final IPlanLogHandler logHandler = PlanLogHandler.instance;

	private final BuildCorrelationToInstanceMapping instanceMapper = BuildCorrelationToInstanceMapping.instance;

	public List<TPlan> getPlansByType(final CSARID id, final PlanTypes... planTypes) {
		logger.debug("Requesting plans of type \"{}\" for CSAR \"{}\"...", planTypes, id);
		final List<TPlan> plans = Lists.newArrayList();
		final Map<PlanTypes, LinkedHashMap<QName, TPlan>> plansOfCsar = this.referenceMapper.getCSARIDToPlans(id);
		for (final PlanTypes planType : planTypes) {
			final LinkedHashMap<QName, TPlan> plansOfType = plansOfCsar.get(planType);
			if (plansOfType == null) {
				logger.warn("CSAR \"" + id.getFileName() + "\" does not have a plan of type \"" + planType.toString()
						+ "\"");
				continue;
			}
			plans.addAll(plansOfType.values());
		}
		return plans;
	}

	public TPlan getPlan(final String name, final CSARID id) {
		final List<TPlan> plans = this.getPlansByType(id, ALL_PLAN_TYPES);
		for (final TPlan plan : plans) {
			if ((plan.getId() != null) && plan.getId().equalsIgnoreCase(name)) {
				return plan;
			}
		}
		return null;
	}

	public String invokePlan(final CSARID csarId, final QName serviceTemplate, final TPlan plan,
			final List<TParameter> parameters) {

		final PlanDTO dto = new PlanDTO(plan);

		final String namespace = this.referenceMapper.getNamespaceOfPlan(csarId, plan.getId());
		dto.setId(new QName(namespace, plan.getId()).toString());
		dto.setInputParameters(parameters);

		try {
			return this.controlService.invokePlanInvocation(csarId, serviceTemplate, -1,
					PlanDTO.Converter.convert(dto));
		} catch (final UnsupportedEncodingException e) {
			throw new ServerErrorException(500, e);
		}
	}

	public boolean hasPlan(final CSARID csarId, final PlanTypes[] planTypes, final String plan) {
		final TPlan p = this.getPlan(plan, csarId);
		if (p == null) {
			return false;
		}
		if (Arrays.asList(planTypes).contains(PlanTypes.isPlanTypeURI(p.getPlanType()))) {
			return true;
		}
		return false;
	}

	public boolean hasPlanInstance(final String correlationId) {
		return this.instanceMapper.knowsCorrelationId(correlationId);
	}

	public Long getServiceTemplateInstanceId(final String correlationId) {
		if (this.hasPlanInstance(correlationId)) {
			return Long.valueOf((this.instanceMapper.getServiceTemplateInstanceIdForBuildPlanCorrelation(correlationId)));
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

	public PlanInvocationEvent getPlanInvocationEvent(final String correlationId) {
		return this.csarInstanceService.getPlanForCorrelationId(correlationId);
	}

	/* Service Injection */
	/********************/
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

	/* API Operations Helper Methods */
	/*******************************/
	public Response getPlans(final UriInfo uriInfo, final CSARID csarId, final QName serviceTemplate,
			final PlanTypes... planTypes) {

		final List<TPlan> buildPlans = getPlansByType(csarId, planTypes);
		logger.debug("Found <{}> plans for ServiceTemplate \"{}\" in CSAR \"{}\"", buildPlans.size(), serviceTemplate,
				csarId);

		final PlanListDTO list = new PlanListDTO();
		buildPlans.stream().forEach(p -> {
			final PlanDTO plan = new PlanDTO(p);
			plan.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path(plan.getId()).build()))
					.rel("self").build());
			list.add(plan);
		});
		list.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());

		return Response.ok(list).build();
	}

	public Response getPlan(final String plan, final UriInfo uriInfo, final CSARID csarId, final QName serviceTemplate,
			final PlanTypes... planTypes) {

		final List<TPlan> buildPlans = getPlansByType(csarId, planTypes);
		logger.debug("Found <{}> plans for ServiceTemplate \"{}\" in CSAR \"{}\"", buildPlans.size(), serviceTemplate,
				csarId);

		final TPlan p = getPlan(plan, csarId);

		if (p == null) {
			logger.info("Plan \"" + plan + "\" of ServiceTemplate \"" + serviceTemplate + "\" in CSAR \"" + csarId
					+ "\" not found");
			throw new NotFoundException("Plan \"" + plan + "\" of ServiceTemplate \"" + serviceTemplate
					+ "\" in CSAR \"" + csarId + "\" not found");
		}

		final PlanDTO dto = new PlanDTO(p);
		dto.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path("instances").build()))
				.rel("instances").build());
		dto.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());
		return Response.ok(dto).build();
	}

	public Response getPlanInstances(final String plan, final UriInfo uriInfo, final CSARID csarId,
			final QName serviceTemplate, final Long serviceTemplateInstanceId, final PlanTypes... planTypes) {

		if (!hasPlan(csarId, planTypes, plan)) {
			logger.info("Plan \"" + plan + "\" could not be found");
			throw new NotFoundException("Plan \"" + plan + "\" could not be found");
		}

		ServiceTemplateInstanceRepository repo = new ServiceTemplateInstanceRepository();

		final Collection<ServiceTemplateInstance> serviceInstances;
		if (serviceTemplateInstanceId != null) {
			serviceInstances = Lists.newArrayList();
			serviceInstances.add(repo.find(serviceTemplateInstanceId).get());
		} else {
			serviceInstances = repo.findByCsarId(csarId);
		}

		final List<PlanInstanceDTO> planInstances = Lists.newArrayList();
		for (ServiceTemplateInstance sti : serviceInstances) {
			List<PlanInstanceDTO> foo = sti.getPlanInstances().stream().filter(
					p -> !Arrays.asList(planTypes).contains(PlanTypes.isPlanTypeEnumRepresentation(p.getType().toString())))
					.map(p -> PlanInstanceDTO.Converter.convert(p)).collect(Collectors.toList());
			planInstances.addAll(foo);
		}

		for (final PlanInstanceDTO pi : planInstances) {

			// Add service template instance link
			final Long id = pi.getServiceTemplateInstanceId();
			if (id != null) {
				final URI uri = uriInfo.getBaseUriBuilder()
						.path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}")
						.build(csarId.toString(), serviceTemplate.toString(), String.valueOf(id));
				pi.add(Link.fromUri(UriUtils.encode(uri)).rel("service_template_instance").build());
			}

			// Add self link
			pi.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePathBuilder().path(pi.getId()).build())).rel("self")
					.build());
		}

		final PlanInstanceListDTO list = new PlanInstanceListDTO();

		list.add(planInstances);
		list.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());

		return Response.ok(list).build();
	}

	public Response invokePlan(final String plan, final UriInfo uriInfo, final List<TParameter> parameters,
			final CSARID csarId, final QName serviceTemplate, final Long serviceTemplateInstanceId,
			final PlanTypes... planTypes) {

		if (parameters == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		if (!hasPlan(csarId, planTypes, plan)) {
			logger.info("Plan \"" + plan + "\" could not be found");
			throw new NotFoundException("Plan \"" + plan + "\" could not be found");
		}

		logger.info("Received a payload for plan \"{}\" in ServiceTemplate \"{}\" of CSAR \"{}\"", plan,
				serviceTemplate, csarId);
		if (logger.isDebugEnabled()) {
			logger.debug("Request payload:\n{}", JsonUtil.writeValueAsString(parameters));
		}

		/*
		 * Add parameter "OpenTOSCAContainerAPIServiceInstanceID" as a callback for the
		 * plan engine
		 */
		if (serviceTemplateInstanceId != null) {
			String url = Settings.CONTAINER_INSTANCEDATA_API + serviceTemplateInstanceId;
			url = url.replace("{csarid}", csarId.getFileName());
			url = url.replace("{servicetemplateid}",
					UriComponent.encode(serviceTemplate.toString(), UriComponent.Type.PATH_SEGMENT));
			final URI uri = UriUtils.encode(URI.create(url));
			final TParameter param = new TParameter();
			param.setName("OpenTOSCAContainerAPIServiceInstanceID");
			param.setRequired(TBoolean.fromValue("yes"));
			param.setType("String");
			param.setValue(uri.toString());
			parameters.add(param);
		}

		final TPlan p = getPlan(plan, csarId);
		final String correlationId = invokePlan(csarId, serviceTemplate, p, parameters);
		final URI location = UriUtils.encode(uriInfo.getAbsolutePathBuilder().path(correlationId).build());

		return Response.created(location).build();
	}

	public Response getPlanInstance(final String plan, final String instance, final UriInfo uriInfo,
			final CSARID csarId, final QName serviceTemplate, final Long serviceTemplateInstanceId,
			final PlanTypes... planTypes) {

		if (!hasPlan(csarId, planTypes, plan)) {
			logger.info("Plan \"" + plan + "\" could not be found");
			throw new NotFoundException("Plan \"" + plan + "\" could not be found");
		}

		PlanInstanceRepository repository = new PlanInstanceRepository();
		PlanInstance pi = repository.findByCorrelationId(instance);
		if (pi == null) {
			return Response.status(Status.NOT_FOUND).entity("Plan instance '" + instance + "' not found").build();
		}

		final PlanInstanceDTO dto = PlanInstanceDTO.Converter.convert(pi);

		// Add service template instance link
		final Long id = pi.getServiceTemplateInstance().getId();
		if (id != null) {
			final URI uri = uriInfo.getBaseUriBuilder()
					.path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}")
					.build(csarId.toString(), serviceTemplate.toString(), String.valueOf(id));
			dto.add(Link.fromUri(UriUtils.encode(uri)).rel("service_template_instance").build());
		}

		// Add self link
		dto.add(Link.fromUri(UriUtils.encode(uriInfo.getAbsolutePath())).rel("self").build());

		return Response.ok(dto).build();
	}
}
