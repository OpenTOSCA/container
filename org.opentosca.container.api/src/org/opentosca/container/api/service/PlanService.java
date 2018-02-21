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
import org.opentosca.container.api.dto.plan.PlanDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceEventListDTO;
import org.opentosca.container.api.dto.plan.PlanInstanceListDTO;
import org.opentosca.container.api.dto.plan.PlanListDTO;
import org.opentosca.container.api.dto.request.CreatePlanInstanceLogEntryRequest;
import org.opentosca.container.api.util.JsonUtil;
import org.opentosca.container.api.util.UriUtil;
import org.opentosca.container.control.IOpenToscaControlService;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
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

	// this service is used to retrieve a reference to IToscaReferenceMapper
	private IToscaEngineService engineService;

	private IToscaReferenceMapper referenceMapper;

	private IOpenToscaControlService controlService;

	private PlanInstanceRepository planInstanceRepository = new PlanInstanceRepository();

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

	public String invokePlan(final CSARID csarId, final QName serviceTemplate, final long serviceTemplateInstanceId, final TPlan plan,
			final List<TParameter> parameters) {

		final PlanDTO dto = new PlanDTO(plan);

		final String namespace = this.referenceMapper.getNamespaceOfPlan(csarId, plan.getId());
		dto.setId(new QName(namespace, plan.getId()).toString());
		dto.setInputParameters(parameters);

		try {
			return this.controlService.invokePlanInvocation(csarId, serviceTemplate, serviceTemplateInstanceId,
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

	/**
	 * Gets the indicated plan instance and performs sanity checks insuring that the
	 * plan belongs to the service template, the instance belongs to the plan, and
	 * belongs to the service template instance (if one is passed).
	 * 
	 * @param plan
	 * @param instance
	 * @param uriInfo
	 * @param csarId
	 * @param serviceTemplate
	 * @param serviceTemplateInstanceId
	 * @param planTypes
	 * @return
	 */
	private PlanInstance resolvePlanInstance(final String plan, final String instance, final UriInfo uriInfo,
			final CSARID csarId, final QName serviceTemplate, final Long serviceTemplateInstanceId,
			final PlanTypes... planTypes) {

		if (!hasPlan(csarId, planTypes, plan)) {
			final String msg = "Plan \"" + plan + "\" could not be found";
			logger.info(msg);
			throw new NotFoundException(msg);
		}

		final PlanInstanceRepository repository = new PlanInstanceRepository();
		final PlanInstance pi = repository.findByCorrelationId(instance);

		if (pi == null) {
			final String msg = "Plan instance '" + instance + "' not found";
			logger.info(msg);
			throw new NotFoundException(msg);
		}

		final Long id = pi.getServiceTemplateInstance().getId();

		if (!pi.getTemplateId().getLocalPart().equals(plan)) {
			final String msg = String.format(
					"The passed plan instance <%s> does not belong to the passed plan template: %s", instance, plan);
			logger.info(msg);
			throw new NotFoundException(msg);
		}

		if (serviceTemplateInstanceId != null && serviceTemplateInstanceId != id) {
			final String msg = String.format(
					"The passed service template instance id <%s> does not match the service template instance id that is associated with the plan instance <%s> ",
					serviceTemplateInstanceId, id, instance);
			logger.info(msg);
			throw new NotFoundException(msg);
		}

		return pi;
	}

	/* Service Injection */
	/********************/
	public void setEngineService(final IToscaEngineService engineService) {
		this.engineService = engineService;
		// We cannot inject an instance of {@link IToscaReferenceMapper} since
		// it is manually created in our default implementation of {@link
		// IToscaEngineService}
		this.referenceMapper = this.engineService.getToscaReferenceMapper();
	}

	public void setControlService(final IOpenToscaControlService controlService) {
		this.controlService = controlService;
	}

	/* API Operations Helper Methods */
	/*********************************/
	/*********************************/

	/* Plan Templates */
	/******************/
	public Response getPlans(final UriInfo uriInfo, final CSARID csarId, final QName serviceTemplate,
			final PlanTypes... planTypes) {

		final List<TPlan> buildPlans = getPlansByType(csarId, planTypes);
		logger.debug("Found <{}> plans for ServiceTemplate \"{}\" in CSAR \"{}\"", buildPlans.size(), serviceTemplate,
				csarId);

		final PlanListDTO list = new PlanListDTO();
		buildPlans.stream().forEach(p -> {
			final PlanDTO plan = new PlanDTO(p);
			plan.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePathBuilder().path(plan.getId()).build()))
					.rel("self").build());
			list.add(plan);
		});
		list.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePath())).rel("self").build());

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
		dto.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePathBuilder().path("instances").build()))
				.rel("instances").build());
		dto.add(Link.fromUri(UriUtil.encode(uriInfo.getAbsolutePath())).rel("self").build());
		return Response.ok(dto).build();
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
			final URI uri = UriUtil.encode(URI.create(url));
			final TParameter param = new TParameter();
			param.setName("OpenTOSCAContainerAPIServiceInstanceID");
			param.setRequired(TBoolean.fromValue("yes"));
			param.setType("String");
			param.setValue(uri.toString());
			parameters.add(param);
		}

		final TPlan p = getPlan(plan, csarId);
		final String correlationId = invokePlan(csarId, serviceTemplate, serviceTemplateInstanceId, p, parameters);
		final URI location = UriUtil.encode(uriInfo.getAbsolutePathBuilder().path(correlationId).build());

		return Response.created(location).build();
	}

	/* Plan Instances */
	/*****************/
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
			List<PlanInstanceDTO> foo = sti.getPlanInstances().stream()
					.filter(p -> {
						PlanTypes currType = PlanTypes.isPlanTypeURI(p.getType().toString());
						return Arrays.asList(planTypes).contains(currType);	
					})
					.map(p -> PlanInstanceDTO.Converter.convert(p)).collect(Collectors.toList());
			planInstances.addAll(foo);
		}

		for (final PlanInstanceDTO pi : planInstances) {
			
			// Should we add the link in the "instances" method or only in "instance" method?
			// Add service template instance link
			final Long id = pi.getServiceTemplateInstanceId();
			if (id != null) {
				final URI uri = uriInfo.getBaseUriBuilder()
						.path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}")
						.build(csarId.toString(), serviceTemplate.toString(), String.valueOf(id));
				pi.add(Link.fromUri(UriUtil.encode(uri)).rel("service_template_instance").build());
			}

			// Add self link
			pi.add(UriUtil.generateSubResourceLink(uriInfo, pi.getCorrelationId(), true, "self"));
		}

		final PlanInstanceListDTO list = new PlanInstanceListDTO();

		list.add(planInstances);
		list.add(UriUtil.generateSelfLink(uriInfo));

		return Response.ok(list).build();
	}

	public Response getPlanInstance(final String plan, final String instance, final UriInfo uriInfo,
			final CSARID csarId, final QName serviceTemplate, final Long serviceTemplateInstanceId,
			final PlanTypes... planTypes) {

		final PlanInstance pi = this.resolvePlanInstance(plan, instance, uriInfo, csarId, serviceTemplate,
				serviceTemplateInstanceId, planTypes);
		final PlanInstanceDTO dto = PlanInstanceDTO.Converter.convert(pi);
		// Add service template instance link
		if (pi.getServiceTemplateInstance() != null) {
			final URI uri = uriInfo.getBaseUriBuilder()
					.path("/csars/{csar}/servicetemplates/{servicetemplate}/instances/{instance}")
					.build(csarId.toString(), serviceTemplate.toString(), String.valueOf(pi.getServiceTemplateInstance().getId()));
			dto.add(Link.fromUri(UriUtil.encode(uri)).rel("service_template_instance").build());
		}
		
		dto.add(UriUtil.generateSubResourceLink(uriInfo, "state", false, "state"));
		dto.add(UriUtil.generateSubResourceLink(uriInfo, "logs", false, "logs"));

		// Add self link
		dto.add(UriUtil.generateSelfLink(uriInfo));

		return Response.ok(dto).build();

	}

	public Response getPlanInstanceState(final String plan, final String instance, final UriInfo uriInfo,
			final CSARID csarId, final QName serviceTemplate, final Long serviceTemplateInstanceId,
			final PlanTypes... planTypes) {
		final PlanInstance pi = this.resolvePlanInstance(plan, instance, uriInfo, csarId, serviceTemplate,
				serviceTemplateInstanceId, planTypes);

		return Response.ok(pi.getState().toString()).build();
	}

	public Response changePlanInstanceState(final String newState, final String plan, final String instance,
			final UriInfo uriInfo, final CSARID csarId, final QName serviceTemplate,
			final Long serviceTemplateInstanceId, final PlanTypes... planTypes) {
		final PlanInstance pi = this.resolvePlanInstance(plan, instance, uriInfo, csarId, serviceTemplate,
				serviceTemplateInstanceId, planTypes);

		try {
			final PlanInstanceState parsedState = PlanInstanceState.valueOf(newState);
			pi.setState(parsedState);
			this.planInstanceRepository.update(pi);

			return Response.ok().build();
		} catch (IllegalArgumentException e) {
			String msg = String.format("The given state %s is an illegal plan instance state.", newState);
			logger.info(msg);

			return Response.status(Status.BAD_REQUEST).build();
		}

	}

	public Response getPlanInstanceLogs(final String plan, final String instance, final UriInfo uriInfo, final CSARID csarId,
			final QName serviceTemplate, final Long serviceTemplateInstanceId, final PlanTypes... planTypes) {
		final PlanInstance pi = this.resolvePlanInstance(plan, instance, uriInfo, csarId, serviceTemplate,
				serviceTemplateInstanceId, planTypes);
		final PlanInstanceDTO piDto = PlanInstanceDTO.Converter.convert(pi);
		final PlanInstanceEventListDTO dto = new PlanInstanceEventListDTO(piDto.getLogs());
		dto.add(UriUtil.generateSelfLink(uriInfo));

		return Response.ok(dto).build();
	}

	public Response addLogToPlanInstance(final CreatePlanInstanceLogEntryRequest logEntry, final String plan, final String instance, final UriInfo uriInfo,
			final CSARID csarId, final QName serviceTemplate, final Long serviceTemplateInstanceId,
			final PlanTypes... planTypes) {
		String entry = logEntry.getLogEntry();


		if (entry != null && entry.length() > 0) {
			final PlanInstance pi = this.resolvePlanInstance(plan, instance, uriInfo, csarId, serviceTemplate,
					serviceTemplateInstanceId, planTypes);
			final PlanInstanceEvent event = new PlanInstanceEvent("INFO", "PLAN_LOG", entry);
			pi.addEvent(event);
			this.planInstanceRepository.update(pi);
			final URI resourceUri = UriUtil.generateSelfURI(uriInfo);

			return Response.ok(resourceUri).build();

		} else {
			logger.info("Log entry is empty!");
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

}
