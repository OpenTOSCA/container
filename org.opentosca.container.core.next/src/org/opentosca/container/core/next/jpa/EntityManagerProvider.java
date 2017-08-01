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

  public static final String PERSISTENCE_UNIT = "default";

  private static EntityManagerFactory emf = null;

  static {
    // We cannot reference Java's temp directory thru the persistence.xml unfortunately.
    // Therefore, we set the "javax.persistence.jdbc.url" property via code.
    final String databaseFile = new File(Consts.DBDIR, "opentosca").getAbsolutePath();
    final String databaseProperties = ";AUTO_SERVER=TRUE";
    final Map<String, Object> cfg = new HashMap<>();
    cfg.put(PersistenceUnitProperties.JDBC_URL,
        "jdbc:h2:file:" + databaseFile + databaseProperties);
    emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, cfg);
  }

  public static AutoCloseableEntityManager createEntityManager() {
    return createEntityManager(emf.createEntityManager());
  }

  public static AutoCloseableEntityManager createEntityManager(final EntityManager em) {
    return proxyOf(em);
  }

  private static AutoCloseableEntityManager proxyOf(final EntityManager em) {
    return (AutoCloseableEntityManager) Proxy.newProxyInstance(
        EntityManagerProvider.class.getClassLoader(),
        new Class[] {AutoCloseableEntityManager.class}, (proxy, method, args) -> {
          return method.invoke(em, args);
        });
  }
}
