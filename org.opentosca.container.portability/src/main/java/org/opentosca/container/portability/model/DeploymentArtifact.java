package org.opentosca.container.portability.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DeploymentArtifact {

  private String name;
  private String type;

  private ArtifactReferences references;

  private Document artifactSpecificContent;


  protected DeploymentArtifact() {

  }

  public DeploymentArtifact(final String name, final String type, final ArtifactReferences references) {
    super();
    this.setName(name);
    this.setType(type);
    this.setReferences(references);
  }

  public DeploymentArtifact(final String name, final String type, final List<String> references) {
    super();
    this.setName(name);
    this.setType(type);
    this.setReferences(new ArtifactReferences(references));
  }

  public DeploymentArtifact(final String name, final String type, final Document artifactSpecificContent) {
    super();
    this.setName(name);
    this.setType(type);
    this.setArtifactSpecificContent(artifactSpecificContent);
  }

  public DeploymentArtifact(final String name, final String type) {
    super();
    this.setName(name);
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
  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @XmlAttribute
  public String getType() {
    return this.type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  @XmlElement(name = "references", type = ArtifactReferences.class)
  public ArtifactReferences getReferences() {
    return this.references;
  }

  public void setReferences(final ArtifactReferences references) {
    this.references = references;
  }

}
