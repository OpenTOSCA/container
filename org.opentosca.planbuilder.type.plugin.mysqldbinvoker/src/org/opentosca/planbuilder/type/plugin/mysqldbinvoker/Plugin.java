/**
 *
 */
package org.opentosca.planbuilder.type.plugin.mysqldbinvoker;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipType;
import org.opentosca.planbuilder.plugins.IPlanBuilderGenericPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.type.plugin.mysqldbinvoker.handler.Handler;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Plugin implements IPlanBuilderGenericPlugin {

	private final static Logger LOG = LoggerFactory.getLogger(Plugin.class);

	private Handler handler;

	public Plugin() {
		try {
			this.handler = new Handler();
		} catch (ParserConfigurationException e) {
			Plugin.LOG.error("Couldn't instantiate internal handler object", e);
		}
	}

	@Override
	public String getID() {
		return "OpenTOSCA MySQL Database Type Plugin";
	}

	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		LOG.debug("Handling NodeTemplate " + templateContext.getNodeTemplate().getId());
		if ((templateContext.getNodeTemplate() != null) && this.canHandle(templateContext.getNodeTemplate())) {
			AbstractNodeTypeImplementation nodeImpl = this.selectNodeTypeImplementation(templateContext);
			return this.handler.handle(templateContext, nodeImpl);
		}
		return false;
	}

	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		LOG.debug("Checking if given NodeTemplate " + nodeTemplate.getId() + " can be handled");
		// check for the mysql db type and whether it's connected to some mysql
		// server with connected vm

		if (Utils.checkForTypeInHierarchy(nodeTemplate, Constants.mySqlDbType)) {
			LOG.debug("Is valid nodeType");
			for (AbstractRelationshipTemplate relationship : nodeTemplate.getOutgoingRelations()) {
				AbstractNodeTemplate target = relationship.getTarget();
				LOG.debug("Checking target NodeTemplate " + target.getId());

				if (Utils.checkForTypeInHierarchy(target, Constants.mySqlServerType)) {
					LOG.debug("Found connection to mysql server");
					// found a mysql server connection

					boolean isConnectedToVm = this.isConnectedToVM(relationship.getTarget());
					if (isConnectedToVm) {
						LOG.debug("Found connection to VM");
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		// can't handle relationshipTemplates
		return false;
	}

	/**
	 * Checks whether there is some VM along the path from the given
	 * nodeTemplate to the sinks of the Topology
	 *
	 * @param nodeTemplate an AbstractNodeTemplate
	 * @return true iff there exists a path from the given NodeTemplate to a VM
	 *         Template in direction to the sinks
	 */
	private boolean isConnectedToVM(AbstractNodeTemplate nodeTemplate) {
		List<AbstractNodeTemplate> nodes = new ArrayList<AbstractNodeTemplate>();
		Utils.getNodesFromNodeToSink(nodeTemplate, nodes);
		for (AbstractNodeTemplate node : nodes) {
			if (Utils.checkForTypeInHierarchy(node, Constants.vmType)) {
				return true;
			}
		}
		return false;
	}

	private AbstractNodeTypeImplementation selectNodeTypeImplementation(TemplatePlanContext templateContext) {
		AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
		if (nodeTemplate == null) {
			return null;
		}
		AbstractNodeType nodeType = nodeTemplate.getType();

		AbstractInterface usableIface = null;

		// check whether the nodeType contains any TOSCA interface
		for (AbstractInterface iface : nodeType.getInterfaces()) {
			if (iface.getName().equals("http://docs.oasis-open.org/tosca/ns/2011/12/interfaces/lifecycle")) {
				// check if we have operations to work with, e.g. install,
				// configure and start
				int toscaOperations = 0;
				for (AbstractOperation operation : iface.getOperations()) {
					switch (operation.getName()) {
					case "install":
					case "start":
					case "configure":
						toscaOperations++;
						break;
					default:
						break;
					}
				}
				if (toscaOperations != iface.getOperations().size()) {
					// we just accept pure TOSCA interfaces
					continue;
				} else {
					usableIface = iface;
				}
			}
		}

		for (AbstractNodeTypeImplementation nodeImpl : nodeTemplate.getImplementations()) {

			// check the IA's with the found interfaces, and if we found an IA
			// we
			// can use for one of the interfaces we'll use that
			List<AbstractImplementationArtifact> iasForInterfaces = this.getIAsForLifecycleInterface(this.getIAsForInterfaces(nodeImpl.getImplementationArtifacts()));
			List<AbstractImplementationArtifact> iasForOperations = this.getIAsForLifecycleInterface(this.getIAsForOperations(nodeImpl.getImplementationArtifacts()));

			// first check if we have an IA for a whole interface
			if (iasForInterfaces.size() == 1) {
				// found an implementation for the lifecycle interface ->
				// nodeTypeImpl will suffice
				return nodeImpl;
			}

			if (usableIface != null) {
				// check if operations in the interface are implementated by
				// single
				// ia's
				if (usableIface.getOperations().size() == iasForOperations.size()) {
					// TODO pretty vague check but should suffice
					return nodeImpl;
				}
			} else {
				// if the node doesn't have an interface basically no extra
				// operations will be executed, just upload of da's into the
				// right spots
				return nodeImpl;
			}

		}
		return null;
	}

	private List<AbstractImplementationArtifact> getIAsForInterfaces(List<AbstractImplementationArtifact> ias) {
		List<AbstractImplementationArtifact> iasForIfaces = new ArrayList<AbstractImplementationArtifact>();
		for (AbstractImplementationArtifact ia : ias) {
			if ((ia.getOperationName() == null) || ia.getOperationName().equals("")) {
				iasForIfaces.add(ia);
			}
		}
		return iasForIfaces;
	}

	private List<AbstractImplementationArtifact> getIAsForOperations(List<AbstractImplementationArtifact> ias) {
		List<AbstractImplementationArtifact> iasForIfaces = new ArrayList<AbstractImplementationArtifact>();
		for (AbstractImplementationArtifact ia : ias) {
			if ((ia.getOperationName() != null) && !ia.getOperationName().equals("")) {
				iasForIfaces.add(ia);
			}
		}
		return iasForIfaces;
	}

	private List<AbstractImplementationArtifact> getIAsForLifecycleInterface(List<AbstractImplementationArtifact> ias) {
		List<AbstractImplementationArtifact> iasForIfaces = new ArrayList<AbstractImplementationArtifact>();
		for (AbstractImplementationArtifact ia : ias) {
			if (ia.getInterfaceName().equals("http://docs.oasis-open.org/tosca/ns/2011/12/interfaces/lifecycle")) {
				iasForIfaces.add(ia);
			}
		}
		return iasForIfaces;
	}

}
