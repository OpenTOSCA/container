package org.opentosca.toscaengine.service.impl.consolidation;

import org.opentosca.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class consolidates the processed TOSCA data. Until now only the
 * BoundaryDefinitions and Plans are consolidated to PublicPlans.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class DefinitionsConsolidation {

	private final Logger LOG = LoggerFactory
			.getLogger(ExportedInterfacesConsolidation.class);

	private ExportedInterfacesConsolidation exportedInterfacesConsolidation = new ExportedInterfacesConsolidation();
	private PolicyConsolidation policyConsolidation = new PolicyConsolidation();

	/**
	 * Resolves the referenced TOSCA files inside of a CSAR and stores the
	 * mapping into the ToscaReferenceMapper.
	 * 
	 * @param csarID
	 *            The ID of the passed CSAR which shall be resolved.
	 * @return true means no error, false one or more errors
	 */
	public boolean consolidateCSAR(CSARID csarID) {

		boolean ret = this.exportedInterfacesConsolidation.consolidate(csarID);
		if (!ret) {
			LOG.error("Consolidation of the exported interfaces of CSAR \"" + csarID + "\" produced one or more errors.");
		}
		ret = ret && policyConsolidation.consolidate(csarID);
		if (!ret) {
			LOG.error("Consolidation of the Policies inside the CSAR \"" + csarID + "\" produced one or more errors.");
		}

		return ret;
	}

}
