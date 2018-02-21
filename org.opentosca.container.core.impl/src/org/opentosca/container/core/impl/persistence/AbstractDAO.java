package org.opentosca.container.core.impl.persistence;

import javax.persistence.EntityManager;

import org.opentosca.container.core.next.jpa.EntityManagerProvider;

/**
 * Abstract class which delivers common functionality for other DAOs
 */
@Deprecated
public abstract class AbstractDAO {

    protected EntityManager em;


    public AbstractDAO() {
        super();
    }

    @Override
    protected void finalize() throws Throwable {
        this.em.close();
        super.finalize();
    }

    /**
     * This method initializes the EntityManager/Factory in case it is not connected/setup yet. It is
     * called by each method, to ensure that a connection exists. (Robustness!)
     */
    protected void init() {
        if (this.em == null) {
            this.em = EntityManagerProvider.createEntityManager();
        }
    }
}
