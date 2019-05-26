package org.opentosca.placement;

public class PlacementMatch {

	private CapablePlacementNode cpbNode;
	private ToBePlacedNode tbpNode;
	private String matchID;

	public PlacementMatch(final CapablePlacementNode cpbNode, final ToBePlacedNode tbpNode) {
		this.setCpbNode(cpbNode);
		this.setTbpNode(tbpNode);
		this.setMatchID(tbpNode.getToBePlacedNode() + "~placeOn~" + cpbNode.getOsNode() + "~"
				+ cpbNode.getInstanceIDOfServiceTemplateOfOsNode());
	}

	/**
	 * @return the cpbNode
	 */
	public CapablePlacementNode getCpbNode() {
		return cpbNode;
	}

	/**
	 * @param cpbNode the cpbNode to set
	 */
	public void setCpbNode(CapablePlacementNode cpbNode) {
		this.cpbNode = cpbNode;
	}

	/**
	 * @return the tbpNode
	 */
	public ToBePlacedNode getTbpNode() {
		return tbpNode;
	}

	/**
	 * @param tbpNode the tbpNode to set
	 */
	public void setTbpNode(ToBePlacedNode tbpNode) {
		this.tbpNode = tbpNode;
	}

	/**
	 * @return the matchID
	 */
	public String getMatchID() {
		return matchID;
	}

	/**
	 * @param matchID the matchID to set
	 */
	public void setMatchID(String matchID) {
		this.matchID = matchID;
	}

}
