package org.opentosca.placement;

import java.awt.RenderingHints.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlacementService {

	private static Logger logger = LoggerFactory.getLogger(PlacementService.class);
	private Integer residue = Integer.MAX_VALUE;
	private CapablePlacementNode chosenHost;
	private CapablePlacementNode alternativeHost;

	/**
	 * This
	 * 
	 * @param cpbNodes All NodeTemplates with instances that have some Capabilities
	 *                 and are Operating System Nodes
	 * @param tbpNodes All NodeTemplates with some Requirements
	 * @return placementCandidate
	 */
	public PlacementCandidate findPlacementCandidate(final List<CapablePlacementNode> cpbNodes,
			final List<ToBePlacedNode> tbpNodes) {

		List<PlacementMatch> results = new ArrayList<PlacementMatch>();
		List<PlacementMatch> alternativeMatches = new ArrayList<PlacementMatch>();

		/**
		 * This map contains all nodes with a certain capability in the form of: { key:
		 * value } ==> { capName: List<CapablePlacementNode> }
		 */
		Map<String, List<CapablePlacementNode>> capNamesToCapableNodes = new HashMap<String, List<CapablePlacementNode>>();
		
		
		cpbNodes.forEach(cpbNode -> {
			cpbNode.getCapsOfOSNode().forEach(cap -> {
				
				List<String> capStrings = splitPrefixFromReqOrCap(removeNumbersFromString(cap.getLocalPart()));
				String capName = capStrings.get(1);
				// if the key already exists in the map then add to list, else create it and
				// then add to it 
				if (capNamesToCapableNodes.containsKey(capName)) {
					capNamesToCapableNodes.get(capName).add(cpbNode);
				} else {
					List<CapablePlacementNode> list = new ArrayList<CapablePlacementNode>();
					list.add(cpbNode);
					capNamesToCapableNodes.put(capName, list);
				}
			});
		});

		capNamesToCapableNodes.forEach((cap, nodesWithThisCap) -> {
			tbpNodes.forEach(tbpNode -> {
				logger.info("Looking at tbpNode: " + tbpNode.getToBePlacedNode());
				tbpNode.getReqsOfToBePlacedNode().forEach(req -> {

					// Get the important part of the Requirement name for matching with Capabilities
					List<String> reqStrings = splitPrefixFromReqOrCap(removeNumbersFromString(req.getLocalPart()));
					String reqName = reqStrings.get(1);
					logger.info("reqName: " + reqName);

					
					nodesWithThisCap.forEach(cpbNode -> {


						logger.info("capName: " + cap);
						// only look at cases where capabilities and requirements match
						if (reqName.equals(cap)) {
							logger.info("reqName equals capName...");
							// calculate residue of properties
							// find out intersection of properties
							Set<String> intersect = new HashSet<String>(tbpNode.getPropertyMap().keySet());
							intersect.retainAll(cpbNode.getPropertyMap().keySet());
							// if there are common properties
							if (intersect != null) {
								logger.info("intersect is not null...");
								intersect.iterator().forEachRemaining(propertyKey -> {
									Integer amountReq = new Integer(tbpNode.getPropertyMap().get(propertyKey));
									Integer amountCap = new Integer(cpbNode.getPropertyMap().get(propertyKey));

									logger.info("amountReq " + amountReq);
									logger.info("amountCap " + amountCap);
									
									/*
									 * if (amountCap >= amountReq && residue > (amountCap - amountReq)) {
									 * logger.info("if ist erfüllt");
									 * residue = amountCap - amountReq;
									 * logger.info("residue: " + residue);
									 * chosenHost = cpbNode;
									 * 
									 * // overwrite remaining amount with residue
									 * cpbNode.getPropertyMap().put(propertyKey, residue.toString()); } else {
									 * 
									 * }
									 */
									// enough resources available
									if (amountCap >= amountReq) {
										// first iteration
										if (chosenHost == null) {
											logger.info("chosen host was null...");
											residue = amountCap - amountReq;
											chosenHost = cpbNode;
											
										} else if (chosenHost != null) {
											logger.info("chosen host was not null...");
											Integer myResidue = amountCap - amountReq;
											if (myResidue < residue) {
												logger.info("myResidue: " + myResidue + "is smaller than residue: " + residue);
												residue = myResidue;
												// add as alternative host since it got beaten by the latest cpbNode
												alternativeHost = chosenHost;
												chosenHost = cpbNode;
											}
										}
										cpbNode.getPropertyMap().put(propertyKey, residue.toString());
									}
								});
							}
							
						}
					});
					// only add if it was found
					if (chosenHost != null) {
						logger.info("chosenHost: " + chosenHost.getOsNode() + " for: " + tbpNode.getToBePlacedNode());
						PlacementMatch match = new PlacementMatch(chosenHost, tbpNode);
						results.add(match);
						// reset fields
						chosenHost = null;
						residue = Integer.MAX_VALUE;
					}
					
					if (alternativeHost != null) {
						PlacementMatch alternativeMatch = new PlacementMatch(alternativeHost, tbpNode);
						alternativeMatches.add(alternativeMatch);
						alternativeHost = null;
					}
				});
			});
		});

		logger.info("Potential Placement Matches: ");
		results.forEach(result -> {
			logger.info(result.getMatchID());
		});

		return new PlacementCandidate(results, alternativeMatches);
	}

	/**
	 * Splits the prefix from the name of a Req or Cap
	 * 
	 * @param reqOrCap
	 * @return strings first element is the prefix ("Req" or "Cap"), second element
	 *         is the name of the ReqOrCap
	 */
	public List<String> splitPrefixFromReqOrCap(String reqOrCap) {
		String name = removeNumbersFromString(reqOrCap.substring(3, reqOrCap.length()));
		List<String> strings = new ArrayList<String>();
		strings.add(reqOrCap.substring(0, 3));
		strings.add(name);

		return strings;
	}

	public String removeNumbersFromString(String string) {
		return string.replaceAll("[0-9]", "");
	}
}
