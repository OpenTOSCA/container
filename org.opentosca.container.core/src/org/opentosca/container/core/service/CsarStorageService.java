package org.opentosca.container.core.service;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;

import javax.ws.rs.NotFoundException;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;

public interface CsarStorageService {

    /**
     * Loads all available CSARs as {@link CSARContent} from storage.
     * 
     * @return Set of {@link CSARContent} objects
     */
    public Set<CSARContent> findAll();

    /**
     * Loads the data of the CSAR associated with the given {@link CSARID} from storage.
     * 
     * @param id The unique {@link CSARID} of the CSAR to retrieve.
     * @return A {@link CSARContent} instance representing the CSAR identified by the ID.
     * 
     * @throws NotFoundException in case the CSARID was unknown.
     */
    public CSARContent findById(CSARID id) throws NotFoundException;

    /**
     * Temporarily stores all information in the given {@link InputStream} as a temporary file with the
     * given filename.
     * 
     * @param filename The name for the temporary file.
     * @param is A stream containing the data to store temporarily.
     * @return The Path the CSAR has been stored to.
     * 
     * @apiSpec The given inputStream is <b>not</b> closed by this method.
     */
    public Path storeCSARTemporarily(final String filename, final InputStream is);

    /**
     * <p>
     * Stores the given CSAR-File permanently.
     * <p>
     * If the given CSAR is already stored, this method rejects the input. Otherwise the following steps
     * are performed.
     * <ol>
     * <li>Unpack the CSAR archive to a temporary location</li>
     * <li>Validate CSAR content</li>
     * <li>Parse TOSCA Metafile</li>
     * <li>Store <b>unpacked</b> CSAR content in permanent storage</li>
     * <li>Store Metadata about the CSAR to the database</li>
     * </ol>
     * 
     * @param csarLocation The location of the CSAR file
     * @return a {@link CSARID} to uniquely identify the CSAR that has been stored
     * 
     * @throws SystemException if unpacking the CSAR failed, access to a directory was denied while
     *         getting files and directories in unpack directory or if an error occurred during storing
     *         to disk.
     * 
     * @throws UserException if {@code csarFile} is not a existent file, has wrong file extension, is
     *         already stored or it's content is invalid, e.g. contains no TOSCA file or has an invalid
     *         TOSCA meta file.
     */
    public CSARID storeCSAR(Path csarLocation) throws UserException, SystemException;


    /**
     * <p>
     * Deletes the CSAR belonging to the given {@link CSARID} from storage. That includes deleting it's
     * metadata from the database.
     * <p>
     * If an error occurs during the deletion of files, the metadata stays in the database. Warning:
     * this is a process that can not be compensated. As such errors can leave the CSAR in an
     * inconsistent state.
     * 
     * @param csarId The {@link CSARID} identifying the CSAR to delete.
     * @throws UserException if the identified CSAR was not found.
     *
     * @throws SystemException if an error occurred during deleting files.
     */
    public void deleteCSAR(CSARID csarId) throws UserException, SystemException;
}
