/**
 *
 */
package org.opentosca.planbuilder.generic.plugin.phpappinvoker;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.generic.plugin.phpappinvoker.handler.Handler;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderGenericPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * <p>
 * Plugin class for Php Application deployment.
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Plugin implements IPlanBuilderGenericPlugin {

	private final QName phpApp = new QName("http://www.example.com/tosca/ServiceTemplates/ApacheWebServer", "OpenTOSCAPhpApplicationApacheHTTP");
	private final QName apacheWebServer = new QName("http://www.example.com/tosca/ServiceTemplates/ApacheWebServer", "OpenTOSCAApacheWebServer");
	private final QName phpModule = new QName("http://www.example.com/tosca/ServiceTemplates/ApacheWebServer", "OpenTOSCAApacheWebServerPhpModule");

	private final QName phpAppNodeTypePlanBuilder = new QName("http://opentosca.org/types/declarative", "PhpApplication");
	private final QName apacheWebServerNodeTypeTOSCASpecificType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaSpecificTypes", "ApacheWebServer");
	private final QName phpModuleNodeTypeTOSCASpecificType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaSpecificTypes", "ApachePHPModule");

	private final QName zipArtifactType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ArchiveArtifact");
	private Handler handler;

	private final static Logger LOG = LoggerFactory.getLogger(Plugin.class);


	public Plugin() {
		try {
			this.handler = new Handler();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getID() {
		return "OpenTOSCA Php Application Declarative Plugin";
	}

	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		if ((templateContext.getNodeTemplate() != null) && this.canHandle(templateContext.getNodeTemplate())) {
			AbstractNodeTypeImplementation nodeImpl = this.selectNodeTypeImplementation(templateContext);
			return this.handler.handle(templateContext, nodeImpl);
		}

		return false;
	}

	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		LOG.debug("Checking if nodeTemplate " + nodeTemplate.getId() + " can be handled");
		if (this.isCompatiblePhpAppNodeType(nodeTemplate.getType())) {
			LOG.debug("NodeTemplate nodeType is okay");
			// nodeType is okay, check if connected to PhpModule and ApacheHTTP
			// TODO should we also check if this node is connected exclusively
			// to apache and php ?
			int isConnectedToPhpModuleAndApacheWebServerCheck = 0;
			boolean hasZipDA = false;
			for (AbstractRelationshipTemplate relation : nodeTemplate.getOutgoingRelations()) {
				if (this.isCompatibleApacheWebServerNodeType(relation.getTarget().getType().getId())) {
					isConnectedToPhpModuleAndApacheWebServerCheck++;
				}
				if (this.isCompatiblePhpModuleNodeType(relation.getTarget().getType().getId())) {
					isConnectedToPhpModuleAndApacheWebServerCheck++;
				}
			}
			if (isConnectedToPhpModuleAndApacheWebServerCheck == 2) {
				LOG.debug("NodeTemplate is properly connected to PhpModule and ApacheWebServer");
				// node is connected with proper nodes, check if there is a
				// proper deployment artifact
				for (AbstractDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
					hasZipDA |= this.hasZipArtfiactReference(da);
				}

				// check whether there is a NodeTypeImplementation with a zip
				// artifact
				for (AbstractNodeTypeImplementation nodeImpl : nodeTemplate.getImplementations()) {
					for (AbstractDeploymentArtifact da : nodeImpl.getDeploymentArtifacts()) {
						hasZipDA |= this.hasZipArtfiactReference(da);
					}
				}
				return hasZipDA;
			}
		}
		return false;
	}

	private boolean hasZipArtfiactReference(AbstractDeploymentArtifact da) {
		if (da.getArtifactType().toString().equals(this.zipArtifactType.toString())) {
			// check reference
			for (AbstractArtifactReference ref : da.getArtifactRef().getArtifactReferences()) {
				if (ref.getReference().endsWith(".zip")) {
					return true;
				}
			}

		}

		return false;
	}

	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		// we can't handle relationshipTemplates
		return false;
	}

	private boolean isCompatibleApacheWebServerNodeType(QName nodeTypeId) {
		if (nodeTypeId.toString().equals(this.apacheWebServer.toString())) {
			return true;
		}
		if (nodeTypeId.toString().equals(this.apacheWebServerNodeTypeTOSCASpecificType.toString())) {
			return true;
		}
		return false;
	}

	private boolean isCompatiblePhpModuleNodeType(QName nodeTypeId) {
		if (nodeTypeId.toString().equals(this.phpModule.toString())) {
			return true;
		}

		if (nodeTypeId.toString().equals(this.phpModuleNodeTypeTOSCASpecificType.toString())) {
			return true;
		}
		return false;
	}

	private boolean isCompatiblePhpAppNodeType(AbstractNodeType nodeType) {
		if (nodeType.getId().toString().equals(this.phpApp.toString())) {
			return true;
		}
		if (nodeType.getId().toString().equals(this.phpAppNodeTypePlanBuilder.toString())) {
			return true;
		}

		// check within type hierarchy
		QName test = nodeType.getId();

		List<QName> typesInHierarchy = Utils.getNodeTypeHierarchy(nodeType);
		// FIXME list.contains doesn't work somehow, altough equals of QName
		// should suffice
		for (QName hierarchyType : typesInHierarchy) {
			if (hierarchyType.toString().equals(this.phpApp.toString()) | hierarchyType.toString().equals(this.phpAppNodeTypePlanBuilder.toString())) {
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
			// check whether all deploymentartifacts are ZipArtifacts
			int zipArtifactCount = 0;
			for (AbstractDeploymentArtifact deplArtifact : nodeImpl.getDeploymentArtifacts()) {
				if (this.isZipArtifact(deplArtifact)) {
					zipArtifactCount++;
				}
			}

			if (nodeImpl.getDeploymentArtifacts().size() != zipArtifactCount) {
				// this implementation doesn't suit this plugin, skip it
				continue;
			}

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
				// operations will be executed, just upload of zip da's into the
				// right spots
				return nodeImpl;
			}

		}
		return null;
	}

	private boolean isZipArtifact(AbstractDeploymentArtifact artifact) {
		if (artifact.getArtifactType().toString().equals(this.zipArtifactType.toString())) {
			return true;
		} else {
			return false;
		}
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
