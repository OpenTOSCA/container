package org.opentosca.iaengine.plugins.script.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.core.model.artifact.AbstractArtifact;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.iaengine.plugins.script.service.impl.util.Messages;
import org.opentosca.iaengine.plugins.service.IIAEnginePluginService;
import org.opentosca.model.tosca.TPropertyConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * IAEnginePlugin for Scripts.<br>
 * <br>
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * Since scripts dont have to be deployed, this plugin just ensure that
 * script-IAs won't be marked as failed.
 * 
 * 
 * 
 * @see ICoreFileService
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 * 
 * 
 */
public class IAEnginePluginScriptServiceImpl implements IIAEnginePluginService {
	
	// In messages.properties defined plugin-type and capabilities .
	static final private String TYPES = Messages.ScriptIAEnginePlugin_types;
	static final private String CAPABILITIES = Messages.ScriptIAEnginePlugin_capabilities;
	
	static final private Logger LOG = LoggerFactory.getLogger(IAEnginePluginScriptServiceImpl.class);
	
	
	@Override
	public URI deployImplementationArtifact(CSARID csarID, QName artifactType, Document artifactContent, Document properties, List<TPropertyConstraint> propertyConstraints, List<AbstractArtifact> artifacts, List<String> requiredFeatures) {
		
		// Maybe some checks can be done here. (ScriptLanguage supported?,
		// Script defined?, Script contained in csar file?, SI-Script-Plugin
		// available...)
		URI uri = null;
		try {
			uri = new URI("si:ScriptPlugin");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public List<String> getSupportedTypes() {
		IAEnginePluginScriptServiceImpl.LOG.debug("Getting Types: {}.", IAEnginePluginScriptServiceImpl.TYPES);
		List<String> types = new ArrayList<String>();
		
		for (String type : IAEnginePluginScriptServiceImpl.TYPES.split("[,;]")) {
			types.add(type.trim());
		}
		return types;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public List<String> getCapabilties() {
		IAEnginePluginScriptServiceImpl.LOG.debug("Getting Plugin-Capabilities: {}.", IAEnginePluginScriptServiceImpl.CAPABILITIES);
		List<String> capabilities = new ArrayList<String>();
		
		for (String capability : IAEnginePluginScriptServiceImpl.CAPABILITIES.split("[,;]")) {
			capabilities.add(capability.trim());
		}
		return capabilities;
	}
	
	@Override
	public boolean undeployImplementationArtifact(String iaName, QName nodeTypeImpl, CSARID csarID, URI path) {
		// TODO
		return false;
	}
	
}
