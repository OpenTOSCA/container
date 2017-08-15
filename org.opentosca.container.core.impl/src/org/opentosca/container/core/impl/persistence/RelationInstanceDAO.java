package org.opentosca.container.core.impl.persistence;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.instance.RelationInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class RelationInstanceDAO {

  private static Logger logger = LoggerFactory.getLogger(RelationInstanceDAO.class);


  public void deleteRelationInstance(final RelationInstance si) {
    logger.info("Not Implemented: Relation instance cannot be changed");
  }

  public void saveRelationInstance(final RelationInstance relationInstance) {
    logger.info("Not Implemented: Relation instance cannot be changed");
  }

  public void setProperties(final RelationInstance relationInstance, final Document properties) {
    logger.info("Not Implemented: Relation instance cannot be changed");
  }

  public void setState(final RelationInstance relationInstance, final String state) {
    logger.info("Not Implemented: Relation instance cannot be changed");
  }

  public List<RelationInstance> getRelationInstances(final URI serviceInstanceID,
      final QName relationshipTemplateID, final String relationshipTemplateName,
      final URI relationInstanceID) {
    logger.info("Not Implemented: Relation instances cannot be queried");
    return new ArrayList<>();
  }
}
