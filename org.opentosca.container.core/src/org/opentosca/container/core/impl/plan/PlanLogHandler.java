package org.opentosca.container.core.impl.plan;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.opentosca.container.core.service.IPlanLogHandler;

@Deprecated
public class PlanLogHandler implements IPlanLogHandler {

    public static IPlanLogHandler instance = new PlanLogHandler();

    private final Map<String, Map<String, String>> corrToLog = new HashMap<>();


    private PlanLogHandler() {}

    @Override
    public void log(final String corrId, final String logMsg) {
        if (!this.corrToLog.containsKey(corrId)) {
            this.corrToLog.put(corrId, new HashMap<String, String>());
        }
        this.corrToLog.get(corrId).put(Long.toString(System.currentTimeMillis()), logMsg);
    }

    @Override
    public Map<String, String> getLogsOfPlanInstance(final String corrId) {
        if (this.corrToLog.containsKey(corrId)) {
            final TreeMap<String, String> sorted = new TreeMap<>(this.corrToLog.get(corrId));
            return sorted;
        } else {
            return new HashMap<>();
        }
    }
}
