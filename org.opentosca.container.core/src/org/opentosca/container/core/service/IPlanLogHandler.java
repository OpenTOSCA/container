package org.opentosca.container.core.service;

import java.util.Map;

public interface IPlanLogHandler {
	
	void log(String corrId, String logMsg);

	Map<String, String> getLogsOfPlanInstance(String corrId);

}