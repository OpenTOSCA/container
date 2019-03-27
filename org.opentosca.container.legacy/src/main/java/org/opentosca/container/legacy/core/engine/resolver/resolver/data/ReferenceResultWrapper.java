package org.opentosca.container.legacy.core.engine.resolver.resolver.data;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * This DTO stores data of found DOM Documents and nested Nodes which are searched by
 * org.opentosca.toscaengine.service.impl.resolver.ReferenceMapper.
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 */
public class ReferenceResultWrapper {

  private Document doc = null;
  private NodeList nodeList = null;


  /**
   * @return the doc
   */
  public Document getDoc() {
    return this.doc;
  }

  /**
   * @param doc the doc to set
   */
  public void setDoc(final Document doc) {
    this.doc = doc;
  }

  /**
   * @return the nodeList
   */
  public NodeList getNodeList() {
    return this.nodeList;
  }

  /**
   * @param nodeList the nodeList to set
   */
  public void setNodeList(final NodeList nodeList) {
    this.nodeList = nodeList;
  }

}
