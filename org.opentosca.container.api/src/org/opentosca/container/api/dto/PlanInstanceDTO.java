package org.opentosca.container.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.tosca.extension.TParameter;

@XmlRootElement(name = "PlanInstance")
public class PlanInstanceDTO extends ResourceSupport {
	
	private String id;
	
	private State state;

	private List<TParameter> output;
	
	private List<LogEntry> logs;
	
	
	public PlanInstanceDTO() {

	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public State getState() {
		return this.state;
	}
	
	public void setState(final State state) {
		this.state = state;
	}
	
	public List<TParameter> getOutput() {
		return this.output;
	}
	
	public void setOutput(final List<TParameter> output) {
		this.output = output;
	}
	
	public List<LogEntry> getLogs() {
		return this.logs;
	}
	
	public void setLogs(final List<LogEntry> logs) {
		this.logs = logs;
	}
	
	
	public static enum State {
		RUNNING, FINISHED, FAILED, UNKNOWN
	}

	public static class LogEntry {

		private String timestamp;

		private String message;
		
		
		public LogEntry() {
			
		}
		
		public LogEntry(final String timestamp, final String message) {
			this.timestamp = timestamp;
			this.message = message;
		}
		
		public String getTimestamp() {
			return this.timestamp;
		}
		
		public void setTimestamp(final String timestamp) {
			this.timestamp = timestamp;
		}
		
		public String getMessage() {
			return this.message;
		}
		
		public void setMessage(final String message) {
			this.message = message;
		}
	}
}
