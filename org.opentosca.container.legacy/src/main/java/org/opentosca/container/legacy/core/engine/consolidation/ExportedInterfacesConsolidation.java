package org.opentosca.container.legacy.core.engine.consolidation;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.*;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TExportedOperation.Plan;
import org.eclipse.winery.model.tosca.TPlan;
import org.opentosca.container.legacy.core.engine.IToscaReferenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class consolidates the BoundaryDefinitions and Plans to PublicPlans.
 *
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 */
@Deprecated
public class ExportedInterfacesConsolidation {

  private static final Logger LOG = LoggerFactory.getLogger(ExportedInterfacesConsolidation.class);
  private final IToscaReferenceMapper toscaReferenceMapper;

  public ExportedInterfacesConsolidation(IToscaReferenceMapper referenceMapper) {
    this.toscaReferenceMapper = referenceMapper;
  }

  /**
   * Consolidates the exported interfaces of a CSAR.
   *
   * @param csarID the ID of the CSAR.
   * @return true for success, false if an error occured
   */
  public boolean consolidate(final CSARID csarID) {
    LOG.info("Consolidate the Interfaces of the BoundaryDefinitions of CSAR \"" + csarID + "\".");
    final boolean errorOccured = false;
    final Map<PlanTypes, LinkedHashMap<QName, TPlan>> mapTypeToPlan = this.toscaReferenceMapper.getCSARIDToPlans(csarID);

    for (final QName serviceTemplateID : this.toscaReferenceMapper.getExportedInterfacesOfCSAR(csarID).keySet()) {
      LOG.debug("Consolidate the Interfaces of the ServiceTemplate \"" + serviceTemplateID + "\".");

      for (final TExportedInterface iface : this.toscaReferenceMapper.getExportedInterfacesOfCSAR(csarID).get(serviceTemplateID)) {

        for (final TExportedOperation operation : iface.getOperation()) {
          final Plan planReference = operation.getPlan();
          if (null != planReference) {
            final TPlan toscaPlan = (TPlan) planReference.getPlanRef();

            final QName planID = new QName(this.toscaReferenceMapper.getNamespaceOfPlan(csarID, toscaPlan.getId()), toscaPlan.getId());
            this.toscaReferenceMapper.storeServiceTemplateBoundsPlan(csarID, serviceTemplateID, iface.getName(), operation.getName(), planID);
            mapTypeToPlan.get(PlanTypes.isPlanTypeURI(toscaPlan.getPlanType())).put(planID, toscaPlan);
          }
        }
      }
    }

    return !errorOccured;
  }
}
