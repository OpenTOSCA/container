package org.opentosca.container.core.next.repository;

import org.opentosca.container.core.next.model.Verification;

public class VerificationRepository extends JpaRepository<Verification> {

    public VerificationRepository() {
        super(Verification.class);
    }
}
