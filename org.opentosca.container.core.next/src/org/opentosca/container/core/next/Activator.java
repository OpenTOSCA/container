package org.opentosca.container.core.next;

import javax.persistence.EntityManager;

import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanInstanceOutput;
import org.opentosca.container.core.next.utils.EntityManagerProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

  private static BundleContext context;

  private static Logger logger = LoggerFactory.getLogger(Activator.class);


  static BundleContext getContext() {
    return context;
  }

  @Override
  public void start(final BundleContext bundleContext) throws Exception {
    logger.info("Starting bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(),
        bundleContext.getBundle().getVersion());
    context = bundleContext;


    final EntityManager em = EntityManagerProvider.getEntityManagerFactory().createEntityManager();
    final PlanInstance pi = new PlanInstance();
    pi.setState(PlanInstanceState.RUNNING);

    pi.addEvent(new PlanInstanceEvent("CREATE_IN_PROGRESS", "OpenTOSCA::SOME_TYPE_DEF",
        "This is a typical status message"));
    pi.addOutput(new PlanInstanceOutput("WebsiteUrl", "http://www.google.com", null));



    try {
      em.getTransaction().begin();
      em.persist(pi);
      em.getTransaction().commit();
    } finally {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      em.close();
    }
  }

  @Override
  public void stop(final BundleContext bundleContext) throws Exception {
    logger.info("Stopping bundle \"{}\" ({})...", bundleContext.getBundle().getSymbolicName(),
        bundleContext.getBundle().getVersion());
    context = null;
  }
}
