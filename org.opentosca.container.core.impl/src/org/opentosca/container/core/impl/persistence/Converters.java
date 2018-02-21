package org.opentosca.container.core.impl.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.model.instance.NodeInstance;
import org.opentosca.container.core.model.instance.RelationInstance;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.model.instance.State;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceProperty;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceProperty;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceProperty;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.RelationshipTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.next.utils.Enums;
import org.w3c.dom.Document;

public abstract class Converters {
    private static ServiceTemplateInstanceRepository stiRepository = new ServiceTemplateInstanceRepository();

    private static NodeTemplateInstanceRepository ntiRepository = new NodeTemplateInstanceRepository();

    private static RelationshipTemplateInstanceRepository rtiRepository = new RelationshipTemplateInstanceRepository();

    private static DocumentConverter xmlConverter = new DocumentConverter();


    public static ServiceInstance convert(final ServiceTemplateInstance object) {
        final ServiceInstance si = new ServiceInstance(object.getCsarId(), object.getTemplateId(), "");
        if (object.getId() != null) {
            si.setId(object.getId().intValue());
        }
        si.setState(Enums.valueOf(State.ServiceTemplate.class, object.getState().toString()));
        si.setIDs();
        si.setCreated(object.getCreatedAt());
        final ServiceTemplateInstanceProperty prop = object.getProperties().stream()
                                                           .filter(p -> p.getName().equalsIgnoreCase("xml"))
                                                           .collect(Collectors.reducing((a, b) -> null)).get();
        if (prop != null) {
            si.setProperties((Document) xmlConverter.convertDataValueToObjectValue(prop.getValue(), null));
        }
        return si;
    }

