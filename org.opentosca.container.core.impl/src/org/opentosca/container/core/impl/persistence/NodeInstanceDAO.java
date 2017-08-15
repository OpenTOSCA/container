package org.opentosca.container.core.impl.persistence;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.model.instance.NodeInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceProperty;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.opentosca.container.core.next.utils.Enums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Data Access Object for NodeInstances
 */
public class NodeInstanceDAO {

  private static Logger logger = LoggerFactory.getLogger(NodeInstanceDAO.class);

  NodeTemplateInstanceRepository repository = new NodeTemplateInstanceRepository();


  public void deleteNodeInstance(final NodeInstance si) {
    try {
      logger.info("NodeInstance: {}", si.toString());
      Optional<NodeTemplateInstance> o = repository.find(DaoUtil.toLong(si.getId()));
      if (o.isPresent()) {
        NodeTemplateInstance nti = o.get();
        nti.setState(NodeTemplateInstanceState.DELETED);
        repository.update(nti);
        repository.remove(nti);
        logger.debug("Deleted NodeInstance with ID: " + si.getId());
      } else {
        logger.info("NOT FOUND");
      }
    } catch (Exception e) {
      logger.error("Could not delete node instance: {}", e.getMessage(), e);
      e.printStackTrace();
    }
  }

  public NodeInstance saveNodeInstance(final NodeInstance nodeInstance) {
    try {
      logger.info("NodeInstance: {}", nodeInstance.toString());
      NodeTemplateInstance nti = Converters.convert(nodeInstance);
      try {
        repository.add(nti);
      } catch (Exception ex) {
        logger.info("Object already added, trying to update");
        repository.update(nti);
      }
      return Converters.convert(nti);
    } catch (Exception e) {
      logger.error("Could not save node instance: {}", e.getMessage(), e);
      e.printStackTrace();
    }
    return nodeInstance;
  }

  /**
   * this method wraps the setting/saving of the properties
   *
   * @param nodeInstance
   * @param properties
   */
  public void setProperties(final NodeInstance nodeInstance, final Document properties) {
    try {
      logger.info("NodeInstance: {}", nodeInstance.toString());
      DocumentConverter converter = new DocumentConverter();
      Optional<NodeTemplateInstance> o = repository.find(DaoUtil.toLong(nodeInstance.getId()));
      if (o.isPresent()) {
        NodeTemplateInstance nti = o.get();
        if (properties != null) {
          String value = (String) converter.convertObjectValueToDataValue(properties, null);
          logger.info("XML: {}", value);
          NodeTemplateInstanceProperty prop = new NodeTemplateInstanceProperty();
          prop.setName("xml");
          prop.setType("xml");
          prop.setValue(value);
          nti.addProperty(prop);
        }
        repository.update(nti);
      } else {
        logger.info("NOT FOUND");
      }
    } catch (Exception e) {
      logger.error("Could not update node instance: {}", e.getMessage(), e);
      e.printStackTrace();
    }
  }

  /**
   * this method wraps the setting/saving of the state
   *
   * @param nodeInstance
   * @param state to be set
   */
  public void setState(final NodeInstance nodeInstance, final String state) {
    try {
      logger.info("NodeInstance: {}", nodeInstance.toString());
      Optional<NodeTemplateInstance> o = repository.find(DaoUtil.toLong(nodeInstance.getId()));
      if (o.isPresent()) {
        NodeTemplateInstance nti = o.get();
        nti.setState(
            Enums.valueOf(NodeTemplateInstanceState.class, state, NodeTemplateInstanceState.ERROR));
        repository.update(nti);
      } else {
        logger.info("NOT FOUND");
      }
    } catch (Exception e) {
      logger.error("Could not update node instance: {}", e.getMessage(), e);
      e.printStackTrace();
    }
  }

  public List<NodeInstance> getNodeInstances(final URI serviceInstanceID,
      final QName nodeTemplateID, final String nodeTemplateName, final URI nodeInstanceID) {

    logger.info("Not Implemented: Node instances cannot be queried");
    return new ArrayList<>();

    // final Query getNodeInstancesQuery = this.em.createNamedQuery(NodeInstance.getNodeInstances);
    //
    // Integer internalID = null;
    // if (nodeInstanceID != null) {
    // internalID = IdConverter.nodeInstanceUriToID(nodeInstanceID);
    // }
    //
    // Integer internalServiceInstanceID = null;
    // if (serviceInstanceID != null) {
    // // The serviceInstanceID in this case has the following format:
    // //
    // http://{hostname}:1337/containerapi/CSARs/{csar}/ServiceTemplates/{template}/Instances/{id}
    // // We gonna split the string on character "/" in order to extract
    // // the instance ID out of it, which is stored at the end of the
    // // resulting string array.
    // final String[] parts = serviceInstanceID.getPath().split("/");
    // internalServiceInstanceID = Integer.valueOf(parts[parts.length - 1]);
    //
    // // This won't work since IdConverter expects a different URL
    // // pattern (/instancedata/serviceInstances), which isn't given in
    // // this case.
    // // internalServiceInstanceID =
    // // IdConverter.serviceInstanceUriToID(serviceInstanceID);
    // }
    //
    // // Set Parameters for the Query
    // getNodeInstancesQuery.setParameter("internalID", internalID);
    // getNodeInstancesQuery.setParameter("nodeTemplateID",
    // ((nodeTemplateID != null) ? nodeTemplateID.toString() : null));
    // getNodeInstancesQuery.setParameter("nodeTemplateName", nodeTemplateName);
    // getNodeInstancesQuery.setParameter("internalServiceInstanceID", internalServiceInstanceID);
    // @SuppressWarnings("unchecked")
    // final List<NodeInstance> queryResults = getNodeInstancesQuery.getResultList();
    //
    // return queryResults;
  }
}
