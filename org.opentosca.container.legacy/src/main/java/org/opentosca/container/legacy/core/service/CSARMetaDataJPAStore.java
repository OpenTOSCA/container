package org.opentosca.container.legacy.core.service;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.backwards.FileSystemDirectory;
import org.opentosca.container.legacy.core.model.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages CSAR meta data in the database by using Eclipse Link (JPA).
 */
@Deprecated
public class CSARMetaDataJPAStore {

  private final static Logger LOG = LoggerFactory.getLogger(CSARMetaDataJPAStore.class);

  private EntityManager em;

  /**
   * Initializes JPA.
   */
  private void initJPA() {
    if (this.em == null) {
      this.em = EntityManagerProvider.createEntityManager();
    }
  }

  /**
   * This method is called when the garbage collector destroys the class. We will then manually close
   * the EntityManager / Factory and pass control back.
   */
  @Override
  protected void finalize() throws Throwable {
    this.em.close();
    super.finalize();
  }

  /**
   * Persists the meta data of CSAR {@code csarID}.
   *  @param csarID        of the CSAR.
   * @param csarRoot
   * @param toscaMetaFile - represents the content of the TOSCA meta file of the CSAR.
   */
  public void storeCSARMetaData(final CSARID csarID, Path csarRoot, final TOSCAMetaFile toscaMetaFile) {
    initJPA();
    LOG.debug("Storing meta data of CSAR \"{}\"...", csarID);

    final CSARContent csar = new CSARContent(csarID, new FileSystemDirectory(csarRoot), toscaMetaFile);

    this.em.getTransaction().begin();
    this.em.persist(csar);
    this.em.getTransaction().commit();

    // clear the JPA 1st level cache
    this.em.clear();

    LOG.debug("Storing meta data of CSAR \"{}\" completed.", csarID);
  }

  /**
   * @param csarID of CSAR
   * @return {@code true} if meta data of CSAR {@code csarID} were found, otherwise {@code false}.
   */
  public boolean isCSARMetaDataStored(final CSARID csarID) {
    initJPA();
    LOG.debug("Checking if meta data of CSAR \"{}\" are stored...", csarID);

    final TypedQuery<CSARContent> query = this.em.createNamedQuery(CSARContent.BY_CSARID, CSARContent.class);
    query.setParameter("csarID", new CsarId(csarID));

    try {
      query.getSingleResult();
      LOG.debug("Meta data of CSAR \"{}\" were found.", csarID);
      return true;
    } catch (NoResultException e) {
      LOG.debug("Meta data of CSAR \"{}\" were not found.", csarID);
      return false;
    }
  }

  /**
   * Retrieves the meta data of CSAR {@code csarID}.
   *
   * @param csarID of CSAR.
   * @return {@link CSARContent} that gives access to all files and directories and the TOSCA meta
   * file of the CSAR.
   * @throws UserException if meta data of CSAR {@code csarID} were not found.
   */
  public CSARContent getCSARMetaData(final CSARID csarID) throws UserException {
    initJPA();
    LOG.debug("Retrieving meta data of CSAR \"{}\"...", csarID);

    final TypedQuery<CSARContent> query = this.em.createNamedQuery(CSARContent.BY_CSARID, CSARContent.class);
    query.setParameter("csarID", new CsarId(csarID));

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      String message = String.format("Meta data of CSAR \"%s\" were not found.", csarID);
      LOG.debug(message);
      throw new NotFoundException(message);
    }
  }

  /**
   * @return CSAR IDs of all stored CSAR files.
   */
  public Set<CSARID> getCSARIDsMetaData() {
    initJPA();
    LOG.trace("Retrieving CSAR IDs of all stored CSARs...");
    final Query getCSARIDsQuery = this.em.createNamedQuery(CSARContent.GET_CSARIDS);

    @SuppressWarnings("unchecked") final List<CSARID> csarIDs = getCSARIDsQuery.getResultList();
    LOG.trace("{} CSAR ID(s) was / were found.", csarIDs.size());
    return new HashSet<>(csarIDs);
  }

  /**
   * Deletes the meta data of CSAR {@code csarID}.
   *
   * @param csarID of CSAR.
   * @throws UserException if meta data of CSAR {@code csarID} were not found.
   */
  public void deleteCSARMetaData(final CSARID csarID) throws UserException {
    initJPA();
    LOG.debug("Deleting meta data of CSAR \"{}\"...", csarID);
    final CSARContent csarContent = getCSARMetaData(csarID);

    this.em.getTransaction().begin();
    this.em.remove(csarContent);
    this.em.getTransaction().commit();

    LOG.debug("Deleting meta data of CSAR \"{}\" completed.", csarID);
  }
}
