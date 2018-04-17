package org.opentosca.container.core;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.configuration.FileBasedRepositoryConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.opentosca.container.core.next.utils.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WineryRepositoryTests {

    private static Logger logger = LoggerFactory.getLogger(WineryRepositoryTests.class);

    @Test
    public void testTryOut() throws Exception {
        final Path csarRoot = Paths.get(Consts.TMPDIR, "opentosca", "CSARs");
        // ensures csar-root exists
        Files.createDirectories(csarRoot);
        final DirectoryStream<Path> stream = Files.newDirectoryStream(csarRoot, entry -> Files.isDirectory(entry));
        boolean hit = false;
        for (final Path p : stream) {
            // create a repository for every CSAR we know
            final IRepository r = RepositoryFactory.getRepository(new FileBasedRepositoryConfiguration(p));
            logger.info("{}", r.getUsedNamespaces());
            hit |= true;
        }
        Assert.assertTrue( "no CSARs were available under " + csarRoot, hit);
    }
}
