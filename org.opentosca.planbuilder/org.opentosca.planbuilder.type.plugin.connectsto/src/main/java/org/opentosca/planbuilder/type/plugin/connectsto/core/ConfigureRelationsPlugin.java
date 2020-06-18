package org.opentosca.planbuilder.type.plugin.connectsto.core;

import java.util.List;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;

public abstract class ConfigureRelationsPlugin<T extends PlanContext> implements IPlanBuilderTypePlugin<T> {

    public static final String INTERFACE_NAME =
        "http://docs.oasis-open.org/tosca/ns/2011/12/interfaces/relationship/configure";
    public static final String OPERATION_POST_CONFIGURE_SOURCE = "postConfigureSource";
    public static final String OPERATION_POST_CONFIGURE_TARGET = "postConfigureTarget";

    @Override
    public boolean canHandleCreate(final AbstractNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean canHandleCreate(final AbstractRelationshipTemplate relationshipTemplate) {
        final List<AbstractInterface> interfaces = relationshipTemplate.getRelationshipType().getInterfaces();
        for (final AbstractInterface i : interfaces) {
            if (i.getName().equalsIgnoreCase(INTERFACE_NAME)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getID() {
        return getClass().getCanonicalName();
    }

    @Override
    public boolean canHandleTerminate(AbstractNodeTemplate nodeTemplate) {
        // TODO we have to define the semantics of a disconnect first
        return false;
    }

    @Override
    public boolean canHandleTerminate(AbstractRelationshipTemplate relationshipTemplate) {
        // TODO we have to define the semantics of a disconnect first
        return false;
    }
}
