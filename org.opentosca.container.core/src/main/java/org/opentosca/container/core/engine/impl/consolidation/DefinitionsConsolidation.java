package org.opentosca.container.core.engine.impl.consolidation;

import org.opentosca.container.core.engine.impl.ToscaReferenceMapper;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class consolidates the processed TOSCA data. Until now only the BoundaryDefinitions and
 * Plans are consolidated to PublicPlans.
 *
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 */
@Deprecated
public class DefinitionsConsolidation {

  private final Logger LOG = LoggerFactory.getLogger(ExportedInterfacesConsolidation.class);

  private final ExportedInterfacesConsolidation exportedInterfacesConsolidation;
  private final PolicyConsolidation policyConsolidation;

  public DefinitionsConsolidation(ToscaReferenceMapper referenceMapper) {
    exportedInterfacesConsolidation = new ExportedInterfacesConsolidation(referenceMapper);
    policyConsolidation = new PolicyConsolidation(referenceMapper);
  }


  /**
   * Resolves the referenced TOSCA files inside of a CSAR and stores the mapping into the
   * ToscaReferenceMapper.
   *
   * @param csarID The ID of the passed CSAR which shall be resolved.
   * @return true means no error, false one or more errors
   */
  public boolean consolidateCSAR(final CSARID csarID) {

    boolean ret = this.exportedInterfacesConsolidation.consolidate(csarID);
    if (!ret) {
      this.LOG.error("Consolidation of the exported interfaces of CSAR \"" + csarID
        + "\" produced one or more errors.");
    }
    ret = ret && this.policyConsolidation.consolidate(csarID);
    if (!ret) {
      this.LOG.error("Consolidation of the Policies inside the CSAR \"" + csarID
        + "\" produced one or more errors.");
    }

    return ret;
  }

}
