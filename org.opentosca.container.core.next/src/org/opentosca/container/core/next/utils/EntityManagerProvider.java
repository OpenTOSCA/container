package org.opentosca.container.core.next.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

public final class EntityManagerProvider {

  public static final String PERSISTENCE_UNIT = "default";

  private static EntityManagerFactory factory = null;

  public static EntityManagerFactory getEntityManagerFactory() {
    if (factory == null) {
      // We cannot reference Java's temp directory thru the persistence.xml unfortunately.
      // Therefore, we set the "javax.persistence.jdbc.url" property via code.
      final String databaseFile = new File(Consts.DBDIR, "opentosca").getAbsolutePath();
      final String databaseProperties = ";AUTO_SERVER=TRUE";
      final Map<String, Object> cfg = new HashMap<>();
      cfg.put(PersistenceUnitProperties.JDBC_URL,
          "jdbc:h2:file:" + databaseFile + databaseProperties);
      factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, cfg);
    }
    return factory;
  }
}
