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
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipManager {

  // Singleton Pattern
  private static final ZipManager INSTANCE = new ZipManager();

  // Buffer for zipping/unzipping
  private final static int BUFFER = 16384;

  private final static Logger LOG = LoggerFactory.getLogger(ZipManager.class);

  private ZipManager() {
  }

  public static ZipManager getInstance() {
    return ZipManager.INSTANCE;
  }

  /**
   * Creates a new ZIP archive containing the contents of the specified directory.<br>
   * Existing archives with the same name will be overwritten automatically.
   *
   * @param directory - Absolute path to the folder which contents should be zipped, including sub
   *                  folders.
   * @param archive   - Absolute path to ZIP archive.
   */
  public File zip(final File directory, final File archive) {
    LOG.info("Zipping {} ...", directory.getPath());

    try (final FileOutputStream destination = new FileOutputStream(archive);
         final ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(destination))) {

      ZipManager.zipDir(directory.getAbsolutePath(), directory, zos, archive.getAbsolutePath());
      zos.flush();

    } catch (final IOException e) {
      LOG.error("Error", e);
      return null;
    }
    LOG.info("Zipping completed.");
    return archive;

  }

  /**
   * Private helper class to recursively zip a folder and its contents.
   *
   * @param currentDir Current working directory.
   * @param dirToZip   Root directory that should be zipped.
   * @param zos        Already opened Output Stream.
   */
  private static void zipDir(final String currentDir, final File dirToZip, final ZipOutputStream zos,
                             final String archive) throws IOException {

    zos.setMethod(ZipOutputStream.DEFLATED);

    final byte[] data = new byte[ZipManager.BUFFER];
    final File archiveFile = new File(archive);

    // Switch to current directory and fetch its entries
    final File cDir = new File(currentDir);
    final String[] dirList = cDir.list();
    if (dirList == null) {
      throw new FileNotFoundException("Given Directory " + currentDir + " was not a directory or an IOException occurred");
    }

    final int length = dirList.length;

    if (length == 0) {
      LOG.trace("ZIP: - It's a empty directory. Adding...");
      final String relPath = cDir.getCanonicalPath().substring(dirToZip.getCanonicalPath().length() + 1);
      final ZipEntry entry = new ZipEntry(relPath + "/");
      zos.putNextEntry(entry);
    }

    // Loop through entries
    for (String s : dirList) {
      LOG.trace("ZIP: Processing entry: '" + s + "'");
      final File f = new File(cDir, s);

      // Skip created archive if it's in the same directory
      if (archiveFile.getAbsolutePath().equals(f.getAbsolutePath())) {
        LOG.trace("ZIP: - Created archive found. Skipping...");
        continue;
      }

      // Go through a sub directory recursively with new cDir
      if (f.isDirectory()) {
        LOG.trace("ZIP: - Directory found. Going into directory...");
        ZipManager.zipDir(f.getCanonicalPath(), dirToZip, zos, archive);
        continue;
      }

      // Generate relative path
      final String relPath = f.getCanonicalPath().substring(dirToZip.getCanonicalPath().length() + 1);
      final ZipEntry entry = new ZipEntry(relPath);

      // Open input streams and write entry to zip
      final FileInputStream fis = new FileInputStream(f.getCanonicalPath());
      final BufferedInputStream origin = new BufferedInputStream(fis, ZipManager.BUFFER);
      zos.putNextEntry(entry);
      LOG.trace("ZIP: - Adding file... ");
      int count;

      while ((count = origin.read(data, 0, ZipManager.BUFFER)) != -1) {
        zos.write(data, 0, count);
      }

      LOG.trace("ZIP: File added!");
      origin.close();
    }
  }

  /**
   * Unzips an archive to specified location.
   *
   * @param file     Location of ZIP archive.
   * @param toTarget Directory where contents of ZIP archive should be placed.
   * @return
   */
  public List<File> unzip(final File file, final File toTarget) {

    LOG.info("Unzipping {} ...", file.getPath());

    final List<File> contents = new ArrayList<>();
    try (final FileInputStream fis = new FileInputStream(file);
         final BufferedInputStream bis = new BufferedInputStream(fis);
         final ZipInputStream zis = new ZipInputStream(bis);
         final ZipFile zipFile = new ZipFile(file)) {
      LOG.trace("UNZIP: Initialization complete.");

      if (!toTarget.exists()) {
        toTarget.mkdirs();
        LOG.trace("UNZIP: Directory created: {}", toTarget.getName());
      }

      int entryIndex = 0;
      // Go through the archive entry by entry
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        entryIndex++;
        final String name = entry.getName();
        String entryTarget = toTarget.getPath() + File.separator + name;
        LOG.trace("UNZIP: Processing entry " + entryIndex + "/" + zipFile.size() + ": " + name);

        if (entry.isDirectory()) {
          LOG.trace("UNZIP: - Creating directory... ");
          new File(entryTarget).mkdirs();
          LOG.trace("UNZIP: - Directory created!");

        } else {
          final File entryTargetFile = new File(entryTarget);
          contents.add(entryTargetFile);

          final File parent = entryTargetFile.getParentFile();
          if (parent != null) {
            parent.mkdirs();
          }

          try (final FileOutputStream fos = new FileOutputStream(entryTargetFile);
               final BufferedOutputStream dest = new BufferedOutputStream(fos, ZipManager.BUFFER)) {
            int count;
            final byte[] buffer = new byte[ZipManager.BUFFER];
            while ((count = zis.read(buffer, 0, ZipManager.BUFFER)) != -1) {
              dest.write(buffer, 0, count);
            }
          }
        }
      }
    } catch (final IOException e) {
      LOG.error("Error", e);
      return null;
    }
    LOG.info("Unzipping completed!");
    return contents;
  }
}
