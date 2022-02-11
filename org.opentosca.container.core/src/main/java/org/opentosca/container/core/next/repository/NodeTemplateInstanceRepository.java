package org.opentosca.container.core.next.repository;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeTemplateInstanceRepository extends JpaRepository<NodeTemplateInstance, Long> {

    List<NodeTemplateInstance> findByTemplateId(String nodeTemplateID);

    List<NodeTemplateInstance> findByTemplateType(QName templateType);

    List<NodeTemplateInstance> findByServiceTemplateInstanceAndTemplateId(ServiceTemplateInstance serviceTemplateInstance, String templateId);
}
