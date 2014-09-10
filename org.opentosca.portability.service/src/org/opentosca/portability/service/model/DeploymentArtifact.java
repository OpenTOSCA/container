package org.opentosca.portability.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
public class DeploymentArtifact {
	
	
	private String name;
	private String type;
	
	private ArtifactReferences references;
	
	
	private Document artifactSpecificContent;
	
	protected DeploymentArtifact() {
		
	}
	
	public DeploymentArtifact(String name, String type, ArtifactReferences references) {
		super();
		this.setName(name);
		this.setType(type);
		this.setReferences(references);
	}
	
	public DeploymentArtifact(String name, String type, List<String> references) {
		super();
		this.setName(name);
		this.setType(type);
		this.setReferences(new ArtifactReferences(references));
	}
	
	public DeploymentArtifact(String name, String type, Document artifactSpecificContent) {
		super();
		this.setName(name);
		this.setType(type);
		this.setArtifactSpecificContent(artifactSpecificContent);
	}
	
	public DeploymentArtifact(String name, String type) {
		super();
		this.setName(name);
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
		if (artifactSpecificContent == null) {
			return null;
		}
		return this.artifactSpecificContent.getDocumentElement();
	}
	
	@XmlAttribute
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@XmlElement(name = "references", type = ArtifactReferences.class)
	public ArtifactReferences getReferences() {
		return references;
	}
	
	public void setReferences(ArtifactReferences references) {
		this.references = references;
	}
	
}
