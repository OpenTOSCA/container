package org.opentosca.planbuilder.plugins.utils;

import org.opentosca.planbuilder.plugins.context.PropertyVariable;

public class PluginUtils {

    /**
     * Checks whether the property of the given variable is empty in the TopologyTemplate
     *
     * @param variable a property variable (var must belong to a topology template property) to check
     * @param context the context the variable belongs to
     * @return true iff the content of the given variable is empty in the topology template property
     */
    public static boolean isVariableValueEmpty(final PropertyVariable variable) {
        final String content = variable.getContent();
        return content == null || content.isEmpty();
    }

}
