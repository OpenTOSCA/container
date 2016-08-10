package org.opentosca.containerapi.resources.csar.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is currently not in Use.<br>
 * <br>
 * 
 * Class to provide JAXB-Annotation for a DeploymentArtifact including its
 * absolutePath on the filesystem <br>
 * 
 * <br>
 * 
 * To be used by Jersey to automatically generate XML-Responses<br>
 * <br>
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 * 
 */
@XmlRootElement(name = "DeploymentArtifact")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeploymentArtifactAbsJaxb {
	
	@XmlAttribute(name = "AbsolutePath")
	private String absolutePath;
	@XmlAttribute(name = "Type")
	private String type;
	@XmlAttribute(name = "Name")
	private String name;
	
	
	public DeploymentArtifactAbsJaxb() {
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getAbsolutePath() {
		return this.absolutePath;
	}
	
	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}
}
