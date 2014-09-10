package org.opentosca.portability.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ImplementationArtifact {
	
	private String operationName;
	
	private String interfaceName;
	
	private String type;
	
	private ArtifactReferences references;
	
	private Document artifactSpecificContent;
	
	public ImplementationArtifact(String operationName, String interfaceName, String type, ArtifactReferences references) {
		super();
		this.setOperationName(operationName);
		this.setInterfaceName(interfaceName);
		this.setType(type);
		this.setReferences(references);
	}
	
	public ImplementationArtifact(String operationName, String interfaceName, String type, List<String> references) {
		super();
		this.setOperationName(operationName);
		this.setInterfaceName(interfaceName);
		this.setType(type);
		this.setReferences(new ArtifactReferences(references));
	}
	
	public ImplementationArtifact(String operationName, String interfaceName, String type, Document artifactSpecificContent) {
		super();
		this.setOperationName(operationName);
		this.setInterfaceName(interfaceName);
		this.setType(type);
		this.artifactSpecificContent = artifactSpecificContent;
	}
	
	public ImplementationArtifact(String operationName, String type) {
		super();
		this.setOperationName(operationName);
		this.setType(type);
	}
	
	@XmlTransient
	public Document getArtifactSpecificContent() {
		return artifactSpecificContent;
	}
	
	public void setArtifactSpecificContent(Document artifactSpecificContent) {
		this.artifactSpecificContent = artifactSpecificContent;
	}
	
	@XmlAnyElement(lax = false)
	private Element getJaxbArtifactSpecificContent() {
		if (this.artifactSpecificContent == null) {
			return null;
		}
		return this.artifactSpecificContent.getDocumentElement();
	}
	
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@XmlAttribute
	public String getOperationName() {
		return operationName;
	}
	
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	
	@XmlAttribute
	public String getInterfaceName() {
		return interfaceName;
	}
	
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	
	@XmlElement(name = "references", type = ArtifactReferences.class)
	public ArtifactReferences getReferences() {
		return references;
	}
	
	public void setReferences(ArtifactReferences references) {
		this.references = references;
	}
	
	
}
