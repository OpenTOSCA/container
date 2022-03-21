package org.opentosca.container.core.next.repository;

import java.util.List;
import java.util.Optional;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceTemplateInstanceRepository extends JpaRepository<ServiceTemplateInstance, Long> {

    List<ServiceTemplateInstance> findByTemplateId(String templateId);

    List<ServiceTemplateInstance> findByCsarId(CsarId csarId);

    @EntityGraph(attributePaths = {"planInstances"})
    List<ServiceTemplateInstance> findWithPlanInstancesByCsarId(CsarId csarId);

    @EntityGraph(attributePaths = {"nodeTemplateInstances"})
    List<ServiceTemplateInstance> findWithNodeTemplateInstancesByCsarId(CsarId csarId);

    @EntityGraph(attributePaths = {"relationshipTemplateInstances"})
    List<ServiceTemplateInstance> findWithRelationshipTemplateInstancesByCsarId(CsarId csarId);

    @EntityGraph(attributePaths = {"nodeTemplateInstances"})
    Optional<ServiceTemplateInstance> findWithNodeTemplateInstancesById(Long id);

    @EntityGraph(attributePaths = {"relationshipTemplateInstances"})
    Optional<ServiceTemplateInstance> findWithRelationshipTemplateInstancesById(Long id);

    @EntityGraph(attributePaths = {"nodeTemplateInstances", "relationshipTemplateInstances"})
    Optional<ServiceTemplateInstance> findWithNodeAndRelationshipTemplateInstancesById(Long id);

    @EntityGraph(attributePaths = {"planInstances"})
    Optional<ServiceTemplateInstance> findWithPlanInstancesById(Long id);
}
