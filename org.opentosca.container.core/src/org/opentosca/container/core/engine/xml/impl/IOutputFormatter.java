package org.opentosca.container.core.engine.xml.impl;

import org.w3c.dom.Node;

/**
 * Interface of the OutputFormatter.
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 */
public interface IOutputFormatter {

	/**
	 * Serializes a passed DOM node to a String.
	 *
	 * @param node A DOM Node which is needed in a String representation.
	 * @param removeWhitespaces Flag for removing whitespace.
	 * @return String representation of the passed DOM Node.
	 */
	public abstract String docToString(Node node, boolean removeWhitespaces);

}