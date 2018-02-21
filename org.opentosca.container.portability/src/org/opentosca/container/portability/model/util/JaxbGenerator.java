package org.opentosca.container.portability.model.util;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;

/**
 * This class can be used to generate a Schema representing the returnType TArtifacts
 */
public class JaxbGenerator {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final Class[] classes = new Class[4];
        classes[0] = org.opentosca.container.portability.model.Artifacts.class;
        classes[1] = org.opentosca.container.portability.model.DeploymentArtifact.class;
        classes[2] = org.opentosca.container.portability.model.ImplementationArtifact.class;
        classes[3] = org.opentosca.container.portability.model.ArtifactReferences.class;

        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            final SchemaOutputResolver sor = new ArtifactSchemaOutputResolver();
            jaxbContext.generateSchema(sor);
        } catch (final JAXBException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
