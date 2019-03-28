package org.opentosca.planbuilder.csarhandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.legacy.core.model.CSARContent;
import org.opentosca.container.legacy.core.service.CoreFileServiceImpl;
import org.opentosca.container.legacy.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is a small layer over the ICoreFileService of the OpenTOSCA Core
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
@Deprecated
public class CSARHandler {

  public static final Path planBuilderWorkingDir = Paths.get(System.getProperty("java.io.tmpdir"), "opentosca", "container", "planbuilder");

  private static final Logger LOG = LoggerFactory.getLogger(CSARHandler.class);

  private final ICoreFileService fileService;

  public CSARHandler() {
    try {
      Files.createDirectories(planBuilderWorkingDir);
    } catch (IOException e) {
      LOG.warn("Could not create working direcotry for planbuilder", e);
      throw new ExceptionInInitializerError(e);
    }
    fileService = new CoreFileServiceImpl(planBuilderWorkingDir);
  }

  /**
   * Stores a CSAR given as file object
   *
   * @param file File referencing a CSAR
   * @return an Object representing an ID of the stored CSAR, if something went wrong null is returned
   * instead
   */
  public Object storeCSAR(final File file) throws UserException, SystemException {
    LOG.debug("Trying to store csar");

    final CSARID csarId = fileService.storeCSAR(file.toPath());
    if (csarId == null) {
      LOG.warn("Storing CSAR file failed");
      return null;
    }
    LOG.info("Storing CSAR file was successful");
    return csarId;
  }

  public void deleteCSAR(final CSARID id) throws UserException, SystemException {
    fileService.deleteCSAR(id);
  }

  /**
   * Deletes all CSARs in the OpenTOSCA Core
   */
  public void deleteAllCsars() {
    LOG.info("Deleting all CSAR files");
    try {
      fileService.deleteCSARs();
    } catch (final SystemException e) {

    }
  }

  /**
   * Returns a CSARContent Object for the given CSARID
   *
   * @param id a CSARID
   * @return the CSARContent for the given CSARID
   * @throws UserException is thrown when something inside the OpenTOSCA Core fails
   */
  public CSARContent getCSARContentForID(final CSARID id) throws UserException {
    LOG.debug("Fetching CSARContent for given ID");
    return fileService.getCSAR(id);
  }
}
