package org.opentosca.container.core.impl.persistence;

import java.util.stream.Collectors;

import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.model.instance.NodeInstance;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.model.instance.State;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceProperty;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.next.utils.Enums;
import org.w3c.dom.Document;

public abstract class Converters {

  private static ServiceTemplateInstanceRepository stiRepository =
      new ServiceTemplateInstanceRepository();
  private static NodeTemplateInstanceRepository ntiRepository =
      new NodeTemplateInstanceRepository();

  private static DocumentConverter converter = new DocumentConverter();

  public static ServiceInstance convert(final ServiceTemplateInstance object) {
    ServiceInstance si = new ServiceInstance(object.getCsarId(), object.getTemplateId(), "");
    if (object.getId() != null)
      si.setId(object.getId().intValue());
    si.setState(Enums.valueOf(State.ServiceTemplate.class, object.getState().toString()));
    return si;
  }

  public static ServiceTemplateInstance convert(final ServiceInstance object) {
    ServiceTemplateInstance sti = new ServiceTemplateInstance();
    try {
      sti = stiRepository.find(DaoUtil.toLong(object.getDBId()));
    } catch (Exception e) {
      // New object (?)
      sti.setCsarId(object.getCSAR_ID());
      sti.setTemplateId(object.getServiceTemplateID());
      sti.setState(ServiceTemplateInstanceState.CREATED);
    }
    return sti;
  }

  public static NodeInstance convert(final NodeTemplateInstance object) {
    NodeInstance ni = new NodeInstance(object.getTemplateId(), "", object.getNodeType(),
        convert(object.getServiceTemplateInstance()));
    ni.setState(Enums.valueOf(State.Node.class, object.getState().toString()));
    NodeTemplateInstanceProperty prop =
        object.getProperties().stream().filter(p -> p.getName().equalsIgnoreCase("xml"))
            .collect(Collectors.reducing((a, b) -> null)).get();
    if (prop != null) {
      ni.setProperties((Document) converter.convertDataValueToObjectValue(prop.getValue(), null));
    }
    return ni;
  }

  public static NodeTemplateInstance convert(final NodeInstance object) {
    NodeTemplateInstance nti = new NodeTemplateInstance();
    try {
      nti = ntiRepository.find(DaoUtil.toLong(object.getId()));
    } catch (Exception e) {
      // New object (?)
      nti.setNodeType(object.getNodeType());
      nti.setTemplateId(object.getNodeTemplateID());
      nti.setState(NodeTemplateInstanceState.CREATED);
      if (object.getProperties() != null) {
        NodeTemplateInstanceProperty prop = new NodeTemplateInstanceProperty();
        prop.setName("xml");
        prop.setType("xml");
        prop.setValue(
            (String) converter.convertObjectValueToDataValue(object.getProperties(), null));
        nti.addProperty(prop);
      }
      if (object.getServiceInstance() != null) {
        try {
          Long id = DaoUtil.toLong(object.getServiceInstance().getDBId());
          nti.setServiceTemplateInstance(stiRepository.find(id));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
    return nti;
  }
}
