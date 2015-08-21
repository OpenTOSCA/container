package org.opentosca.planbuilder.provphase.plugin.wsdloperation;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.plugins.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.provphase.plugin.wsdloperation.handlers.AsyncHandler;
import org.opentosca.planbuilder.provphase.plugin.wsdloperation.handlers.SyncHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * This class implements a ProvPhase Plugin, in particular to use WSDL
 * Operations to provision Templates inside a Topology
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class Plugin implements IPlanBuilderProvPhaseOperationPlugin {
	
	private final static Logger LOG = LoggerFactory.getLogger(Plugin.class);
	
	private QName openTOSCAWar = new QName("http://www.example.com/ToscaTypes", "WAR");
	private SyncHandler syncHandler;
	private AsyncHandler asyncHandler;
	
	
	/**
	 * Contructor
	 */
	public Plugin() {
		this.syncHandler = new SyncHandler();
		this.asyncHandler = new AsyncHandler();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getID() {
		return "OpenTOSCA Plan Builder WSDL Operation Plugin";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(QName artifactType) {
		return artifactType.equals(this.openTOSCAWar);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handle(TemplatePlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia) {
		
		Plugin.LOG.debug("Checking if wsdlMapping is declared, in IA {}", ia.getName());
		// check if wsdl mapping is specified
		Element wsdlMappingElement = null;
		for (AbstractProperties props : ia.getAdditionalElements()) {
			if (props.getDOMElement().getLocalName().equals("wsdlMapping") && props.getDOMElement().getNamespaceURI().equals("http://example.com/ba")) {
				wsdlMappingElement = props.getDOMElement();
			}
		}
		
		if (wsdlMappingElement != null) {
			// found wsdlMapping check if synchronous or asynchronous
			if (this.isSyncMapping(wsdlMappingElement)) {
				return this.syncHandler.handle(context, operation, ia);
			} else {
				return this.asyncHandler.handle(context, operation, ia);
			}
		} else {
			Plugin.LOG.warn("No wsdlMapping declared");
			return false;
		}
	}
	
	/**
	 * Returns whether this given wsdlMapping element is synchronous or
	 * asynchronous
	 * 
	 * @param wsdlMappingElement a DOM Element containing a wsdlMapping
	 * @return true iff mapping is synchronous, this means there is only one
	 *         portType and one operation specified
	 */
	private boolean isSyncMapping(Element wsdlMappingElement) {
		Plugin.LOG.debug("Checking if wsdlMapping is synchronous or asynchronous");
		
		int counter = 0;
		NodeList children = wsdlMappingElement.getChildNodes();
		
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			
			if (((child.getLocalName() != null) && child.getLocalName().equals("portType")) || child.getNodeName().equals("portType")) {
				Plugin.LOG.debug("Found elements with localName portType in wsdlMapping");
				counter++;
			}
			if (((child.getLocalName() != null) && child.getLocalName().equals("callbackPortType")) || child.getNodeName().equals("callbackPortType")) {
				Plugin.LOG.debug("Found elements with localName callbackPortType in wsdlMapping");
				counter++;
			}
			if (((child.getLocalName() != null) && child.getLocalName().equals("wsdlOperation")) || child.getNodeName().equals("wsdlOperation")) {
				Plugin.LOG.debug("Found elements with localName wsdlOperation in wsdlMapping");
				counter++;
			}
			if (((child.getLocalName() != null) && child.getLocalName().equals("wsdlCallbackOperation")) || child.getNodeName().equals("wsdlCallbackOperation")) {
				Plugin.LOG.debug("Found elements with localName wsdlCallbackOperation in wsdlMapping");
				counter++;
			}
			
		}
		// if there are two portTypes/operations specified -> mapping is async
		if (counter == 4) {
			Plugin.LOG.debug("wsdlMapping is asynchronous");
			return false;
		} else {
			Plugin.LOG.debug("wsdlMapping is synchronous");
			return true;
		}
	}
	
}
