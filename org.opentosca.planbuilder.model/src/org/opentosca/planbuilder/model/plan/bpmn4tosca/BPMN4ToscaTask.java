package org.opentosca.planbuilder.model.plan.bpmn4tosca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.plan.bpmn4tosca.parameter.Parameter;
import org.opentosca.planbuilder.model.plan.bpmn4tosca.parameter.StringParameter;

public class BPMN4ToscaTask extends BPMN4ToscaElement {
	private String interfaceName;
	private QName nodeTemplateId;
	private String nodeOperation;
	private final List<Parameter> inputParameter = new ArrayList<>();
	private final List<Parameter> outputParameter = new ArrayList<>();

	public BPMN4ToscaTask addInputParameter(Parameter parameter) {
		this.outputParameter.add(parameter);
		return this;
	}

	public BPMN4ToscaTask addInputParameter(String key, String value) {
		this.inputParameter.add(new StringParameter(key, value));
		return this;
	}

	public BPMN4ToscaTask addOutputParameter(Parameter parameter) {
		this.outputParameter.add(parameter);
		return this;
	}

	public BPMN4ToscaTask addOutputParameter(String key, String value) {
		this.outputParameter.add(new StringParameter(key, value));
		return this;
	}

	public List<Parameter> getInputParameter() {
		return this.inputParameter;
	}

	public String getInterfaceName() {
		return this.interfaceName;
	}

	public String getNodeOperation() {
		return this.nodeOperation;
	}

	public QName getNodeTemplateId() {
		return this.nodeTemplateId;
	}

	public List<Parameter> getOutputParameter() {
		return this.outputParameter;
	}

	@Override
	public BPMN4ToscaElementType getType() {
		return BPMN4ToscaElementType.TOSCA_MANAGEMENT_TASK;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public void setNodeOperation(String nodeOperation) {
		this.nodeOperation = nodeOperation;
	}

	public void setNodeTemplateId(QName nodeTemplateId) {
		this.nodeTemplateId = nodeTemplateId;
	}

}
