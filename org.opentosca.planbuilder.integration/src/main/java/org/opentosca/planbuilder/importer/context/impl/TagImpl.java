package org.opentosca.planbuilder.importer.context.impl;

import org.oasis_open.docs.tosca.ns._2011._12.TTag;
import org.opentosca.planbuilder.model.tosca.AbstractTag;

/**
 * <p>
 * This class implements a TOSCA Tag, in particular an AbstractTag
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class TagImpl extends AbstractTag {

  private final TTag tag;


  /**
   * Constructor
   *
   * @param tag a JAXB TTag
   */
  public TagImpl(final TTag tag) {
    this.tag = tag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.tag.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getValue() {
    return this.tag.getValue();
  }

}
