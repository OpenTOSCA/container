package org.opentosca.container.core.next.jpa;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.opentosca.container.core.common.Settings;

public final class EntityManagerProvider {

  private static final String DATABASE_FILE = Settings.DBDIR.resolve("opentosca").toAbsolutePath().toString();
  private static final String DATABASE_PROPERTIES = ";AUTO_SERVER=TRUE";
  private static final String JDBC_URL = "jdbc:h2:file:" + DATABASE_FILE + DATABASE_PROPERTIES;

  private static final String PERSISTENCE_UNIT = "default";

  private static EntityManagerFactory emf;

  static {
    final Map<String, Object> cfg = new HashMap<>();
    // We cannot reference Java's temp directory through the persistence.xml
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
      new Class[] {AutoCloseableEntityManager.class},
      (proxy, method, args) -> {
        return method.invoke(em, args);
      });
  }
}
