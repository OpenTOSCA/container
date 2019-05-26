package org.opentosca.placement;

import java.awt.RenderingHints.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlacementService {

	private static Logger logger = LoggerFactory.getLogger(PlacementService.class);

	/**
	 * This
	 * 
	 * @param cpbNodes All NodeTemplates with instances that have some Capabilities
	 *                 and are Operating System Nodes
	 * @param tbpNodes All NodeTemplates with some Requirements
	 * @return placementCandidate
	 */
	public List<PlacementMatch> findPlacementCandidate(final List<CapablePlacementNode> cpbNodes,
			final List<ToBePlacedNode> tbpNodes) {

		List<List<String>> capsOfCpbNodes = new ArrayList<List<String>>();
		List<List<String>> reqsOfTbpNodes = new ArrayList<List<String>>();
		List<PlacementMatch> results = new ArrayList<PlacementMatch>();
		
		/**
		 *  This map contains all nodes with a certain capability in the form of:
		 *  { key: value } ==> { capName: List<CapablePlacementNode> }
		 */
		Map<String, List<CapablePlacementNode>> capNamesToCapableNodes = new HashMap<String, List<CapablePlacementNode>>();

		/**
		 * Find pairs of matching Reqs and Caps. They have to follow the naming scheme
		 * of Req{NameOfReqCap} & Cap{NameOfReqCap} for the matching to work. Complexity
		 * is O(n²). TODO: Normally namespaces of the ReqCaps should be compared too
		 * before comparing names, but this is just a prototype for now.
		 */
		cpbNodes.forEach(cpbNode -> {

			cpbNode.getCapsOfOSNode().forEach(cap -> {
				List<String> capStrings = splitPrefixFromReqOrCap(cap.getLocalPart());
				String capName = capStrings.get(1);
				
				// if the key already exists in the map then add to list, else create it and then add to it
				if (capNamesToCapableNodes.containsKey(capName)) {
					capNamesToCapableNodes.get(capName).add(cpbNode);
				} else {
					List<CapablePlacementNode> list = new ArrayList<CapablePlacementNode>();
					list.add(cpbNode);
					capNamesToCapableNodes.put(capName, list);
				}
				
				// if prefix follows the convention of naming
				if(capStrings.get(0).equals("Cap")) {
					tbpNodes.forEach(tbpNode -> {
						tbpNode.getReqsOfToBePlacedNode().forEach(req -> {
							List<String> reqStrings = splitPrefixFromReqOrCap(req.getLocalPart());
							// the actual match happens here if this condition is met
							if (capStrings.get(1).equals(reqStrings.get(1))) {
								logger.info("node with cap: " + cpbNode.getOsNode());
								logger.info("node with req: " + tbpNode.getToBePlacedNode());
								PlacementMatch match = new PlacementMatch(cpbNode, tbpNode);
								// add to list of all matches
								results.add(match);
							}
						});
					});
				};
			});
			
			capNamesToCapableNodes.forEach((key, value) -> {
				logger.info("#############################################");
				logger.info("map entry: ");
				logger.info("key: " + key);
				value.forEach(cpbNodebla -> {
					logger.info("~~~~");
					logger.info("cpbNodeName: " + cpbNodebla.getOsNode());
					logger.info("cpbNodeInstanceId: " + cpbNodebla.getInstanceIDOfOSNode());
				});
				logger.info("~~~~");
			});
			logger.info("#############################################");
			
		});

		logger.info("Potential Placement Matches: ");
		results.forEach(result -> {
			logger.info(result.getMatchID());
		});

		return results;
	}

	/**
	 * Splits the prefix from the name of a Req or Cap
	 * 
	 * @param reqOrCap
	 * @return strings first element is the prefix ("Req" or "Cap"), second element
	 *         is the name of the ReqOrCap
	 */
	public List<String> splitPrefixFromReqOrCap(String reqOrCap) {
		List<String> strings = new ArrayList<String>();
		strings.add(reqOrCap.substring(0, 3));
		strings.add(reqOrCap.substring(3, reqOrCap.length()));

		return strings;
	}
}
