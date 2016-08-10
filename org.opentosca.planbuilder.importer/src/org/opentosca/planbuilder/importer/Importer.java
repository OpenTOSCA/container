package org.opentosca.planbuilder.importer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.planbuilder.csarhandler.CSARHandler;
import org.opentosca.planbuilder.importer.context.impl.DefinitionsImpl;
import org.opentosca.planbuilder.integration.layer.AbstractImporter;
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is a PlanBuilder Importer for openTOSCA. Importing of CSARs is
 * handled by passing a CSARID
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Importer extends AbstractImporter {

	final private static Logger LOG = LoggerFactory.getLogger(Importer.class);
	private CSARHandler handler = new CSARHandler();


	/**
	 * Generates a List of BuildPlans for the given CSARID. The BuildPlans are
	 * generated for the ServiceTemplates inside the Entry-Definitions Document,
	 * that haven't got a BuildPlan yet.
	 *
	 * @param csarId the CSARID for the CSAR the BuildPlans should be generated
	 * @return a List of BuildPlan
	 */
	public List<BuildPlan> importDefs(CSARID csarId) {
		try {
			CSARContent content = this.handler.getCSARContentForID(csarId);
			AbstractDefinitions defs = this.createContext(content);
			List<BuildPlan> plans = this.buildPlans(defs, csarId.getFileName());
			return plans;
		} catch (UserException e) {
			Importer.LOG.error("Some error within input", e);
		} catch (SystemException e) {
			Importer.LOG.error("Some internal error", e);
		}
		return new ArrayList<BuildPlan>();
	}

	/**
	 * Creates an AbstractDefinitions Object of the given CSARContent
	 *
	 * @param csarContent the CSARContent to generate an AbstractDefinitions for
	 * @return an AbstractDefinitions which is the Entry-Definitions of the
	 *         given CSAR
	 * @throws SystemException is thrown if accessing data inside the OpenTOSCA
	 *             Core fails
	 */
	public AbstractDefinitions createContext(CSARContent csarContent) throws SystemException {
		AbstractFile rootTosca = csarContent.getRootTOSCA();
		Set<AbstractFile> referencedFilesInCsar = csarContent.getFilesRecursively();
		return new DefinitionsImpl(rootTosca, referencedFilesInCsar, true);
	}

}