    public static ServiceTemplateInstance convert(final ServiceInstance object) {
        ServiceTemplateInstance sti = null;
        try {
            final Optional<ServiceTemplateInstance> o = stiRepository.find(DaoUtil.toLong(object.getDBId()));
            if (o.isPresent()) {
                sti = o.get();
            } else {
                sti = new ServiceTemplateInstance();
                sti.setCsarId(object.getCSAR_ID());
                sti.setTemplateId(object.getServiceTemplateID());
                sti.setState(ServiceTemplateInstanceState.INITIAL);
            }
            final Document properties = object.getProperties();
            if (properties != null) {
                final String value = (String) xmlConverter.convertObjectValueToDataValue(properties, null);
                final ServiceTemplateInstanceProperty prop = new ServiceTemplateInstanceProperty();
                prop.setName("xml");
                prop.setType("xml");
                prop.setValue(value);
                sti.addProperty(prop);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return sti;
    }

    public static NodeInstance convert(final NodeTemplateInstance object) {
        final NodeInstance ni = new NodeInstance(object.getTemplateId(), "", object.getTemplateType(),
            convert(object.getServiceTemplateInstance()));
        if (object.getId() != null) {
            ni.setId(object.getId().intValue());
        }
        ni.setNodeInstanceID();
        ni.setState(Enums.valueOf(State.Node.class, object.getState().toString()));
        ni.setCreated(object.getCreatedAt());
        final List<NodeTemplateInstanceProperty> props = object.getProperties().stream()
                                                               .filter(p -> p.getName().equalsIgnoreCase("xml"))
                                                               .collect(Collectors.toList());
        if (props != null && !props.isEmpty() && props.get(0) != null) {
            ni.setProperties((Document) xmlConverter.convertDataValueToObjectValue(props.get(0).getValue(), null));
        }
        return ni;
    }

    public static NodeTemplateInstance convert(final NodeInstance object) {
        NodeTemplateInstance nti = null;
        try {
            final Optional<NodeTemplateInstance> o = ntiRepository.find(DaoUtil.toLong(object.getId()));
            if (o.isPresent()) {
                nti = o.get();
            } else {
                nti = new NodeTemplateInstance();
                nti.setTemplateType(object.getNodeType());
                nti.setTemplateId(object.getNodeTemplateID());
                nti.setState(NodeTemplateInstanceState.INITIAL);
                if (object.getProperties() != null) {
                    final NodeTemplateInstanceProperty prop = new NodeTemplateInstanceProperty();
                    prop.setName("xml");
                    prop.setType("xml");
                    prop.setValue((String) xmlConverter.convertObjectValueToDataValue(object.getProperties(), null));
                    nti.addProperty(prop);
                }
                if (object.getServiceInstance() != null) {
                    try {
                        final Long id = DaoUtil.toLong(object.getServiceInstance().getDBId());
                        final Optional<ServiceTemplateInstance> so = stiRepository.find(id);
                        if (so.isPresent()) {
                            nti.setServiceTemplateInstance(so.get());
                        }
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return nti;
    }

    public static RelationInstance convert(final RelationshipTemplateInstance object) {
        final RelationInstance ri = new RelationInstance(object.getTemplateId(), object.getTemplateId().getLocalPart(),
            object.getTemplateType(), null /* ServiceInstance */, null /* Source NodeInstance */,
            null /* Target NodeInstance */);
        if (object.getId() != null) {
            ri.setId(object.getId().intValue());
        }
        ServiceTemplateInstance sti = null;
        final NodeTemplateInstance source = object.getSource();
        if (source != null && sti == null) {
            sti = source.getServiceTemplateInstance();
        }
        final NodeTemplateInstance target = object.getTarget();
        if (target != null && sti == null) {
            sti = target.getServiceTemplateInstance();
        }
        if (sti != null) {
            ri.setServiceInstance(convert(sti));
        }
        if (source != null) {
            ri.setSourceInstance(convert(source));
        }
        if (target != null) {
            ri.setTargetInstance(convert(target));
        }
        ri.setRelationInstanceID();
        ri.setState(Enums.valueOf(State.Relationship.class, object.getState().toString()));
        ri.setCreated(object.getCreatedAt());
        final List<RelationshipTemplateInstanceProperty> props = object.getProperties().stream()
                                                                       .filter(p -> p.getName().equalsIgnoreCase("xml"))
                                                                       .collect(Collectors.toList());
        if (props != null && !props.isEmpty() && props.get(0) != null) {
            ri.setProperties((Document) xmlConverter.convertDataValueToObjectValue(props.get(0).getValue(), null));
        }
        return ri;
    }

    public static RelationshipTemplateInstance convert(final RelationInstance object) {
        RelationshipTemplateInstance rti = null;
        try {
            final Optional<RelationshipTemplateInstance> o = rtiRepository.find(DaoUtil.toLong(object.getId()));
            if (o.isPresent()) {
                rti = o.get();
            } else {
                rti = new RelationshipTemplateInstance();
                rti.setTemplateType(object.getRelationshipType());
                rti.setTemplateId(object.getRelationshipTemplateID());
                rti.setState(RelationshipTemplateInstanceState.INITIAL);
                if (object.getProperties() != null) {
                    final RelationshipTemplateInstanceProperty prop = new RelationshipTemplateInstanceProperty();
                    prop.setName("xml");
                    prop.setType("xml");
                    prop.setValue((String) xmlConverter.convertObjectValueToDataValue(object.getProperties(), null));
                    rti.addProperty(prop);
                }
                if (object.getSourceInstance() != null) {
                    try {
                        final Long id = DaoUtil.toLong(object.getSourceInstance().getId());
                        final Optional<NodeTemplateInstance> no = ntiRepository.find(id);
                        if (no.isPresent()) {
                            rti.setSource(no.get());
                        }
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (object.getTargetInstance() != null) {
                    try {
                        final Long id = DaoUtil.toLong(object.getTargetInstance().getId());
                        final Optional<NodeTemplateInstance> no = ntiRepository.find(id);
                        if (no.isPresent()) {
                            rti.setTarget(no.get());
                        }
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return rti;
    }
}
