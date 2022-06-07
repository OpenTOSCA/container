package org.opentosca.container.core.next.repository;

import java.util.List;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.DeploymentProcessInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentProcessInfoRepository extends JpaRepository<DeploymentProcessInfo, Long> {

    List<DeploymentProcessInfo> findByCsarID(CsarId csar);
}
