package org.opentosca.planbuilder.integration.layer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.namespace.QName;

import org.opentosca.planbuilder.AbstractPlanBuilder;
import org.opentosca.planbuilder.core.bpel.BPELBuildProcessBuilder;
import org.opentosca.planbuilder.core.bpel.BPELScaleOutProcessBuilder;
import org.opentosca.planbuilder.core.bpel.BPELTerminationProcessBuilder;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

/**
 * <p>
 * This abstract class is used to define importers
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class AbstractImporter {

//  @Inject
//  @Named("bpelBuildProcessBuilder")
  private AbstractPlanBuilder bpelBuildProcessBuilder = new BPELBuildProcessBuilder();
//  @Inject
//  @Named("bpelTerminationProcessBuilder")
  private AbstractPlanBuilder bpelTerminationBuilder = new BPELTerminationProcessBuilder();
//  @Inject
//  @Named("bpelScaleOutProcessBuilder")
  private AbstractPlanBuilder bpelScaleOutProcessBuilder = new BPELScaleOutProcessBuilder();

  /**
   * Creates a BuildPlan for the given ServiceTemplate
   *
   * @param defs            an AbstractDefinitions
   * @param csarName        the File name of the CSAR the Definitions document is
   *                        defined in
   * @param serviceTemplate a QName representing a ServiceTemplate inside the
   *                        given Definitions Document
   * @return a BuildPlan if generating a BuildPlan was successful, else null
   */
  public AbstractPlan buildPlan(final AbstractDefinitions defs, final String csarName, final QName serviceTemplate) {
    return bpelBuildProcessBuilder.buildPlan(csarName, defs, serviceTemplate);
  }

  /**
   * Generates Plans for ServiceTemplates inside the given Definitions document
   *
   * @param defs     an AbstractDefinitions
   * @param csarName the FileName of the CSAR the given Definitions is contained
   *                 in
   * @return a List of Plans
   */
  public List<AbstractPlan> buildPlans(final AbstractDefinitions defs, final String csarName) {
    final List<AbstractPlan> plans = new ArrayList<>();
    // FIXME: This does not work for me (Michael W. - 2018-02-19)
    // Because policies must be enforced when they are set on the the topology, if
    // the planbuilder doesn't understand them it doesn't generate a plan -> doesn't work for you
    //
    // if (!this.hasPolicies(defs)) {
    // buildPlanBuilder = new BPELBuildProcessBuilder();
    // } else {
    // buildPlanBuilder = new PolicyAwareBPELBuildProcessBuilder();
    // }

    plans.addAll(bpelScaleOutProcessBuilder.buildPlans(csarName, defs));
    plans.addAll(bpelBuildProcessBuilder.buildPlans(csarName, defs));
    plans.addAll(bpelTerminationBuilder.buildPlans(csarName, defs));
    return plans;
  }

  private boolean hasPolicies(final AbstractDefinitions defs) {
    for (final AbstractServiceTemplate serv : defs.getServiceTemplates()) {
      for (final AbstractNodeTemplate nodeTemplate : serv.getTopologyTemplate().getNodeTemplates()) {
        if (!nodeTemplate.getPolicies().isEmpty()) {
          return true;
        }
      }
    }
    return false;
  }

}
