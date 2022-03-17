package org.opentosca.container.core.next.repository;

import java.util.List;
import java.util.Optional;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationshipTemplateInstanceRepository extends JpaRepository<RelationshipTemplateInstance, Long> {

    List<RelationshipTemplateInstance> findByTemplateId(String templateId);

    @EntityGraph(attributePaths = {"properties"})
    RelationshipTemplateInstance findWithPropertiesById(Long id);

}
