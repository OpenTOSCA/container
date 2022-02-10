package org.opentosca.container.core.next.repository;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EndpointRepository extends JpaRepository<Endpoint, Long> {

    List<Endpoint> findAll();

    List<Endpoint> findByPortTypeAndTriggeringContainerAndCsarId(QName portType, String triggeringContainer, CsarId csarId);

    List<Endpoint> findByTriggeringContainerAndServiceTemplateInstanceID(String triggeringContainer, Long serviceTemplateInstanceID);

    List<Endpoint> findByTriggeringContainerAndCsarIdAndPlanId(String triggeringContainer, CsarId csarId, QName planId);

    List<Endpoint> findByTriggeringContainerAndManagingContainerAndTypeImplementationAndIaName(String triggeringContainer, String managingContainer,
                                                                                                  QName nodeTypeImpl, String iaName);
}
