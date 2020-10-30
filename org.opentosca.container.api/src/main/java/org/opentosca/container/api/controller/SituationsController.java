package org.opentosca.container.api.controller;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.opentosca.container.api.dto.situations.SituationDTO;
import org.opentosca.container.api.dto.situations.AggregatedSituationDTO;
import org.opentosca.container.api.dto.situations.SituationListDTO;
import org.opentosca.container.api.dto.situations.AggregatedSituationListDTO;
import org.opentosca.container.api.dto.situations.SituationTriggerDTO;
import org.opentosca.container.api.dto.situations.SituationTriggerInstanceDTO;
import org.opentosca.container.api.dto.situations.SituationTriggerListDTO;
import org.opentosca.container.api.service.InstanceService;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.AggregatedSituation;
import org.opentosca.container.core.next.model.SituationTrigger;
import org.opentosca.container.core.next.model.SituationTriggerProperty;
import org.opentosca.container.core.service.CsarStorageService;
import org.springframework.stereotype.Component;

@Path("/situationsapi")
@Component
public class SituationsController {

	@Context
	UriInfo uriInfo;

	@Inject
	private InstanceService instanceService;
	@Inject
	private CsarStorageService csarService;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getRoot() {
		return Response.ok("Situations").build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/situations")
	public Response getSituations() {
		final SituationListDTO dto = new SituationListDTO();
		this.instanceService.getSituations().forEach(x -> dto.add(SituationDTO.Converter.convert(x)));
		return Response.ok(dto).build();
	}

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/situations/{situation}")
	public Response updateSituation(@PathParam("situation") final Long situationId, final SituationDTO situation) {
		final Situation sit = this.instanceService.getSituation(situationId);

		sit.setActive(situation.getActive());
		sit.setEventProbability(situation.getEventProbability());
		sit.setEventTime(situation.getEventTime());

		this.instanceService.updateSituation(sit);
		Collection<AggregatedSituation> aggrSits = Lists.newArrayList();
		aggrSits = this.instanceService.getAggregatedSituationBySitId(situationId);
		for (AggregatedSituation aggrSit : aggrSits) {
			this.instanceService.updateAggregatedSituation(aggrSit);
		}

		final URI instanceURI = this.uriInfo.getAbsolutePath();

		return Response.ok(instanceURI).build();
	}

	@PUT
	@Consumes({ MediaType.TEXT_PLAIN })
	@Path("/situations/{situation}/active")
	public Response updateSituationActivity(@PathParam("situation") final Long situationId, final String body) {
		final Situation sit = this.instanceService.getSituation(situationId);
		boolean active = false;

		if (body.equalsIgnoreCase("true") || body.equalsIgnoreCase("false")) {
			active = Boolean.valueOf(body);
		} else {
			return Response.notAcceptable(null).build();
		}

		sit.setActive(active);
		Collection<AggregatedSituation> aggrSits = Lists.newArrayList();
		aggrSits = this.instanceService.getAggregatedSituationBySitId(situationId);
		for (AggregatedSituation aggrSit : aggrSits) {
			this.instanceService.updateAggregatedSituation(aggrSit);
		}

		this.instanceService.updateSituation(sit);

		final URI instanceURI = this.uriInfo.getAbsolutePath();

		return Response.ok(instanceURI).build();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/situations")
	public Response createSituation(final SituationDTO situation) {
		final Situation sit = this.instanceService.createNewSituation(situation.getThingId(),
				situation.getSituationTemplateId(), situation.getActive(), situation.getEventProbability(),
				situation.getEventTime());

		final URI instanceURI = UriUtil.generateSubResourceURI(this.uriInfo, sit.getId().toString(), false);

		return Response.ok(instanceURI).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/situations/{situation}")
	public Response getSituation(@PathParam("situation") final Long situationId) {
		return Response.ok(SituationDTO.Converter.convert(this.instanceService.getSituation(situationId))).build();
	}

	@DELETE
	@Consumes({ MediaType.APPLICATION_XML })
	@Path("/situations/{situation}")
	public Response deleteSituation(@PathParam("situation") final Long situationId) {
		Situation situation = this.instanceService.getSituation(situationId);
		Collection<SituationTrigger> triggers = Lists.newArrayList();
		triggers = this.instanceService.getSituationTriggers(situation);
		if (!triggers.isEmpty()) {
			for (SituationTrigger trigger : triggers) {
				this.instanceService.removeSituationTrigger(trigger.getId());
			}
		}
		Collection<AggregatedSituation> aggrSits = Lists.newArrayList();
		aggrSits = this.instanceService.getAggregatedSituationBySitId(situationId);
		if (!aggrSits.isEmpty()) {
			for (AggregatedSituation aggrSit : aggrSits) {
				System.out.println(aggrSit.getId());
				this.instanceService.removeAggregatedSituation(aggrSit.getId());
				triggers = this.instanceService.getSituationTriggers(aggrSit);
				if (!triggers.isEmpty()) {
					for (SituationTrigger trigger : triggers) {
						this.instanceService.removeSituationTrigger(trigger.getId());
					}
				}
			}
		}
		boolean removed = this.instanceService.removeSituation(situationId);
		if (removed) {
			return Response.ok().build();
		} else {
			return Response.status(403).build();
		}
	}

	@DELETE
	@Consumes({ MediaType.APPLICATION_XML })
	@Path("/situations")
	public Response deleteAllSituation() {
		Collection<Situation> colSit = this.instanceService.getSituations();
		for (Situation sit : colSit) {
			System.out.println(sit.getId());
			Collection<SituationTrigger> triggers = Lists.newArrayList();
			triggers = this.instanceService.getSituationTriggers(sit);
			if (!triggers.isEmpty()) {
				for (SituationTrigger trigger : triggers) {
					this.instanceService.removeSituationTrigger(trigger.getId());
				}
			}
			Collection<AggregatedSituation> aggrSits = Lists.newArrayList();
			aggrSits = this.instanceService.getAggregatedSituationBySitId(sit.getId());
			if (!aggrSits.isEmpty()) {
				for (AggregatedSituation aggrSit : aggrSits) {
					boolean removeAggrSit = this.instanceService.removeAggregatedSituation(aggrSit.getId());
					if (!removeAggrSit) {
						return Response.status(403).build();
					}
					triggers = this.instanceService.getSituationTriggers(aggrSit);
					if (!triggers.isEmpty()) {
						for (SituationTrigger trigger : triggers) {
							boolean removeTrigger = this.instanceService.removeSituationTrigger(trigger.getId());
							if (!removeTrigger) {
								return Response.status(403).build();
							}
						}
					}
				}
			}

			boolean removed = this.instanceService.removeSituation(sit.getId());
			if (!removed) {
				return Response.status(403).build();
			}
		}
		return Response.ok().build();
	}

	// Aggregierte Situationen
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/aggregatedsituations")
	public Response getAggregatedSituations() {
		final AggregatedSituationListDTO dto = new AggregatedSituationListDTO();
		this.instanceService.getAggregatedSituations()
				.forEach(x -> dto.add(AggregatedSituationDTO.Converter.convert(x)));
		return Response.ok(dto).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/aggregatedsituations/{aggregatedsituation}")
	public Response getAggregatedSituation(@PathParam("aggregatedsituation") final Long aggregatedSituationId) {
		return Response.ok(AggregatedSituationDTO.Converter
				.convert(this.instanceService.getAggregatedSituation(aggregatedSituationId))).build();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/aggregatedsituations")
	public Response createAggregatedSituation(final AggregatedSituationDTO aggregatedSituation) {

		String evalLogicExpression = aggregatedSituation.getLogicExpression();
		String logicExpression = aggregatedSituation.getLogicExpression();
		boolean failed = false;
		evalLogicExpression = evalLogicExpression.replaceAll("AA", "&&");
		logicExpression = logicExpression.replaceAll("AA", "&&");
		evalLogicExpression = evalLogicExpression.replaceAll("BB", "||");
		logicExpression = logicExpression.replaceAll("BB", "||");
		final Collection<Situation> sits = Lists.newArrayList();
		final Collection<Long> sitsOfLogicExp = Lists.newArrayList();
		final Collection<Long> sitIDs = Lists.newArrayList();
		
		boolean matchExp = Pattern.compile("[0-9]+(?:(?:\\|\\||&&)[0-9]+)*").matcher(evalLogicExpression).matches();
		if(matchExp) {
			String [] sitOfLogicExpA = evalLogicExpression.split("(?:\\|\\||&&)");
			for(int i = 0; i< sitOfLogicExpA.length; i++) {
				sitsOfLogicExp.add(Long.parseLong(sitOfLogicExpA[i]));
				
			}
		}

		for (final Long situationId : aggregatedSituation.getSituationIds()) {
			final Situation situation = this.instanceService.getSituation(situationId);

			if (situation.isActive()) {
				evalLogicExpression = evalLogicExpression.replaceAll(situationId.toString(), "1");
			} else {
				evalLogicExpression = evalLogicExpression.replaceAll(situationId.toString(), "0");
			}

			sits.add(situation);
			sitIDs.add(situation.getId());
		}
		
		if(!(sitIDs.containsAll(sitsOfLogicExp))) {
			failed = true;
		}
		
		StringBuffer buffer = new StringBuffer(evalLogicExpression);
		int number = this.instanceService.evaluateBoolExpr(buffer);
		boolean active;
		if (number == 1) {
			active = true;
		} else {
			active = false;
		}

		float eventProbability = -1.0f;
		if (Float.compare(aggregatedSituation.getEventProbability(), eventProbability) != 0) {
			eventProbability = aggregatedSituation.getEventProbability();
		}

		String eventTime = null;
		if (aggregatedSituation.getEventTime() != null) {
			eventTime = aggregatedSituation.getEventTime();
		}
		
		if(!failed) {
			final AggregatedSituation aggregatedSit = this.instanceService.createNewAggregatedSituation(sits,
					logicExpression, active, eventProbability, eventTime);
			final URI instanceURI = UriUtil.generateSubResourceURI(this.uriInfo, aggregatedSit.getId().toString(), false);

			return Response.ok(instanceURI).build();
		}

		return Response.noContent().build();
	}

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/aggregatedsituations/{aggregatedsituation}")
	public Response updateAggregatedSituation(@PathParam("aggregatedsituation") final Long aggrsituationId,
			final AggregatedSituationDTO aggregatedSituationDTO) {

		final AggregatedSituation aggr = this.instanceService.getAggregatedSituation(aggrsituationId);
		boolean failed = false;
		aggr.setLogicExpression(aggregatedSituationDTO.getLogicExpression());
		String evalLogicExpression = aggr.getLogicExpression();

		final Collection<Situation> sits = Lists.newArrayList();
		final Collection<Long> sitsOfLogicExp = Lists.newArrayList();
		final Collection<Long> sitIDs = Lists.newArrayList();

		for (final Long situationId : aggregatedSituationDTO.getSituationIds()) {
			final Situation situation = this.instanceService.getSituation(situationId);
			sits.add(situation);
			sitIDs.add(situationId);
		}
		aggr.setSituations(sits);
		boolean matchExp = Pattern.compile("[0-9]+(?:(?:\\|\\||&&)[0-9]+)*").matcher(evalLogicExpression).matches();
		if (matchExp) {
			String[] sitsOfLogicExpA = evalLogicExpression.split("(?:\\|\\||&&)");
			for (int i = 0; i < sitsOfLogicExpA.length; i++) {
				sitsOfLogicExp.add(Long.parseLong(sitsOfLogicExpA[i]));
			}
			if (!(sitIDs.containsAll(sitsOfLogicExp))) {
				failed = true;
			}
			for (Situation situation : aggr.getSituations()) {
				if (situation.isActive()) {
					evalLogicExpression = evalLogicExpression.replaceAll(situation.getId().toString(), "1");
				} else {
					evalLogicExpression = evalLogicExpression.replaceAll(situation.getId().toString(), "0");
				}
			}

			StringBuffer buffer = new StringBuffer(evalLogicExpression);
			if (failed) {
				return Response.status(403).build();
			} else {
				int number = this.instanceService.evaluateBoolExpr(buffer);
				boolean active;
				if (number == 1) {
					active = true;
				} else {
					active = false;
				}
				aggr.setActive(active);
				aggr.getSituations().forEach(x -> System.out.println(x.getId()));
				aggr.setEventProbability(aggregatedSituationDTO.getEventProbability());
				aggr.setEventTime(aggregatedSituationDTO.getEventTime());
				this.instanceService.updateAggregatedSituation(aggr);
				
				return Response.ok().build();
			}
		}
		return Response.noContent().build();

	}

	@DELETE
	@Consumes({ MediaType.APPLICATION_XML })
	@Path("/aggregatedsituations/{aggregatedsituation}")
	public Response deleteAggregatedSituation(@PathParam("aggregatedsituation") final Long aggrsituationId) {
		boolean removed = this.instanceService.removeAggregatedSituation(aggrsituationId);
		if (removed) {
			return Response.ok().build();
		} else {
			return Response.status(403).build();
		}
	}

	@DELETE
	@Consumes({ MediaType.APPLICATION_XML })
	@Path("/aggregatedsituations")
	public Response deleteAllAggregatedSituation() {
		Collection<SituationTrigger> triggers = Lists.newArrayList();
		Collection<AggregatedSituation> aggrSits = Lists.newArrayList();
		aggrSits = this.instanceService.getAggregatedSituations();
		if (!aggrSits.isEmpty()) {
			for (AggregatedSituation aggrSit : aggrSits) {
				boolean remove = this.instanceService.removeAggregatedSituation(aggrSit.getId());
				if (!remove) {
					return Response.status(403).build();
				}
				triggers = this.instanceService.getSituationTriggers(aggrSit);
				if (!triggers.isEmpty()) {
					for (SituationTrigger trigger : triggers) {
						boolean removeTrigger = this.instanceService.removeSituationTrigger(trigger.getId());
						if (!removeTrigger) {
							return Response.status(403).build();
						}
					}
				}
			}
		}
		return Response.ok().build();
	}

	//Situationtrigger
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/triggers")
	public Response getSituationTriggers() {
		final SituationTriggerListDTO dto;
		try {
			dto = new SituationTriggerListDTO();
			this.instanceService.getSituationTriggers().forEach(x -> dto.add(SituationTriggerDTO.Converter.convert(x)));
		} catch (final Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
		return Response.ok(dto).build();
	}

	@POST
	@Path("/triggers")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response createSituationTrigger(final SituationTriggerDTO situationTrigger) {
		final Collection<Situation> sits = Lists.newArrayList();
		final Collection<AggregatedSituation> aggrSits = Lists.newArrayList();

		for (final Long situationId : situationTrigger.getSituationIds()) {
			final Situation situation = this.instanceService.getSituation(situationId);
			sits.add(situation);
		}
		
		for (final Long aggrSituationId : situationTrigger.getAggregatedSituationIds()) {
			final AggregatedSituation situation = this.instanceService.getAggregatedSituation(aggrSituationId);
			aggrSits.add(situation);
		}

		ServiceTemplateInstance serviceInstance;
		try {
			serviceInstance = this.instanceService.getServiceTemplateInstance(situationTrigger.getServiceInstanceId(),
					false);
		} catch (final UndeclaredThrowableException e) {
			serviceInstance = null;
		} catch (final Exception e) {
			serviceInstance = null;
		}
		NodeTemplateInstance nodeInstance = null;
		if (situationTrigger.getNodeInstanceId() != null) {
			nodeInstance = this.instanceService.getNodeTemplateInstance(situationTrigger.getNodeInstanceId());
		}

		final Set<SituationTriggerProperty> inputs = Sets.newHashSet();

		float eventProbability = -1.0f;
		if (Float.compare(situationTrigger.getEventProbability(), eventProbability) != 0) {
			eventProbability = situationTrigger.getEventProbability();
		}

		String eventTime = null;
		if (situationTrigger.getEventTime() != null) {
			eventTime = situationTrigger.getEventTime();
		}

		situationTrigger.getInputParams()
				.forEach(x -> inputs.add(new SituationTriggerProperty(x.getName(), x.getValue(), x.getType())));

		final SituationTrigger sitTrig = this.instanceService.createNewSituationTrigger(sits,aggrSits,
				csarService.findById(new CsarId(situationTrigger.getCsarId())).id(), situationTrigger.isOnActivation(),
				situationTrigger.isSingleInstance(), serviceInstance, nodeInstance, situationTrigger.getInterfaceName(),
				situationTrigger.getOperationName(), inputs, eventProbability, eventTime);

		final URI instanceURI = UriUtil.generateSubResourceURI(this.uriInfo, sitTrig.getId().toString(), false);
		return Response.ok(instanceURI).build();
	}

	@DELETE
	@Consumes({ MediaType.APPLICATION_XML })
	@Path("/triggers/{trigger}")
	public Response deleteSituationTrigger(@PathParam("trigger") final Long triggerId) {
		boolean removed = this.instanceService.removeSituationTrigger(triggerId);
		if (removed) {
			return Response.ok().build();
		} else {
			return Response.status(403).build();
		}
	}

	@DELETE
	@Consumes({ MediaType.APPLICATION_XML })
	@Path("/triggers")
	public Response deleteAllSituationTrigger() {
		Collection<SituationTrigger> colTrig = this.instanceService.getSituationTriggers();
		if (colTrig != null) {
			for (SituationTrigger trig : colTrig) {
				boolean remove = this.instanceService.removeSituationTrigger(trig.getId());
				if (!remove) {
					return Response.status(403).build();
				}
			}
		}
		return Response.ok().build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/triggers/{situationtrigger}")
	public Response getSituationTrigger(@PathParam("situationtrigger") final Long situationTriggerId) {
		return Response
				.ok(SituationTriggerDTO.Converter.convert(this.instanceService.getSituationTrigger(situationTriggerId)))
				.build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/triggers/{situationtrigger}/{situationtriggerinstance}")
	public Response getSituationTriggerInstance(@PathParam("situationtrigger") final Long situationTriggerId,
			@PathParam("situationtriggerinstance") final Long situationTriggerInstanceId) {
		return Response.ok(SituationTriggerInstanceDTO.Converter
				.convert(this.instanceService.getSituationTriggerInstance(situationTriggerInstanceId))).build();
	}
}
