package org.opentosca.portability.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
public class ArtifactReferences {
	
	public ArtifactReferences(List<String> references) {
		this.allReferences = references;
	}
	
	@XmlElement(name = "ref")
	public List<String> allReferences;
	
}