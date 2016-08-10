package org.opentosca.instancedata.service.impl.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.opentosca.settings.Settings;

/**
 * Abstract class which delivers common functionality for other DAOs
 * 
 * @author Marcus Eisele (marcus.eisele@gmail.com)
 *
 */
public abstract class AbstractDAO {

	/**
	 * JDBC-Url to the Database. The Database to store InstanceData (for
	 * NodeInstances, ServiceInstances [later: and Relationsships]) will reside
	 * at this spot. It will be created if * it does not exist yet.
	 * 
	 * @see org.opentosca.settings
	 */
	private final String databaseURL = "jdbc:derby:"
				+ Settings.getSetting("databaseLocation") + ";create=true";
	/**
	 * ORM EntityManager + Factory. These variables are global, as we do not
	 * want to create a new EntityManager/Factory each time a method is called.
	 */
	private EntityManagerFactory emf;
	protected EntityManager em;

	public AbstractDAO() {
		super();
	}

	@Override
	protected void finalize() throws Throwable {
		this.em.close();
		this.emf.close();
		super.finalize();
	}

	/**
	 * This method initializes the EntityManager/Factory in case it is not
	 * connected/setup yet. It is called by each method, to ensure that a
	 * connection exists. (Robustness!)
	 */
	protected void init() {
		if (this.emf == null) {
			Map<String, String> properties = new HashMap<String, String>();
			properties
					.put(PersistenceUnitProperties.JDBC_URL, this.databaseURL);
			// first parameter has to be the name of the JPA Unit (in this case)
			// see ServiceInstance JPA Unit in persinstence.xml
			this.emf = Persistence.createEntityManagerFactory("InstanceData",
					properties);
			this.em = this.emf.createEntityManager();
		}
	}

}