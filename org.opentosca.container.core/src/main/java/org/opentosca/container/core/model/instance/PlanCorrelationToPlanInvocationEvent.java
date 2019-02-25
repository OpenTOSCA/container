package org.opentosca.container.core.model.instance;

import java.util.HashMap;
import java.util.Map;

import org.opentosca.container.core.tosca.extension.PlanInvocationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class maps a CorrelationID to a PlanInvocationEvent for the CSARInstance History.
 */
@Deprecated
public class PlanCorrelationToPlanInvocationEvent {

    private final Logger LOG = LoggerFactory.getLogger(PlanCorrelationToPlanInvocationEvent.class);

    // map of CorrelationID to PlanInvocationEvent
    // TODO make persistent
    private final Map<String, PlanInvocationEvent> storageMap = new HashMap<>();


    public PlanInvocationEvent getPlan(final String planCorrelation) {
        return this.storageMap.get(planCorrelation);
    }

    public void storePlan(final String planCorrelation, final PlanInvocationEvent planInvocationEvent) {
        // if (!storageMap.containsKey(planCorrelation)) {
        this.storageMap.put(planCorrelation, planInvocationEvent);
        // return;
        // }
        // LOG.warn("The correlation was stored before. Therefore the passed
        // PlanInvocationEvent was not stored again.");
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        final String ls = System.getProperty("line.separator");

        builder.append("Currently stored informations for instances and correlations:" + ls);
        for (final String correlation : this.storageMap.keySet()) {
            builder.append("Correlation \"" + correlation + "\":" + ls + "   ");
            builder.append(this.storageMap.get(correlation).toString());
            builder.append(ls + ls);
        }

        return builder.toString();
    }

}
