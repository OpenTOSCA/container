package org.opentosca.container.core.impl.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipManager {

    // Singleton Pattern
    private static final ZipManager INSTANCE = new ZipManager();


    private ZipManager() {}

    public static ZipManager getInstance() {
        return ZipManager.INSTANCE;
    }

    private List<File> getUnzippedFiles(final List<File> files) {

        return files;
    }


    // Buffer for zipping/unzipping
    private final static int BUFFER = 16384;

    private final static Logger LOG = LoggerFactory.getLogger(ZipManager.class);


    /**
     * Creates a new ZIP archive containing the contents of the specified directory.<br>
     * Existing archives with the same name will be overwritten automatically.
     *
     * @param directory - Absolute path to the folder which contents should be zipped, including sub
     *        folders.
     * @param archive - Absolute path to ZIP archive.
     */
    public File zip(final File directory, final File archive) {

        try {
            ZipManager.LOG.info("Zipping {} ...", directory.getPath());
            final FileOutputStream dest = new FileOutputStream(archive);
            final ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(dest));

            ZipManager.zipDir(directory.getAbsolutePath(), directory, zos, archive.getAbsolutePath());

            zos.flush();
            zos.close();
            ZipManager.LOG.info("Zipping completed.");

        }
        catch (final FileNotFoundException e) {
            ZipManager.LOG.error("Error", e);
            return null;
        }
        catch (final IOException e) {
            ZipManager.LOG.error("Error", e);
            return null;
        }
        return archive;

    }

    /**
     * Private helper class to recursively zip a folder and its contents.
     *
     * @param currentDir Current working directory.
     * @param dirToZip Root directory that should be zipped.
     * @param zos Already opened Output Stream.
     * @param bCompress Whether to use <i>ZipOutputStream.DEFLATED</i> or <i>ZipOutputStream.STORED</i>.
     */
    private static void zipDir(final String currentDir, final File dirToZip, final ZipOutputStream zos,
                               final String archive) throws FileNotFoundException, IOException {

        zos.setMethod(ZipOutputStream.DEFLATED);

        final byte data[] = new byte[ZipManager.BUFFER];
        final File archiveFile = new File(archive);

        // Switch to current directory and fetch its entries
        final File cDir = new File(currentDir);
        final String[] dirList = cDir.list();

        final int length = dirList.length;

        if (length == 0) {
            ZipManager.LOG.debug("ZIP: - It's a empty directory. Adding...");
            final String relPath = cDir.getCanonicalPath().substring(dirToZip.getCanonicalPath().length() + 1,
                                                                     cDir.getCanonicalPath().length());
            final ZipEntry entry = new ZipEntry(relPath + "/");
            zos.putNextEntry(entry);
        }

        // Loop through entries
        for (int i = 0; i < length; i++) {

            ZipManager.LOG.debug("ZIP: Processing entry: '" + dirList[i] + "'");
            final File f = new File(cDir, dirList[i]);

            // Skip created archive if it's in the same directory
            if (archiveFile.getAbsolutePath().equals(f.getAbsolutePath())) {
                ZipManager.LOG.debug("ZIP: - Created archive found. Skipping...");
                continue;
            }

            // Go through a sub directory recursively with new cDir
            if (f.isDirectory()) {
                ZipManager.LOG.debug("ZIP: - Directory found. Going into directory...");
                ZipManager.zipDir(f.getCanonicalPath(), dirToZip, zos, archive);
                continue;
            }

            // Generate relative path
            final String relPath =
                f.getCanonicalPath().substring(dirToZip.getCanonicalPath().length() + 1, f.getCanonicalPath().length());
            final ZipEntry entry = new ZipEntry(relPath);

            // Open input streams and write entry to zip
            final FileInputStream fis = new FileInputStream(f.getCanonicalPath());
            final BufferedInputStream origin = new BufferedInputStream(fis, ZipManager.BUFFER);
            zos.putNextEntry(entry);
            ZipManager.LOG.debug("ZIP: - Adding file... ");
            int count;

            while ((count = origin.read(data, 0, ZipManager.BUFFER)) != -1) {
                zos.write(data, 0, count);
            }

            ZipManager.LOG.debug("ZIP: File added!");
            origin.close();

        }
    }

    /**
     * Unzips an archive to specified location.
     *
     * @param file Location of ZIP archive.
     * @param toTarget Directory where contents of ZIP archive should be placed.
     * @return
     */
    public List<File> unzip(final File file, final File toTarget) {

        final List<File> contents = new ArrayList<>();
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        ZipInputStream zis = null;
        ZipFile zipFile = null;

        try {
            ZipManager.LOG.info("Unzipping {} ...", file.getPath());
            // Open input streams
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            zis = new ZipInputStream(bis);

            ZipEntry entry = zis.getNextEntry();

            String entryTarget = "";
            // ZipFile zipFile;
            int zipSize;
            int entryIndex = 0;

            zipFile = new ZipFile(file);
            zipSize = zipFile.size();

            ZipManager.LOG.debug("UNZIP: Initialization complete.");

            // If targetDirectory doesn't exist, create it now.
            if (!toTarget.exists()) {
                toTarget.mkdirs();
                ZipManager.LOG.debug("UNZIP: Directory created: {}", toTarget.getName());
            }

            // Go through the archive entry by entry
            while (entry != null) {
                entryIndex++;
                final String name = entry.getName();
                entryTarget = toTarget.getPath() + File.separator + name;
                ZipManager.LOG.debug("UNZIP: Processing entry " + entryIndex + File.separator + zipSize + ": " + name);

                if (entry.isDirectory()) {
                    ZipManager.LOG.debug("UNZIP: - Creating directory... ");
                    new File(entryTarget).mkdirs();
                    ZipManager.LOG.debug("UNZIP: - Directory created!");

                } else {

                    int count;
                    final byte data[] = new byte[ZipManager.BUFFER];

                    final File entryTargetFile = new File(entryTarget);

                    contents.add(entryTargetFile);

                    final File parent = entryTargetFile.getParentFile();
                    if (parent != null) {
                        parent.mkdirs();
                    }

                    final FileOutputStream fos = new FileOutputStream(entryTargetFile);
                    final BufferedOutputStream dest = new BufferedOutputStream(fos, ZipManager.BUFFER);
                    // ZipManager.LOG.debug("UNZIP: - Decompressing file... ");

                    while ((count = zis.read(data, 0, ZipManager.BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    // dest.flush();
                    dest.close();
                    fos.close();

                    // ZipManager.LOG.debug("UNZIP: - File decompressed!");
                    entryTarget = "";
                }

                entry = zis.getNextEntry();

            }

            // zis.close();
            // zipFile.close();
            ZipManager.LOG.info("Unzipping completed!");

        }
        catch (final FileNotFoundException e) {
            ZipManager.LOG.error("Error", e);
            return null;
        }
        catch (final ZipException e) {
            ZipManager.LOG.error("Error", e);
            return null;
        }
        catch (final IOException e) {
            ZipManager.LOG.error("Error", e);
            return null;
        }
        finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                }
                catch (final IOException e) {
                    ZipManager.LOG.error("", e);
                }
            }

            if (zis != null) {
                try {
                    zis.close();
                }
                catch (final IOException e) {
                    ZipManager.LOG.error("", e);
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                }
                catch (final IOException e) {
                    ZipManager.LOG.error("", e);
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final IOException e) {
                    ZipManager.LOG.error("", e);
                }
            }

        }

        return this.getUnzippedFiles(contents);
    }
}
