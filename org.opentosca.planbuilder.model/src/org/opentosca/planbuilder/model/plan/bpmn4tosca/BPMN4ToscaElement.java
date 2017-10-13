package org.opentosca.planbuilder.model.plan.bpmn4tosca;

/**
 * <p>
 * This class is the main Element of the BPMN4Tosca model.
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Alex Frank - st152404@stud.uni-stuttgart.de
 *
 */
public abstract class BPMN4ToscaElement {
	private String id;
	private String name;
	private BPMN4ToscaElementType type;

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public BPMN4ToscaElementType getType() {
		return this.type;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(BPMN4ToscaElementType type) {
		this.type = type;
	}
}
