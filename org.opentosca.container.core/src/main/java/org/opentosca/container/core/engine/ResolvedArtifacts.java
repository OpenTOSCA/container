package org.opentosca.container.core.engine;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

/**
 * This class holds data for an resolved Artifact (Implementation or DeploymentArtifact) therefore
 * it contains name, type and artifactSpecificContent
 */
public class ResolvedArtifacts {

  /**
   * Class to hold all fields used by implementation and deploymentArtifacts This is a generic class
   * so therefore there wont be an object of this (abstract!) Use a child class instead (see
   * ResolvedDeploymentArtifact and ResolvedImplementationArtifact)
   */
  private abstract static class ResolvedArtifact {

    private QName type;

    private Document artifactSpecificContent;

    private List<String> references;

    public QName getType() {
      return this.type;
    }

    public void setType(final QName type) {
      this.type = type;
    }

    public Document getArtifactSpecificContent() {
      return this.artifactSpecificContent;
    }

    public void setArtifactSpecificContent(final Document artifactSpecificContent) {
      this.artifactSpecificContent = artifactSpecificContent;
    }

    public List<String> getReferences() {
      return this.references;
    }

    public void setReferences(final List<String> references) {
      this.references = references;
    }

  }

  /**
   * extends ResolvedArtifact by the DeploymentArtifact-specific field name
   */
  public static class ResolvedDeploymentArtifact extends ResolvedArtifact {

    private String name;

    public String getName() {
      return this.name;
    }

    public void setName(final String name) {
      this.name = name;
    }
  }

  /**
   * extends ResolvedArtifact by the ImplementationArtifact-specific fields operationName and
   * interfaceName
   */
  public static class ResolvedImplementationArtifact extends ResolvedArtifact {

    private String operationName;

    private String interfaceName;

    public String getOperationName() {
      return this.operationName;
    }

    public void setOperationName(final String operationName) {
      this.operationName = operationName;
    }

    public String getInterfaceName() {
      return this.interfaceName;
    }

    public void setInterfaceName(final String interfaceName) {
      this.interfaceName = interfaceName;
    }
  }

  private List<ResolvedDeploymentArtifact> deploymentArtifacts = new ArrayList<>();
  private List<ResolvedImplementationArtifact> implementationArtifacts = new ArrayList<>();

  public List<ResolvedDeploymentArtifact> getDeploymentArtifacts() {
    return this.deploymentArtifacts;
  }

  public void setDeploymentArtifacts(final List<ResolvedDeploymentArtifact> deploymentArtifacts) {
    this.deploymentArtifacts = deploymentArtifacts;
  }

  public List<ResolvedImplementationArtifact> getImplementationArtifacts() {
    return this.implementationArtifacts;
  }

  public void setImplementationArtifacts(final List<ResolvedImplementationArtifact> implementationArtifacts) {
    this.implementationArtifacts = implementationArtifacts;
  }
}
