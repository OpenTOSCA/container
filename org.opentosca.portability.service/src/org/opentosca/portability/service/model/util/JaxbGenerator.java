package org.opentosca.portability.service.model.util;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;

/**
 * This class can be used to generate a Schema representing the returnType TArtifacts
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
public class JaxbGenerator {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Class[] classes = new Class[4];
		classes[0] = org.opentosca.portability.service.model.Artifacts.class;
		classes[1] = org.opentosca.portability.service.model.DeploymentArtifact.class;
		classes[2] = org.opentosca.portability.service.model.ImplementationArtifact.class;
		classes[3] = org.opentosca.portability.service.model.ArtifactReferences.class;
		
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(classes);
			SchemaOutputResolver sor = new ArtifactSchemaOutputResolver();
			jaxbContext.generateSchema(sor);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
