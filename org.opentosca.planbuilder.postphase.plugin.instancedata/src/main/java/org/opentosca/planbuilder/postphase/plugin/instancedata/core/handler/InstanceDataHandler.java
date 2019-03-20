package org.opentosca.planbuilder.postphase.plugin.instancedata.core.handler;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.context.PlanContext;

public interface InstanceDataHandler<T extends PlanContext> {
  public boolean handle(T context, AbstractRelationshipTemplate relationshipTemplate);

  public boolean handleBuild(T context, AbstractNodeTemplate nodeTemplate);

  public boolean handleTerminate(T context, AbstractNodeTemplate nodeTemplate);

}
