package org.opentosca.container.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.model.instance.ServiceInstanceId;
import org.opentosca.container.core.model.instance.State;
import org.opentosca.container.core.tosca.extension.TParameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "PlanInstance")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanInstanceDTO extends ResourceSupport {
	
	@XmlAttribute(name = "id")
	private String id;
	
	@XmlAttribute(name = "state")
	private State.Plan state;

	@XmlElement(name = "OutputParameter")
	@XmlElementWrapper(name = "OutputParameters")
	private List<TParameter> output;

	@XmlElement(name = "LogEntry")
	@XmlElementWrapper(name = "Logs")
	private List<LogEntry> logs;
	
	@JsonIgnore
	private ServiceInstanceId serviceTemplateInstance;
	
	
	public PlanInstanceDTO() {

	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public State.Plan getState() {
		return this.state;
	}
	
	public void setState(final State.Plan state) {
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

	public ServiceInstanceId getServiceTemplateInstance() {
		return this.serviceTemplateInstance;
	}

	public void setServiceTemplateInstance(final ServiceInstanceId serviceTemplateInstance) {
		this.serviceTemplateInstance = serviceTemplateInstance;
	}


	@XmlRootElement(name = "LogEntry")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class LogEntry {

		@XmlElement(name = "Timestamp")
		private String timestamp;

		@XmlElement(name = "Message")
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
