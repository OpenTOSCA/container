package org.opentosca.container.core.impl.persistence;

import java.util.Optional;
import java.util.stream.Collectors;

import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.model.instance.NodeInstance;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.model.instance.State;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceProperty;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceProperty;
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
    if (object.getId() != null) {
      si.setId(object.getId().intValue());
    }
    si.setState(Enums.valueOf(State.ServiceTemplate.class, object.getState().toString()));
    si.setIDs();
    si.setCreated(object.getCreatedAt());
    return si;
  }

  public static ServiceTemplateInstance convert(final ServiceInstance object) {
    ServiceTemplateInstance sti = null;
    try {
      Optional<ServiceTemplateInstance> o = stiRepository.find(DaoUtil.toLong(object.getDBId()));
      if (o.isPresent()) {
        sti = o.get();
      } else {
        sti = new ServiceTemplateInstance();
        sti.setCsarId(object.getCSAR_ID());
        sti.setTemplateId(object.getServiceTemplateID());
        sti.setState(ServiceTemplateInstanceState.INITIAL);
      }
      Document properties = object.getProperties();
      if (properties != null) {
        String value = (String) converter.convertObjectValueToDataValue(properties, null);
        ServiceTemplateInstanceProperty prop = new ServiceTemplateInstanceProperty();
        prop.setName("xml");
        prop.setType("xml");
        prop.setValue(value);
        sti.addProperty(prop);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return sti;
  }

  public static NodeInstance convert(final NodeTemplateInstance object) {
    NodeInstance ni = new NodeInstance(object.getTemplateId(), "", object.getNodeType(),
        convert(object.getServiceTemplateInstance()));
    if (object.getId() != null) {
      ni.setId(object.getId().intValue());
    }
    ni.setNodeInstanceID();
    ni.setState(Enums.valueOf(State.Node.class, object.getState().toString()));
    ni.setCreated(object.getCreatedAt());
    NodeTemplateInstanceProperty prop =
        object.getProperties().stream().filter(p -> p.getName().equalsIgnoreCase("xml"))
            .collect(Collectors.reducing((a, b) -> null)).get();
    if (prop != null) {
      ni.setProperties((Document) converter.convertDataValueToObjectValue(prop.getValue(), null));
    }
    return ni;
  }

  public static NodeTemplateInstance convert(final NodeInstance object) {
    NodeTemplateInstance nti = null;
    try {
      Optional<NodeTemplateInstance> o = ntiRepository.find(DaoUtil.toLong(object.getId()));
      if (o.isPresent()) {
        nti = o.get();
      } else {
        nti = new NodeTemplateInstance();
        nti.setNodeType(object.getNodeType());
        nti.setTemplateId(object.getNodeTemplateID());
        nti.setState(NodeTemplateInstanceState.INITIAL);
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
            Optional<ServiceTemplateInstance> so = stiRepository.find(id);
            if (so.isPresent()) {
              nti.setServiceTemplateInstance(so.get());
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return nti;
  }
}
