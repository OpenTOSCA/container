package org.opentosca.container.core.next.repository;

import java.util.List;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.IADeploymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IADeploymentInfoRepository extends JpaRepository<IADeploymentInfo, Long> {

    List<IADeploymentInfo> findByCsarIDAndRelPath(CsarId csar, String relPath);

    List<IADeploymentInfo> findByCsarID(CsarId csar);
}
