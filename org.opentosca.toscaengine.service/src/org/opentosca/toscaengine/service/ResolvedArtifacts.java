package org.opentosca.toscaengine.service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

/**
 * This class holds data for an resolved Artifact (Implementation or DeploymentArtifact) therefore it contains name,
 * type and artifactSpecificContent
 * 
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 * 
 */
public class ResolvedArtifacts {
	
	/**
	 * Class to hold all fields used by implementation and deploymentArtifacts
	 * This is a generic class so therefore there wont be an object of this (abstract!)
	 * Use a child class instead (see ResolvedDeploymentArtifact and ResolvedImplementationArtifact)
	 */
	private abstract static class ResolvedArtifact {
		
		private QName type;
		
		private Document artifactSpecificContent;
		
		private List<String> references;
		
		public QName getType() {
			return type;
		}
		
		public void setType(QName type) {
			this.type = type;
		}
		
		public Document getArtifactSpecificContent() {
			return artifactSpecificContent;
		}
		
		public void setArtifactSpecificContent(Document artifactSpecificContent) {
			this.artifactSpecificContent = artifactSpecificContent;
		}
		
		public List<String> getReferences() {
			return references;
		}
		
		public void setReferences(List<String> references) {
			this.references = references;
		}
		
	}
	
	/**
	 * extends ResolvedArtifact by the DeploymentArtifact-specific field name
	 */
	public static class ResolvedDeploymentArtifact extends ResolvedArtifact {
		
		private String name;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
	}
	
	/**
	 * extends ResolvedArtifact by the ImplementationArtifact-specific fields operationName and interfaceName
	 */
	public static class ResolvedImplementationArtifact extends ResolvedArtifact {
		
		private String operationName;
		
		private String interfaceName;
		
		public String getOperationName() {
			return operationName;
		}
		
		public void setOperationName(String operationName) {
			this.operationName = operationName;
		}
		
		public String getInterfaceName() {
			return interfaceName;
		}
		
		public void setInterfaceName(String interfaceName) {
			this.interfaceName = interfaceName;
		}
	}
	
	private List<ResolvedDeploymentArtifact> deploymentArtifacts = new ArrayList<ResolvedDeploymentArtifact>();
	private List<ResolvedImplementationArtifact> implementationArtifacts = new ArrayList<ResolvedImplementationArtifact>();
	
	public List<ResolvedDeploymentArtifact> getDeploymentArtifacts() {
		return deploymentArtifacts;
	}
	
	public void setDeploymentArtifacts(List<ResolvedDeploymentArtifact> deploymentArtifacts) {
		this.deploymentArtifacts = deploymentArtifacts;
	}
	
	public List<ResolvedImplementationArtifact> getImplementationArtifacts() {
		return implementationArtifacts;
	}
	
	public void setImplementationArtifacts(List<ResolvedImplementationArtifact> implementationArtifacts) {
		this.implementationArtifacts = implementationArtifacts;
	};
	
}
