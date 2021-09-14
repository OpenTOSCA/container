package org.opentosca.planbuilder.type.plugin.connectsto.core;

import java.util.List;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.model.utils.ModelUtils;

public abstract class ConfigureRelationsPlugin<T extends PlanContext> implements IPlanBuilderTypePlugin<T> {

    public static final String INTERFACE_NAME =
        "http://docs.oasis-open.org/tosca/ns/2011/12/interfaces/relationship/configure";
    public static final String OPERATION_POST_CONFIGURE_SOURCE = "postConfigureSource";
    public static final String OPERATION_POST_CONFIGURE_TARGET = "postConfigureTarget";

    @Override
    public boolean canHandleCreate(Csar csar, final TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean canHandleCreate(Csar csar, final TRelationshipTemplate relationshipTemplate) {
        final List<TInterface> interfaces = ModelUtils.findRelationshipType(relationshipTemplate, csar).getInterfaces();
        if (interfaces == null) {
            return false;
        }
        for (final TInterface i : interfaces) {
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
    public boolean canHandleTerminate(Csar csar, TNodeTemplate nodeTemplate) {
        // TODO we have to define the semantics of a disconnect first
        return false;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, TRelationshipTemplate relationshipTemplate) {
        // TODO we have to define the semantics of a disconnect first
        return false;
    }
}
