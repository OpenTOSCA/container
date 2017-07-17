package org.opentosca.container.portability.model;

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
	
	
	public ImplementationArtifact(final String operationName, final String interfaceName, final String type, final ArtifactReferences references) {
		super();
		this.setOperationName(operationName);
		this.setInterfaceName(interfaceName);
		this.setType(type);
		this.setReferences(references);
	}

	public ImplementationArtifact(final String operationName, final String interfaceName, final String type, final List<String> references) {
		super();
		this.setOperationName(operationName);
		this.setInterfaceName(interfaceName);
		this.setType(type);
		this.setReferences(new ArtifactReferences(references));
	}

	public ImplementationArtifact(final String operationName, final String interfaceName, final String type, final Document artifactSpecificContent) {
		super();
		this.setOperationName(operationName);
		this.setInterfaceName(interfaceName);
		this.setType(type);
		this.artifactSpecificContent = artifactSpecificContent;
	}

	public ImplementationArtifact(final String operationName, final String type) {
		super();
		this.setOperationName(operationName);
		this.setType(type);
	}

	@XmlTransient
	public Document getArtifactSpecificContent() {
		return this.artifactSpecificContent;
	}

	public void setArtifactSpecificContent(final Document artifactSpecificContent) {
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
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	@XmlAttribute
	public String getOperationName() {
		return this.operationName;
	}

	public void setOperationName(final String operationName) {
		this.operationName = operationName;
	}

	@XmlAttribute
	public String getInterfaceName() {
		return this.interfaceName;
	}

	public void setInterfaceName(final String interfaceName) {
		this.interfaceName = interfaceName;
	}

	@XmlElement(name = "references", type = ArtifactReferences.class)
	public ArtifactReferences getReferences() {
		return this.references;
	}

	public void setReferences(final ArtifactReferences references) {
		this.references = references;
	}
	
}
