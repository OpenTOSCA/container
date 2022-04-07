package org.opentosca.container.core.next.repository;

import javax.xml.namespace.QName;

import org.opentosca.container.core.next.model.PlanInstance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanInstanceRepository extends JpaRepository<PlanInstance, Long> {

    PlanInstance findByCorrelationId(String correlationId);

    @EntityGraph(attributePaths = {"events"})
    PlanInstance findWithLogsByCorrelationId(String correlationId);

    @EntityGraph(attributePaths = {"events", "outputs"})
    PlanInstance findWithLogsAndOutputsByCorrelationId(String correlationId);

    PlanInstance findByChoreographyCorrelationId(String choreographyCorrelationId);

    PlanInstance findByChoreographyCorrelationIdAndTemplateId(String choreographyCorrelationId, QName templateId);

    @EntityGraph(attributePaths = {"events"})
    PlanInstance findWithLogsById(Long id);

    @EntityGraph(attributePaths = {"outputs"})
    PlanInstance findWithOutputsById(Long id);
}
