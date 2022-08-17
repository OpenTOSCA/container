package org.opentosca.container.core.next.repository;

import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeTemplateInstanceRepository extends JpaRepository<NodeTemplateInstance, Long> {

    List<NodeTemplateInstance> findByTemplateId(String nodeTemplateID);

    @EntityGraph(attributePaths = {"properties"})
    List<NodeTemplateInstance> findWithPropertiesByTemplateId(String nodeTemplateID);

    @EntityGraph(attributePaths = {"properties", "outgoingRelations"})
    List<NodeTemplateInstance> findWithPropertiesAndOutgoingByTemplateId(String nodeTemplateID);

    List<NodeTemplateInstance> findByTemplateType(QName templateType);

    List<NodeTemplateInstance> findByServiceTemplateInstanceAndTemplateId(ServiceTemplateInstance serviceTemplateInstance, String templateId);

    @EntityGraph(attributePaths = {"properties"})
    Optional<NodeTemplateInstance> findWithPropertiesById(Long id);

    @EntityGraph(attributePaths = {"properties", "outgoingRelations"})
    Optional<NodeTemplateInstance> findWithPropertiesAndOutgoingById(Long id);

    @EntityGraph(attributePaths = {"properties", "outgoingRelations"})
    Optional<NodeTemplateInstance> findWithOutgoingById(Long id);

    @EntityGraph(attributePaths = {"properties", "incomingRelations"})
    Optional<NodeTemplateInstance> findWithIncomingById(Long id);

    @EntityGraph(attributePaths = {"serviceTemplateInstance", "properties"})
    Optional<NodeTemplateInstance> findWithServiceTemplateInstanceById(Long id);
}
