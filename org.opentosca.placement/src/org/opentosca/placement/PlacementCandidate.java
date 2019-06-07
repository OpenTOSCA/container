package org.opentosca.placement;

import java.util.List;
import java.util.Map;

public class PlacementCandidate {

	private List<PlacementMatch> placementMatches;
	private List<PlacementMatch> alternativeMatches;
	
	public PlacementCandidate(final List<PlacementMatch> placementMatches, final List<PlacementMatch> alternativeMatches) {
		this.placementMatches = placementMatches;
		this.alternativeMatches = alternativeMatches;
	}

	/**
	 * @return the placementMatches
	 */
	public List<PlacementMatch> getPlacementMatches() {
		return placementMatches;
	}

	/**
	 * @param placementMatches the placementMatches to set
	 */
	public void setPlacementMatches(List<PlacementMatch> placementMatches) {
		this.placementMatches = placementMatches;
	}

	/**
	 * @return the alternativeMatches
	 */
	public List<PlacementMatch> getAlternativeMatches() {
		return alternativeMatches;
	}

	/**
	 * @param alternativeMatches the alternativeMatches to set
	 */
	public void setAlternativeMatches(List<PlacementMatch> alternativeMatches) {
		this.alternativeMatches = alternativeMatches;
	}
}
