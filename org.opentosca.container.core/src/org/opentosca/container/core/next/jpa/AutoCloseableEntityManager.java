package org.opentosca.container.core.next.jpa;

import javax.persistence.EntityManager;

public interface AutoCloseableEntityManager extends EntityManager, AutoCloseable {

}
