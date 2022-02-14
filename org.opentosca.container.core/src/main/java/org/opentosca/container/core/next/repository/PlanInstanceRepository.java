package org.opentosca.container.core.next.repository;

import javax.xml.namespace.QName;

import org.opentosca.container.core.next.model.PlanInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanInstanceRepository extends JpaRepository<PlanInstance, Long> {

    PlanInstance findByCorrelationId(String correlationId);

    PlanInstance findByChoreographyCorrelationId(String choreographyCorrelationId);

    PlanInstance findByChoreographyCorrelationIdAndTemplateId(String choreographyCorrelationId, QName templateId);
}
