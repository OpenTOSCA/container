package org.opentosca.planbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTypeImplementation;
import org.opentosca.planbuilder.plugins.IPlanBuilderPlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderPrePhaseIAPlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This Class represents the low-level algorithm for the concept in <a href=
 * "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
 * >Konzept und Implementierung eine Java-Komponente zur Generierung von WS-BPEL
 * 2.0 BuildPlans fuer OpenTOSCA</a>. This includes selecting a implementation
 * (NodeTypeImplementation,Relationship..) where all artifacts (IA, DA) and the
 * operations of the template (Node and Relation) can be used for provisioning.
 * All complete possibilities of provisioning are hold inside a
 * ProvisioningChain Object, which can contain more than one possible
 * provisioning.
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class TemplatePlanBuilder {

	private final static Logger LOG = LoggerFactory
			.getLogger(TemplatePlanBuilder.class);

	/**
	 * <p>
	 * This Class is a wrapper class for the other wrapper classes
	 * (IACandidateWrapper,DACandidateWrapper,..). The class also represents if
	 * there are complete provisioning possible with the available template
	 * implementations.
	 * </p>
	 * Copyright 2013 IAAS University of Stuttgart <br>
	 * <br>
	 * 
	 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
	 * 
	 */
	public static class ProvisioningChain {

		// this chain either holds a NodeTemplate or RelationshipTemplate
		private AbstractNodeTemplate nodeTemplate;
		private AbstractRelationshipTemplate relationshipTemplate;

		// lists for all other wrapper classes
		private List<DACandidateWrapper> daCandidates = new ArrayList<DACandidateWrapper>();
		private List<IACandidateWrapper> iaCandidates = new ArrayList<IACandidateWrapper>();
		private List<ProvCandidateWrapper> provCandidates = new ArrayList<ProvCandidateWrapper>();

		/**
		 * <p>
		 * Constructor for a NodeTemplate
		 * </p>
		 * 
		 * @param nodeTemplate
		 *            a NodeTemplate which the ProvisioningChain should belong
		 */
		private ProvisioningChain(AbstractNodeTemplate nodeTemplate) {
			this.nodeTemplate = nodeTemplate;
		}

		/**
		 * <p>
		 * Constructor for a RelationshipTemplate
		 * </p>
		 * 
		 * @param relationshipTemplate
		 *            a RelationshipTemplate which the ProvisioningChain should
		 *            belong
		 */
		private ProvisioningChain(
				AbstractRelationshipTemplate relationshipTemplate) {
			this.relationshipTemplate = relationshipTemplate;
		}

		/**
		 * <p>
		 * Executes the first found IACandidate to provision IA's with the
		 * appropiate plugins set in the candidate
		 * </p>
		 * 
		 * @param context
		 *            a TemplatePlanContext which is initialized for either a
		 *            NodeTemplate or RelationshipTemplate this
		 *            ProvisioningChain belongs to
		 * @return returns false only when execution of a plugin inside the
		 *         IACandidate failed, else true. There may be no IACandidate
		 *         available, because there is no need for IA's to provision. In
		 *         this case true is also returned.
		 */
		public boolean executeIAProvisioning(TemplatePlanContext context) {
			boolean check = true;
			if (!this.iaCandidates.isEmpty()) {
				IACandidateWrapper iaCandidate = this.iaCandidates.get(0);
				for (int index = 0; index < iaCandidate.ias.size(); index++) {
					AbstractImplementationArtifact ia = iaCandidate.ias
							.get(index);
					AbstractNodeTemplate infraNode = iaCandidate.infraNodes
							.get(index);
					IPlanBuilderPrePhaseIAPlugin plugin = iaCandidate.plugins
							.get(index);
					check &= plugin.handle(context, ia, infraNode);
				}
			}
			return check;
		}

		/**
		 * <p>
		 * Executes the first found DACandidate to provision DA's with the
		 * appropiate plugins set in the candidate
		 * </p>
		 * 
		 * @param context
		 *            a TemplatePlanContext which is initialized for either a
		 *            NodeTemplate or RelationshipTemplate this
		 *            ProvisioningChain belongs to
		 * @return returns false only when execution of a plugin inside the
		 *         DACandidate failed, else true. There may be no IACandidate
		 *         available, because there is no need for DA's to provision. In
		 *         this case true is also returned.
		 */
		public boolean executeDAProvisioning(TemplatePlanContext context) {
			boolean check = true;
			if (!this.daCandidates.isEmpty()) {
				DACandidateWrapper daCandidate = this.daCandidates.get(0);
				for (int index = 0; index < daCandidate.das.size(); index++) {
					AbstractDeploymentArtifact da = daCandidate.das.get(index);
					AbstractNodeTemplate infraNode = daCandidate.infraNodes
							.get(index);
					IPlanBuilderPrePhaseDAPlugin plugin = daCandidate.plugins
							.get(index);
					check &= plugin.handle(context, da, infraNode);
				}
			}
			return check;
		}

		/**
		 * <p>
		 * Executes the first found ProvisioningCandidate to execute
		 * provisioning operations with the appropiate plugins set in the
		 * candidate
		 * </p>
		 * 
		 * <p>
		 * <b>Info:</b> A ProvisioningCandidate may not have an appropiate order
		 * of operations set
		 * </p>
		 * 
		 * @param context
		 *            a TemplatePlanContext which is initialized for either a
		 *            NodeTemplate or RelationshipTemplate this
		 *            ProvisioningChain belongs to
		 * @return returns false only when execution of a plugin inside the
		 *         ProvisioningCandidate failed, else true. There may be no
		 *         ProvisioningCandidate available, because there is no need for
		 *         operation to call. In this case true is also returned.
		 */
		public boolean executeOperationProvisioning(TemplatePlanContext context) {
			boolean check = true;
			if (!this.provCandidates.isEmpty()) {
				ProvCandidateWrapper provCandidate = this.provCandidates.get(0);
				for (int index = 0; index < provCandidate.ops.size(); index++) {
					AbstractOperation op = provCandidate.ops.get(index);
					AbstractImplementationArtifact ia = provCandidate.ias
							.get(index);
					IPlanBuilderProvPhaseOperationPlugin plugin = provCandidate.plugins
							.get(index);
					check &= plugin.handle(context, op, ia);
				}
			}
			return check;
		}

		/**
		 * <p>
		 * Executes the first found ProvisioningCandidate to execute
		 * provisioning operations with the appropiate plugins set in the
		 * candidate. The order of calling each operation provisioning is
		 * represented in the given list of strings
		 * </p>
		 * 
		 * @param context
		 *            a TemplatePlanContext which is initialized for either a
		 *            NodeTemplate or RelationshipTemplate this
		 *            ProvisioningChain belongs to
		 * @param operationNames
		 *            a List of String denoting an order of operations (name
		 *            attribute)
		 * @return returns false only when execution of a plugin inside the
		 *         ProvisioningCandidate failed, else true. There may be no
		 *         ProvisioningCandidate available, because there is no need for
		 *         operation to call. In this case true is also returned.
		 */
		public boolean executeOperationProvisioning(
				TemplatePlanContext context, List<String> operationNames) {
			boolean check = true;
			if (!this.provCandidates.isEmpty()) {
				ProvCandidateWrapper provCandidate = this.provCandidates.get(0);
				Map<String, Integer> order = new HashMap<String, Integer>();
				// check for index of prov candidates
				for (String opName : operationNames) {
					for (Integer index = 0; index < provCandidate.ops.size(); index++) {
						AbstractOperation op = provCandidate.ops.get(index);
						if (opName.equals(op.getName())) {
							order.put(opName, index);
						}
					}
				}

				for (String opName : operationNames) {
					Integer index = order.get(opName);
					if (index == null) {
						continue;
					}
					AbstractOperation op = provCandidate.ops.get(index);
					if (!operationNames.contains(op.getName())) {
						// if the operation isn't mentioned in operationName
						// list, don't execute the operation
						continue;
					}
					AbstractImplementationArtifact ia = provCandidate.ias
							.get(index);
					IPlanBuilderProvPhaseOperationPlugin plugin = provCandidate.plugins
							.get(index);
					check &= plugin.handle(context, op, ia);
				}
			}
			return check;
		}

		public List<AbstractDeploymentArtifact> getDAsOfCandidate(
				int candidateIndex) {
			return this.daCandidates.get(candidateIndex).das;
		}
	}

	/**
	 * <p>
	 * This Class is a wrapper for operations that provision a particular
	 * template. This is realized by a mapping between operations, IA's and
	 * ProvPhasePlugins
	 * </p>
	 * 
	 * Copyright 2013 IAAS University of Stuttgart <br>
	 * <br>
	 * 
	 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
	 * 
	 */
	private static class ProvCandidateWrapper {

		// lists which hold the various data, the mapping is enforced with the
		// positions inside the lists
		private List<AbstractOperation> ops = new ArrayList<AbstractOperation>();
		private List<AbstractImplementationArtifact> ias = new ArrayList<AbstractImplementationArtifact>();
		private List<IPlanBuilderProvPhaseOperationPlugin> plugins = new ArrayList<IPlanBuilderProvPhaseOperationPlugin>();

		/**
		 * <p>
		 * Adds a mapping for a operation, an IA and ProvPhasePlugin
		 * </p>
		 * 
		 * @param op
		 *            an AbstractOperation of Template
		 * @param ia
		 *            an AbstractImplementationArtifact which implements the
		 *            given operation
		 * @param plugin
		 *            a ProvPhasePlugin that can execute on the given Operation
		 *            and ImplementationArtifact
		 */
		private void add(AbstractOperation op,
				AbstractImplementationArtifact ia,
				IPlanBuilderProvPhaseOperationPlugin plugin) {
			this.ops.add(op);
			this.ias.add(ia);
			this.plugins.add(plugin);
		}

		/**
		 * <p>
		 * Checks if any Interfaceof the given NodeTemplate can be executed
		 * completely by this ProvisioningCandidate
		 * </p>
		 * 
		 * @param nodeTemplate
		 *            an AbtractNodeTemplate
		 * @return true if one Interface of the NodeTemplate can be provisioned,
		 *         else false
		 */
		private boolean isValid(AbstractNodeTemplate nodeTemplate) {
			for (AbstractInterface iface : nodeTemplate.getType()
					.getInterfaces()) {
				int interfaceSize = iface.getOperations().size();
				if ((interfaceSize == this.ops.size())
						&& (interfaceSize == this.ias.size())
						&& (interfaceSize == this.plugins.size())) {
					int counter = 0;
					for (AbstractOperation iFaceOp : iface.getOperations()) {
						for (AbstractOperation op : this.ops) {
							if (iFaceOp.equals(op)) {
								counter++;
							}
						}
					}
					if (counter == interfaceSize) {
						// found an interface which can be provisioned
						return true;
					}
				}
			}
			return false;
		}

		/**
		 * <p>
		 * Checks whether the mapping of operations/IA/ProvPhasePlugin is valid
		 * for this.
		 * </p>
		 * <p>
		 * <b>INFO:</b> It is assumed that the selected mappings are either for
		 * a TargetInterface or a SourceInterface
		 * </p>
		 * 
		 * @param relationshipTemplate
		 *            an AbstractRelationshipTemplate to check it Interfaces
		 *            with the Mappings
		 * @return true if the Mappings are valid for a Source- or
		 *         TargetInterface of the given RelationshipTemplate, else false
		 */
		private boolean isValid(
				AbstractRelationshipTemplate relationshipTemplate) {
			TemplatePlanBuilder.LOG
					.debug("Checking if the selected provisioning for relationshipTemplate {}",
							relationshipTemplate.getId());
			TemplatePlanBuilder.LOG
					.debug(" with type {} is valid whether on the source or target interface",
							relationshipTemplate.getRelationshipType().getId()
									.toString());
			// check if any source interface matches the selected prov plugins
			for (AbstractInterface iface : relationshipTemplate
					.getRelationshipType().getSourceInterfaces()) {
				int interfaceSize = iface.getOperations().size();
				if ((interfaceSize == this.ops.size())
						&& (interfaceSize == this.ias.size())
						&& (interfaceSize == this.plugins.size())) {
					int counter = 0;
					for (AbstractOperation iFaceOp : iface.getOperations()) {
						for (AbstractOperation op : this.ops) {
							if (iFaceOp.equals(op)) {
								counter++;
							}
						}
					}
					if (counter == interfaceSize) {
						return true;
					}
				}
			}
			// same check for target interfaces
			for (AbstractInterface iface : relationshipTemplate
					.getRelationshipType().getTargetInterfaces()) {
				int interfaceSize = iface.getOperations().size();
				if ((interfaceSize == this.ops.size())
						&& (interfaceSize == this.ias.size())
						&& (interfaceSize == this.plugins.size())) {
					int counter = 0;
					for (AbstractOperation iFaceOp : iface.getOperations()) {
						for (AbstractOperation op : this.ops) {
							if (iFaceOp.equals(op)) {
								counter++;
							}
						}
					}
					if (counter == interfaceSize) {
						return true;
					}
				}
			}
			return false;
		}

	}

	/**
	 * <p>
	 * This Class represents a Mapping of DA's of an Implementation Plugins
	 * which can handle that with matching InfrastructureNode
	 * </p>
	 * 
	 * Copyright 2013 IAAS University of Stuttgart <br>
	 * <br>
	 * 
	 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
	 * 
	 */
	private static class DACandidateWrapper {

		private AbstractNodeTypeImplementation impl;
		private AbstractNodeTemplate nodeTemplate;
		private List<AbstractDeploymentArtifact> das = new ArrayList<AbstractDeploymentArtifact>();
		private List<AbstractNodeTemplate> infraNodes = new ArrayList<AbstractNodeTemplate>();
		private List<IPlanBuilderPrePhaseDAPlugin> plugins = new ArrayList<IPlanBuilderPrePhaseDAPlugin>();

		/**
		 * Constructor determines which NodeTypeImplementation is used
		 * 
		 * @param impl
		 *            an AbstractNodeTypeImplementation with a DA
		 */
		private DACandidateWrapper(AbstractNodeTemplate nodeTemplate,
				AbstractNodeTypeImplementation impl) {
			this.impl = impl;
			this.nodeTemplate = nodeTemplate;
		}

		/**
		 * Adds a mapping from DA to NodeTemplate with a PrePhaseDAPlugin
		 * 
		 * @param da
		 *            the DeploymentArtifact which should be provisioned
		 * @param nodeTemplate
		 *            an InfrastructureNode on which the DA should be deployed
		 * @param plugin
		 *            the PrePhaseDAPlugin which can deploy the DA unto the
		 *            given NodeTemplate
		 */
		private void add(AbstractDeploymentArtifact da,
				AbstractNodeTemplate nodeTemplate,
				IPlanBuilderPrePhaseDAPlugin plugin) {
			this.das.add(da);
			this.infraNodes.add(nodeTemplate);
			this.plugins.add(plugin);
		}

		/**
		 * Checks whether the mappings are valid
		 * 
		 * @return true if all DA's of the NodeTypeImplementation can be
		 *         deployed, else false
		 */
		private boolean isValid() {
			return TemplatePlanBuilder.calculateEffectiveDAs(this.nodeTemplate,
					this.impl).size() == this.das.size();
		}
	}

	/**
	 * <p>
	 * This Class represents mappings from IA's to InfrastructureNodes with
	 * PrePhaseIAPlugins
	 * </p>
	 * Copyright 2013 IAAS University of Stuttgart <br>
	 * <br>
	 * 
	 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
	 * 
	 */
	private static class IACandidateWrapper {

		private AbstractNodeTypeImplementation nodeImpl;
		private AbstractRelationshipTypeImplementation relationImpl;
		private List<AbstractImplementationArtifact> ias = new ArrayList<AbstractImplementationArtifact>();
		private List<AbstractNodeTemplate> infraNodes = new ArrayList<AbstractNodeTemplate>();
		private List<IPlanBuilderPrePhaseIAPlugin> plugins = new ArrayList<IPlanBuilderPrePhaseIAPlugin>();

		/**
		 * Constructor for a NodeTypeImplementation
		 * 
		 * @param impl
		 *            a AbstractNodeTypeImplementation which should be used for
		 *            provisioning
		 */
		private IACandidateWrapper(AbstractNodeTypeImplementation impl) {
			this.nodeImpl = impl;
		}

		/**
		 * Constructor for a RelationshipTypeImplementation
		 * 
		 * @param impl
		 *            a AbstractRelationshipTypeImplementation which should be
		 *            used for provisioning
		 */
		private IACandidateWrapper(AbstractRelationshipTypeImplementation impl) {
			this.relationImpl = impl;
		}

		/**
		 * Adds a mapping from IA to InfrastructureNode with a PrePhaseIAPlugin
		 * 
		 * @param ia
		 *            the IA to deploy
		 * @param nodeTemplate
		 *            the InfrastructureNode to deploy the IA on
		 * @param plugin
		 *            the PrePhaseIAPlugin which can deploy the IA unto the
		 *            InfrastructureNode
		 */
		private void add(AbstractImplementationArtifact ia,
				AbstractNodeTemplate nodeTemplate,
				IPlanBuilderPrePhaseIAPlugin plugin) {
			this.ias.add(ia);
			this.infraNodes.add(nodeTemplate);
			this.plugins.add(plugin);
		}

		/**
		 * Checks whether all IA's can be deployed of Implementation
		 * 
		 * @return true if all IA's can be deployed, else false
		 */
		private boolean isValid() {
			if (this.nodeImpl != null) {
				return this.nodeImpl.getImplementationArtifacts().size() == this.ias
						.size();
			} else {
				return this.relationImpl.getImplementationArtifacts().size() == this.ias
						.size();
			}
		}
	}

	/**
	 * <p>
	 * Filters IA and DA Candidates inside the given ProvisioningChain.
	 * Filtering means if there are IA and DACandidates which don't operate on
	 * the same Template Implementation they are deleted.
	 * </p>
	 * 
	 * @param chain
	 *            a ProvisioningChain to filter
	 */
	private static void filterIncompatibleIADACandidates(ProvisioningChain chain) {
		Map<IACandidateWrapper, DACandidateWrapper> compatibleCandidates = new HashMap<IACandidateWrapper, DACandidateWrapper>();
		for (IACandidateWrapper iaCandidate : chain.iaCandidates) {
			for (DACandidateWrapper daCandidate : chain.daCandidates) {
				if (iaCandidate.nodeImpl.getName().equals(
						daCandidate.impl.getName())) {
					compatibleCandidates.put(iaCandidate, daCandidate);
				}
			}
		}
		chain.daCandidates = new ArrayList<DACandidateWrapper>();
		chain.iaCandidates = new ArrayList<IACandidateWrapper>();

		for (IACandidateWrapper key : compatibleCandidates.keySet()) {
			chain.iaCandidates.add(key);
			chain.daCandidates.add(compatibleCandidates.get(key));
		}
	}

	/**
	 * Creates a ProvisioningChain for the given RelationshipTemplate.
	 * 
	 * @param relationshipTemplate
	 *            an AbstractRelationshipTemplate which should be provisioned
	 * @param forSource
	 *            determines whether provisioning is handle on the
	 *            SourceInterface (set to true) or TargetInterface
	 * @return a ProvisioningChain with complete provisioning Candidates
	 */
	public static ProvisioningChain createProvisioningChain(
			AbstractRelationshipTemplate relationshipTemplate, boolean forSource) {
		// get implementations
		List<AbstractRelationshipTypeImplementation> relationshipTypeImpls = relationshipTemplate
				.getImplementations();

		if (relationshipTypeImpls.isEmpty()) {
			return null;
		}

		// init chain
		ProvisioningChain chain = new ProvisioningChain(relationshipTemplate);

		// calculate infraNodes
		List<AbstractNodeTemplate> infraNodes = new ArrayList<AbstractNodeTemplate>();

		Utils.getInfrastructureNodes(relationshipTemplate, infraNodes,
				forSource);

		// check for IA Plugins
		List<IPlanBuilderPrePhaseIAPlugin> iaPlugins = PluginRegistry
				.getIaPlugins();

		TemplatePlanBuilder.calculateBestImplementationRelationIACandidates(
				relationshipTypeImpls, iaPlugins, infraNodes, chain, forSource);

		// check for prov plugins
		List<IPlanBuilderProvPhaseOperationPlugin> provPlugins = PluginRegistry
				.getProvPlugins();

		TemplatePlanBuilder.calculateProvPlugins(chain, provPlugins);

		TemplatePlanBuilder.filterIADACandidatesRelations(chain);

		TemplatePlanBuilder.reorderProvCandidates(chain);

		return chain;
	}

	/**
	 * Creates a complete ProvisioningChain for the given NodeTemplate
	 * 
	 * @param nodeTemplate
	 *            an AbstractNodeTemplate to create a ProvisioningChain for
	 * @return a complete ProvisioningChain
	 */
	public static ProvisioningChain createProvisioningChain(
			AbstractNodeTemplate nodeTemplate) {
		// get nodetype implementations
		List<AbstractNodeTypeImplementation> nodeTypeImpls = nodeTemplate
				.getImplementations();

		if (nodeTypeImpls.isEmpty()) {
			TemplatePlanBuilder.LOG
					.warn("No implementations available for NodeTemplate {} , can't generate Provisioning logic",
							nodeTemplate.getId());
			return null;
		}

		ProvisioningChain chain = new ProvisioningChain(nodeTemplate);

		// calculate infrastructure nodes
		List<AbstractNodeTemplate> infraNodes = new ArrayList<AbstractNodeTemplate>();
		Utils.getInfrastructureNodes(nodeTemplate, infraNodes);

		// check for IA Plugins
		List<IPlanBuilderPrePhaseIAPlugin> iaPlugins = PluginRegistry
				.getIaPlugins();

		TemplatePlanBuilder.LOG.debug(
				"Calculating best IA candidates for nodeTemplate {} ",
				nodeTemplate.getId());
		// calculate nodeImpl candidates where all IAs of each can be
		// provisioned
		TemplatePlanBuilder.calculateBestImplementationIACandidates(
				nodeTypeImpls, iaPlugins, infraNodes, chain);
		for (IACandidateWrapper wrapper : chain.iaCandidates) {
			int length = wrapper.ias.size();
			for (int i = 0; i < length; i++) {
				AbstractImplementationArtifact ia = wrapper.ias.get(i);
				AbstractNodeTemplate infraNode = wrapper.infraNodes.get(i);
				IPlanBuilderPlugin plugin = wrapper.plugins.get(i);
				TemplatePlanBuilder.LOG
						.debug("Found IA {} for deployment on the InfraNode {} with the Plugin {}",
								ia.getName(), infraNode.getId(), plugin.getID());
			}
		}

		// check for DA Plugins
		List<IPlanBuilderPrePhaseDAPlugin> daPlugins = PluginRegistry
				.getDaPlugins();

		// calculate nodeImpl candidates where all DAs of each can be
		// provisioned
		TemplatePlanBuilder.calculateBestImplementationDACandidates(
				nodeTemplate, nodeTypeImpls, daPlugins, infraNodes, chain);
		for (DACandidateWrapper wrapper : chain.daCandidates) {
			int length = wrapper.das.size();
			for (int i = 0; i < length; i++) {
				AbstractDeploymentArtifact da = wrapper.das.get(i);
				AbstractNodeTemplate infraNode = wrapper.infraNodes.get(i);
				IPlanBuilderPlugin plugin = wrapper.plugins.get(i);
				TemplatePlanBuilder.LOG
						.debug("Found DA {} for deployment on the InfraNode {} with the Plugin {}",
								da.getName(), infraNode.getId(), plugin.getID());
			}
		}

		// filter for nodeTypeImpl Candidates where both DAs and IAs can
		// be provisioned
		TemplatePlanBuilder.filterIncompatibleIADACandidates(chain);

		// check for prov plugins
		List<IPlanBuilderProvPhaseOperationPlugin> provPlugins = PluginRegistry
				.getProvPlugins();

		// search for prov plugins according to the chosen IA provisionings in
		// the chain
		TemplatePlanBuilder.calculateProvPlugins(chain, provPlugins);

		// filter ia and da candidates where the operations can't be executed
		TemplatePlanBuilder.filterIADACandidates(chain);

		// order provisioning candidates
		TemplatePlanBuilder.reorderProvCandidates(chain);

		// TODO consistency plugins

		// select provisioning
		TemplatePlanBuilder.selectProvisioning(chain);

		return chain;
	}

	/**
	 * Reorders the IA/ProvCandidates inside the given ProvisioningChain, so
	 * that a correct order is enforced
	 * 
	 * @param chain
	 *            a ProvisioningChain
	 */
	private static void reorderProvCandidates(ProvisioningChain chain) {
		// ia candidates and da candidates in the chains are already ordered
		// accordingly
		List<ProvCandidateWrapper> reorderedList = new ArrayList<ProvCandidateWrapper>();
		for (IACandidateWrapper iaCandidate : chain.iaCandidates) {
			int iaCandidateSize = iaCandidate.ias.size();
			for (ProvCandidateWrapper provCandidate : chain.provCandidates) {
				int count = 0;
				for (AbstractImplementationArtifact iaCandidateIa : iaCandidate.ias) {
					for (AbstractImplementationArtifact provCandidateIa : provCandidate.ias) {
						if (iaCandidateIa.equals(provCandidateIa)) {
							count++;
						}
					}
				}
				if (count == iaCandidateSize) {
					reorderedList.add(provCandidate);
				}
			}
		}

		chain.provCandidates = reorderedList;

	}

	/**
	 * Filters IA and ProvCandidates which aren't generated from the same
	 * Template Implementation
	 * 
	 * @param chain
	 *            a ProvisioningChain
	 */
	private static void filterIADACandidatesRelations(ProvisioningChain chain) {
		if (chain.provCandidates.size() != chain.iaCandidates.size()) {
			List<IACandidateWrapper> iaCandidatesToRemove = new ArrayList<IACandidateWrapper>();
			Set<ProvCandidateWrapper> provCandidatesWithMatch = new HashSet<ProvCandidateWrapper>();
			for (IACandidateWrapper iaCandidate : chain.iaCandidates) {
				int iaCandidateSize = iaCandidate.ias.size();
				ProvCandidateWrapper match = null;
				for (ProvCandidateWrapper provCandidate : chain.provCandidates) {
					int count = 0;
					for (AbstractImplementationArtifact iaCandidateIa : iaCandidate.ias) {
						for (AbstractImplementationArtifact procCandidateIa : provCandidate.ias) {
							if (iaCandidateIa.equals(procCandidateIa)) {
								count++;
							}
						}
					}
					if (count == iaCandidateSize) {
						match = provCandidate;
					}
				}
				if ((match == null) && !chain.provCandidates.isEmpty()) {
					iaCandidatesToRemove.add(iaCandidate);
				} else {
					if (match != null) {
						provCandidatesWithMatch.add(match);
					}
				}
			}
			if (!iaCandidatesToRemove.isEmpty()) {
				// we need to remove ia and da candidates accordingly, because
				// we didn't found matchin operation candidates for them
				for (IACandidateWrapper iaCandidateToRemove : iaCandidatesToRemove) {
					int index = chain.iaCandidates.indexOf(iaCandidateToRemove);
					chain.iaCandidates.remove(index);
				}
			}

			if (!provCandidatesWithMatch.isEmpty()) {
				// remove all prov candidates which weren't matched to some ia
				// candidate
				chain.provCandidates = new ArrayList<ProvCandidateWrapper>();
				for (ProvCandidateWrapper matchedCandidate : provCandidatesWithMatch) {
					chain.provCandidates.add(matchedCandidate);
				}
			}
		}
	}

	/**
	 * Filters DA/IA Candidates where no OperationCandidates could be found
	 * 
	 * @param chain
	 *            a ProvisioningChain
	 */
	private static void filterIADACandidates(ProvisioningChain chain) {
		if (chain.provCandidates.size() != chain.iaCandidates.size()) {
			// search for ia/da-Candidates where no operation candidate could be
			// found
			List<IACandidateWrapper> iaCandidatesToRemove = new ArrayList<IACandidateWrapper>();
			Set<ProvCandidateWrapper> provCandidatesWithMatch = new HashSet<ProvCandidateWrapper>();
			for (IACandidateWrapper iaCandidate : chain.iaCandidates) {
				int iaCandidateSize = iaCandidate.ias.size();
				ProvCandidateWrapper match = null;
				for (ProvCandidateWrapper provCandidate : chain.provCandidates) {
					int count = 0;
					for (AbstractImplementationArtifact iaCandidateIa : iaCandidate.ias) {
						for (AbstractImplementationArtifact provCandidateIa : provCandidate.ias) {
							if (iaCandidateIa.equals(provCandidateIa)) {
								count++;
							}
						}
					}
					if (count == iaCandidateSize) {
						match = provCandidate;
					}
				}
				if ((match == null) && !chain.provCandidates.isEmpty()) {
					iaCandidatesToRemove.add(iaCandidate);
				} else {
					if (match != null) {
						provCandidatesWithMatch.add(match);
					}
				}
			}

			if (!iaCandidatesToRemove.isEmpty()) {
				// we need to remove ia and da candidates accordingly, because
				// we didn't found matchin operation candidates for them
				for (IACandidateWrapper iaCandidateToRemove : iaCandidatesToRemove) {
					int index = chain.iaCandidates.indexOf(iaCandidateToRemove);
					chain.iaCandidates.remove(index);
					chain.daCandidates.remove(index);
				}
			}

			if (!provCandidatesWithMatch.isEmpty()) {
				// remove all prov candidates which weren't matched to some ia
				// candidate
				chain.provCandidates = new ArrayList<ProvCandidateWrapper>();
				for (ProvCandidateWrapper matchedCandidate : provCandidatesWithMatch) {
					chain.provCandidates.add(matchedCandidate);
				}
			}
		}
	}

	private static void selectProvisioning(ProvisioningChain chain) {
		// TODO just select the first ia candidate, da candidate and prov
		// candidate for now
		// Selection should determine a minimal provisioning. Minimal=
		// min{|IACandidates| + |DACandidates| +|ProvPhaseOperations|}
	}

	/**
	 * Calculates which Provisioning can be used for Provisioining according to
	 * the given IA/DACandidates inside the given ProvisioningChain
	 * 
	 * @param chain
	 *            a ProvisioningChain with set DA/IACandidates
	 * @param provPlugins
	 *            a List of ProvPhaseOperationPlugins
	 */
	private static void calculateProvPlugins(ProvisioningChain chain,
			List<IPlanBuilderProvPhaseOperationPlugin> provPlugins) {
		List<ProvCandidateWrapper> candidates = new ArrayList<ProvCandidateWrapper>();
		for (IACandidateWrapper candidate : chain.iaCandidates) {
			ProvCandidateWrapper provCandidate = new ProvCandidateWrapper();
			for (AbstractImplementationArtifact ia : candidate.ias) {
				for (IPlanBuilderProvPhaseOperationPlugin plugin : provPlugins) {
					if (chain.nodeTemplate != null) {
						if (plugin.canHandle(ia.getArtifactType())
								&& (TemplatePlanBuilder.getOperationForIa(
										chain.nodeTemplate, ia) != null)) {

							provCandidate.add(TemplatePlanBuilder
									.getOperationForIa(chain.nodeTemplate, ia),
									ia, plugin);
						}
					} else {
						if (plugin.canHandle(ia.getArtifactType())
								&& (TemplatePlanBuilder.getOperationForIa(
										chain.relationshipTemplate, ia) != null)) {
							provCandidate.add(TemplatePlanBuilder
									.getOperationForIa(
											chain.relationshipTemplate, ia),
									ia, plugin);
						}
					}
				}
			}
			if (chain.nodeTemplate != null) {
				if (provCandidate.isValid(chain.nodeTemplate)) {
					candidates.add(provCandidate);
				}
			} else {
				if (provCandidate.isValid(chain.relationshipTemplate)) {
					candidates.add(provCandidate);
				}
			}

		}
		chain.provCandidates = candidates;
	}

	/**
	 * Returns the Operation which is implemented by the given IA
	 * 
	 * @param nodeTemplate
	 *            an AbstractNodeTemplate
	 * @param ia
	 *            an AbstractImplementationArtifact
	 * @return AbstractOperation of the NodeTemplate if the given IA implements
	 *         it, else null
	 */
	private static AbstractOperation getOperationForIa(
			AbstractNodeTemplate nodeTemplate, AbstractImplementationArtifact ia) {
		for (AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
			for (AbstractOperation op : iface.getOperations()) {
				if (op.getName().equals(ia.getOperationName())) {
					return op;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the Operation which is implemented by the given IA
	 * 
	 * @param relationshipTemplate
	 *            an AbstractRelationshipTemplate
	 * @param ia
	 *            an AbstractImplementationArtifact
	 * @return AbstractOperation of the RelationshipTemplate if the given IA
	 *         implements it, else null
	 */
	private static AbstractOperation getOperationForIa(
			AbstractRelationshipTemplate relationshipTemplate,
			AbstractImplementationArtifact ia) {
		for (AbstractInterface iface : relationshipTemplate
				.getRelationshipType().getSourceInterfaces()) {
			for (AbstractOperation op : iface.getOperations()) {
				if (op.getName().equals(ia.getOperationName())) {
					return op;
				}
			}
		}

		for (AbstractInterface iface : relationshipTemplate
				.getRelationshipType().getTargetInterfaces()) {
			for (AbstractOperation op : iface.getOperations()) {
				if (op.getName().equals(ia.getOperationName())) {
					return op;
				}
			}
		}
		return null;
	}

	/**
	 * Calculates correct mappings of the given NodeTypeImplementations,
	 * PrePhaseDAPlugins and InfrastructureNodes for the given ProvisioningChain
	 * 
	 * @param impls
	 *            a List of NodeTypeImplementations
	 * @param plugins
	 *            a List of PrePhaseDAPlugins
	 * @param infraNodes
	 *            a List of InfrastructureNode of the NodeTemplate the
	 *            NodeTypeImplementations belong to
	 * @param chain
	 *            a ProvisioningChain where the candidates are added to
	 */
	private static void calculateBestImplementationDACandidates(
			AbstractNodeTemplate nodeTemplate,
			List<AbstractNodeTypeImplementation> impls,
			List<IPlanBuilderPrePhaseDAPlugin> plugins,
			List<AbstractNodeTemplate> infraNodes, ProvisioningChain chain) {
		List<DACandidateWrapper> candidates = new ArrayList<DACandidateWrapper>();

		for (AbstractNodeTypeImplementation impl : impls) {
			TemplatePlanBuilder.LOG.debug(
					"Checking DAs of NodeTypeImpl {} and NodeTemplate {}",
					impl.getName(), nodeTemplate.getId());
			DACandidateWrapper candidate = new DACandidateWrapper(nodeTemplate,
					impl);

			List<AbstractDeploymentArtifact> effectiveDAs = TemplatePlanBuilder
					.calculateEffectiveDAs(nodeTemplate, impl);

			for (AbstractDeploymentArtifact da : effectiveDAs) {
				TemplatePlanBuilder.LOG.debug(
						"Checking whether DA {} can be deployed", da.getName());
				for (AbstractNodeTemplate infraNode : infraNodes) {
					TemplatePlanBuilder.LOG
							.debug("Checking if DA {} can be deployed on InfraNode {}",
									da.getName(), infraNode.getId());
					for (IPlanBuilderPrePhaseDAPlugin plugin : plugins) {
						TemplatePlanBuilder.LOG.debug(
								"Checking with Plugin {}", plugin.getID());
						if (plugin.canHandle(da, infraNode.getType())) {
							TemplatePlanBuilder.LOG
									.debug("Adding Plugin, can handle DA on InfraNode");
							candidate.add(da, infraNode, plugin);
						}
					}
				}
			}
			if (candidate.isValid()) {
				TemplatePlanBuilder.LOG
						.debug("Generated Candidate was valid, adding to all Candidates");
				candidates.add(candidate);
			} else {
				TemplatePlanBuilder.LOG
						.debug("Generated Candidate was invalid, don't add to all Candidates");
			}
		}
		chain.daCandidates = candidates;
	}

	/**
	 * Searches for NodeTypeImplementations where all IA's can be provisioned by
	 * some plugin in the system.
	 * 
	 * @param impls
	 *            all implementations of single nodetype
	 * @param plugins
	 *            all plugins possibly capable of working with the ia's
	 *            contained in a nodetypeImplementation
	 * @param infraNodes
	 *            all infrastructure nodes of the nodetemplate the
	 *            nodetypeimplementations originate from
	 * @return a list of Wrapper class Object which contain information of which
	 *         ia is provisioned on which infrastructure by which plugin
	 */
	private static void calculateBestImplementationIACandidates(
			List<AbstractNodeTypeImplementation> impls,
			List<IPlanBuilderPrePhaseIAPlugin> plugins,
			List<AbstractNodeTemplate> infraNodes, ProvisioningChain chain) {

		List<IACandidateWrapper> candidates = new ArrayList<IACandidateWrapper>();
		// cycle through all implementations
		for (AbstractNodeTypeImplementation impl : impls) {
			IACandidateWrapper candidate = new IACandidateWrapper(impl);
			// match the ias of the implementation with the infrastructure nodes
			for (AbstractImplementationArtifact ia : impl
					.getImplementationArtifacts()) {
				TemplatePlanBuilder.LOG
						.debug("Checking whether IA {} can be deployed on a specific Infrastructure Node",
								ia.getName());
				for (AbstractNodeTemplate infraNode : infraNodes) {
					// check if any plugin can handle installing the ia on the
					// infraNode
					for (IPlanBuilderPrePhaseIAPlugin plugin : plugins) {
						if (plugin.canHandle(ia, infraNode.getType())) {
							candidate.add(ia, infraNode, plugin);
						}
					}
				}
			}
			// check if all ias of the implementation can be provisioned
			if (candidate.isValid()) {
				candidates.add(candidate);
				TemplatePlanBuilder.LOG
						.debug("IA Candidate is valid, adding to candidate list");
			} else {
				TemplatePlanBuilder.LOG
						.debug("IA Candidate is invalid, discarding candidate");
			}
		}
		chain.iaCandidates = candidates;
	}

	/**
	 * Checks whether the IA implements a SourceInterfaceOperation
	 * 
	 * @param ia
	 *            the IA to check with
	 * @param relationshipTemplate
	 *            the RelationshipTemplate to check with
	 * @return true if the IA implements a Operation inside a SourceInterface of
	 *         the RelationshipTemplate
	 */
	private static boolean checkIfIaImplementsSrcIface(
			AbstractImplementationArtifact ia,
			AbstractRelationshipTemplate relationshipTemplate) {

		for (AbstractInterface iface : relationshipTemplate
				.getRelationshipType().getSourceInterfaces()) {
			if (iface.getName().equals(ia.getInterfaceName())) {
				for (AbstractOperation op : iface.getOperations()) {
					if (op.getName().equals(ia.getOperationName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Calculates correct mappings for the given RelationshipTypeImplementations
	 * with the given Plugins and InfraNodes
	 * 
	 * @param impls
	 *            a List of RelationshipTypeImplementation
	 * @param plugins
	 *            a List of PrePhaseIAPlugins
	 * @param infraNodes
	 *            a List of InfrastructureNodes which belong to the
	 *            RelationshipTemplate the given Implementation belong to
	 * @param chain
	 *            a ProvisioningChain to save the results
	 * @param forSource
	 *            whether the calculation is done for the SourceInterface or for
	 *            the TargetInterface
	 */
	private static void calculateBestImplementationRelationIACandidates(
			List<AbstractRelationshipTypeImplementation> impls,
			List<IPlanBuilderPrePhaseIAPlugin> plugins,
			List<AbstractNodeTemplate> infraNodes, ProvisioningChain chain,
			boolean forSource) {
		List<IACandidateWrapper> candidates = new ArrayList<IACandidateWrapper>();
		for (AbstractRelationshipTypeImplementation impl : impls) {
			IACandidateWrapper candidate = new IACandidateWrapper(impl);
			for (AbstractImplementationArtifact ia : impl
					.getImplementationArtifacts()) {
				if (forSource) {
					// check if ia implements source interfaces
					if (!TemplatePlanBuilder.checkIfIaImplementsSrcIface(ia,
							chain.relationshipTemplate)) {
						continue;
					}
				} else {
					if (TemplatePlanBuilder.checkIfIaImplementsSrcIface(ia,
							chain.relationshipTemplate)) {
						continue;
					}
				}

				for (AbstractNodeTemplate infraNode : infraNodes) {
					for (IPlanBuilderPrePhaseIAPlugin plugin : plugins) {
						if (plugin.canHandle(ia, infraNode.getType())) {
							candidate.add(ia, infraNode, plugin);
						}
					}
				}
			}
			if (candidate.isValid()) {
				candidates.add(candidate);
			}
		}
		chain.iaCandidates = candidates;

	}

	/**
	 * Calculates a list of DA's containing an effective set of DA combining the
	 * DA's from the given NodeImplementation and NodeTemplates according to the
	 * TOSCA specification.
	 * 
	 * @param nodeTemplate
	 *            the NodeTemplate the NodeImplementations belongs to
	 * @param nodeImpl
	 *            a NodeTypeImplementation for the given NodeTemplate
	 * @return a possibly empty list of AbstractDeploymentArtifacts
	 */
	private static List<AbstractDeploymentArtifact> calculateEffectiveDAs(
			AbstractNodeTemplate nodeTemplate,
			AbstractNodeTypeImplementation nodeImpl) {
		List<AbstractDeploymentArtifact> effectiveDAs = new ArrayList<AbstractDeploymentArtifact>();

		List<AbstractDeploymentArtifact> nodeImplDAs = nodeImpl
				.getDeploymentArtifacts();
		List<AbstractDeploymentArtifact> nodeTemplateDAs = nodeTemplate
				.getDeploymentArtifacts();

		for (AbstractDeploymentArtifact templateDa : nodeTemplateDAs) {
			boolean overridesDA = false;
			int daIndex = -1;
			for (int i = 0; i < nodeImplDAs.size(); i++) {
				AbstractDeploymentArtifact nodeImplDa = nodeImplDAs.get(i);

				if (nodeImplDa.getName().equals(templateDa.getName())
						& nodeImplDa.getArtifactType().equals(
								nodeImplDa.getArtifactType())) {
					overridesDA = true;
					daIndex = i;
				}
			}

			if (overridesDA) {
				nodeImplDAs.remove(daIndex);
			}
		}

		effectiveDAs.addAll(nodeTemplateDAs);
		effectiveDAs.addAll(nodeImplDAs);

		return effectiveDAs;
	}
}
