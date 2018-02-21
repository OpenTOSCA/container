package org.opentosca.container.core.next.jpa;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.opentosca.container.core.next.utils.Consts;

public final class EntityManagerProvider {

    public static final String DATABASE_FILE = new File(Consts.DBDIR, "opentosca").getAbsolutePath();
    public static final String DATABASE_PROPERTIES = ";AUTO_SERVER=TRUE";

    public static final String JDBC_URL = "jdbc:h2:file:" + DATABASE_FILE + DATABASE_PROPERTIES;

    public static final String PERSISTENCE_UNIT = "default";

    private static EntityManagerFactory emf = null;

    static {
        final Map<String, Object> cfg = new HashMap<>();
        // We cannot reference Java's temp directory thru the persistence.xml unfortunately.
        // Therefore, we set the "javax.persistence.jdbc.url" property via code.
        cfg.put(PersistenceUnitProperties.JDBC_URL, JDBC_URL);
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, cfg);
    }

    public static AutoCloseableEntityManager createEntityManager() {
        return createEntityManager(emf.createEntityManager());
    }

    public static AutoCloseableEntityManager createEntityManager(final EntityManager em) {
        return proxyOf(em);
    }

    private static AutoCloseableEntityManager proxyOf(final EntityManager em) {
        return (AutoCloseableEntityManager) Proxy.newProxyInstance(EntityManagerProvider.class.getClassLoader(),
            new Class[] {AutoCloseableEntityManager.class}, (proxy, method, args) -> {
                return method.invoke(em, args);
            });
    }
}
