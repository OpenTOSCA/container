package org.opentosca.container.core.model.csar.toscametafile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class TOSCAMetaFileParserTest {

    private final TOSCAMetaFileParser parser = new TOSCAMetaFileParser();

    @Test
    public void testParsingOfNewMetaFileStructure() throws Exception {

        final String test = "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\r\n"
            + "CSAR-Version: 1.0\r\n" + "Created-By: Winery 2.0.0-SNAPSHOT\r\n" + "TOSCA-Meta-Version: 1.0\r\n" + "\r\n"
            + "Name: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\r\n"
            + "Content-Type: application/vnd.oasis.tosca.definitions\r\n"
            + "SHA-256: 6cb74ecd57adaebc2d9b87deed0be50e\r\n" + "\r\n"
            + "Name: Definitions/nodetypes__MyTinyToDoDockerContainer.tosca\r\n"
            + "Content-Type: application/vnd.oasis.tosca.definitions\r\n"
            + "SHA-256: 6cb74ecd57adaebc2d9b87deed0be50e\r\n" + "\r\n"
            + "Name: Definitions/ToscaBaseTypes__HostedOn.tosca\r\n"
            + "Content-Type: application/vnd.oasis.tosca.definitions\r\n"
            + "SHA-256: 6cb74ecd57adaebc2d9b87deed0be50e\r\n" + "\r\n"
            + "Name: Definitions/artifacttemplates__MyTinyToDo_DA.tosca\r\n"
            + "Content-Type: application/vnd.oasis.tosca.definitions\r\n"
            + "SHA-256: 6cb74ecd57adaebc2d9b87deed0be50e\r\n" + "\r\n"
            + "Name: Definitions/nodetypes__DockerEngine.tosca\r\n"
            + "Content-Type: application/vnd.oasis.tosca.definitions\r\n"
            + "SHA-256: 6cb74ecd57adaebc2d9b87deed0be50e\r\n" + "\r\n"
            + "Name: Definitions/artifacttypes__DockerContainerArtifact.tosca\r\n"
            + "Content-Type: application/vnd.oasis.tosca.definitions\r\n" + "SHA-256: 6cb74ecd57adaebc2d9b87deed0be50e";

        // Prepare temp file
        final Path file = Files.createTempFile("metafile", ".tosca");
        FileUtils.writeStringToFile(file.toFile(), test);

        // Test
        final TOSCAMetaFile metafile = this.parser.parse(file);

        // Assertions
        assertThat(metafile, is(not(nullValue())));
        assertThat(metafile.getBlock0().size(), is(4));
        assertThat(metafile.getFileBlocks().size(), is(6));

        // Clean up
        FileUtils.deleteQuietly(file.toFile());
    }
}
