package org.opentosca.container.api.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.model.TPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class PlanService {

	private static Logger logger = LoggerFactory.getLogger(PlanService.class);
	
	@SuppressWarnings("unused")
	private IToscaEngineService engineService;

	private IToscaReferenceMapper referenceMapper;
	
	
	public List<TPlan> getPlansByType(final PlanTypes planType, final CSARID id) {
		logger.debug("Requesting plans of type \"{}\" for CSAR \"{}\"...", planType, id);
		final Map<PlanTypes, LinkedHashMap<QName, TPlan>> plans = this.referenceMapper.getCSARIDToPlans(id);
		final LinkedHashMap<QName, TPlan> buildPlans = plans.get(planType);
		if (buildPlans == null) {
			logger.info("CSAR \"" + id.getFileName() + "\" does not have any build plan");
			throw new NotFoundException("CSAR \"" + id.getFileName() + "\" does not have any build plan");
		}
		return Lists.newArrayList(buildPlans.values());
	}
	
	public void setEngineService(final IToscaEngineService engineService) {
		this.engineService = engineService;
		// We cannot inject an instance of {@link IToscaReferenceMapper} since
		// it is manually created in our default implementation of {@link
		// IToscaEngineService}
		this.referenceMapper = engineService.getToscaReferenceMapper();
	}
}
