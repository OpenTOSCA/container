package org.opentosca.planbuilder.importer.context.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.TDeploymentArtifact;
import org.oasis_open.docs.tosca.ns._2011._12.TImplementationArtifact;
import org.oasis_open.docs.tosca.ns._2011._12.TNodeTypeImplementation;
import org.oasis_open.docs.tosca.ns._2011._12.TRequiredContainerFeature;
import org.oasis_open.docs.tosca.ns._2011._12.TTag;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class implements a TOSCA NodeTypeImplementation, in particular an
 * AbstractNodeTypeImplementation
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class NodeTypeImplementationImpl extends AbstractNodeTypeImplementation {

	private final static Logger LOG = LoggerFactory.getLogger(NodeTypeImplementationImpl.class);

	private DefinitionsImpl definitions;
	private TNodeTypeImplementation nodeTypeImpl;
	private List<AbstractTag> tags;
	private List<AbstractImplementationArtifact> ias;
	private List<AbstractDeploymentArtifact> das;


	/**
	 * Constructor
	 *
	 * @param nodeTypeImpl a JAXB TNodeTypeImplementation
	 * @param definitionsImpl a DefinitionsImpl
	 */
	public NodeTypeImplementationImpl(TNodeTypeImplementation nodeTypeImpl, DefinitionsImpl definitionsImpl) {
		this.definitions = definitionsImpl;
		this.nodeTypeImpl = nodeTypeImpl;
		this.tags = new ArrayList<AbstractTag>();
		this.ias = new ArrayList<AbstractImplementationArtifact>();
		this.das = new ArrayList<AbstractDeploymentArtifact>();
		LOG.debug("Initializing NodeTypeImplementation {" + this.nodeTypeImpl.getTargetNamespace() + "}" + this.nodeTypeImpl.getName());
		this.initTags();
		this.initIas();
		this.initDas();
	}
	
	/**
	 * Initializes the internal IAs
	 */
	private void initIas() {		
		if (this.nodeTypeImpl.getImplementationArtifacts() != null) {
			for (TImplementationArtifact artifact : this.nodeTypeImpl.getImplementationArtifacts().getImplementationArtifact()) {
				this.ias.add(new ImplementationArtifactImpl(artifact, this.definitions));
			}
		}
	}
	
	/**
	 * Initializes the internal DAs
	 */
	private void initDas() {
		if (this.nodeTypeImpl.getDeploymentArtifacts() != null) {
			for (TDeploymentArtifact artifact : this.nodeTypeImpl.getDeploymentArtifacts().getDeploymentArtifact()) {
				this.das.add(new DeploymentArtifactImpl(artifact, this.definitions));
			}
		}
	}
	
	/**
	 * Initializes the internal Tags
	 */
	private void initTags() {
		if (this.nodeTypeImpl.getTags() != null) {
			for (TTag tag : this.nodeTypeImpl.getTags().getTag()) {
				this.tags.add(new TagImpl(tag));
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.nodeTypeImpl.getName();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTargetNamespace() {
		return this.nodeTypeImpl.getTargetNamespace();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAbstract() {
		return this.nodeTypeImpl.getAbstract().value().equals("yes") ? true : false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFinal() {
		return this.nodeTypeImpl.getFinal().value().equals("yes") ? true : false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractTag> getTags() {
		return this.tags;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getRequiredContainerFeatures() {
		// TODO make this non-hacky
		List<String> features = new ArrayList<String>();
		for (TRequiredContainerFeature feature : this.nodeTypeImpl.getRequiredContainerFeatures().getRequiredContainerFeature()) {
			features.add(feature.getFeature());
		}
		return features;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QName getDerivedFrom() {
		// TODO return the nodetypeimplementation instead of qname
		return this.nodeTypeImpl.getDerivedFrom().getNodeTypeImplementationRef();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractImplementationArtifact> getImplementationArtifacts() {
		return this.ias;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractDeploymentArtifact> getDeploymentArtifacts() {
		return this.das;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractNodeType getNodeType() {
		if (this.nodeTypeImpl.getNodeType() == null) {
			NodeTypeImplementationImpl.LOG.error("NodeTypeImplementation {} has no defined nodeType", "{" + this.getTargetNamespace() + "}" + this.getName());
		}
		for(AbstractNodeType nodeType :this.definitions.getAllNodeTypes()){
			if(nodeType.getId().toString().equals(this.nodeTypeImpl.getNodeType().toString())){
				return nodeType;
			}
		}
		return null;
	}

}
