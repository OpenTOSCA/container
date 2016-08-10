package org.opentosca.portability.service.model.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.portability.service.model.Artifacts;
import org.opentosca.portability.service.model.DeploymentArtifact;
import org.opentosca.portability.service.model.ImplementationArtifact;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
public class PortabilityJaxbTest {
	
	
	/**
	 * @param args
	 * @throws JAXBException
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) throws JAXBException, URISyntaxException {
		Class[] classes = new Class[4];
		classes[0] = org.opentosca.portability.service.model.Artifacts.class;
		classes[1] = org.opentosca.portability.service.model.DeploymentArtifact.class;
		classes[2] = org.opentosca.portability.service.model.ImplementationArtifact.class;
		classes[3] = org.opentosca.portability.service.model.ArtifactReferences.class;
		
		JAXBContext jaxbContext = JAXBContext.newInstance(classes);
		Marshaller artifactMarshaller = jaxbContext.createMarshaller();
		
		Artifacts artifacts = new Artifacts();
		
		ArrayList<DeploymentArtifact> deploymentArtifacts = new ArrayList<DeploymentArtifact>();
		ArrayList<ImplementationArtifact> implementationArtifacts = new ArrayList<ImplementationArtifact>();
		
		//create the DA
		DeploymentArtifact tDeploymentArtifact = new DeploymentArtifact("myThirdDA", "blabla:type");
		Document doc = newDocumentFromString("<root><ustutt:AMIRef xmlns:ustutt=\"http://www.example.com/\">ami-321515</ustutt:AMIRef><ustutt:AMIRef xmlns:ustutt=\"http://www.example.com/\">ami-321515</ustutt:AMIRef></root>");
		tDeploymentArtifact.setArtifactSpecificContent(doc);
		
		deploymentArtifacts.add(tDeploymentArtifact);
		
		
		//create the IA (with refs)
		ArrayList<String> refs = new ArrayList<String>();
		refs.add("/csar/bla/blub/xy.war");
		refs.add("/csar/bla/blub/xyz.zip");
		//		ImplementationArtifact ia = new ImplementationArtifact("myOpName", "myInterfaceName", "myType", refs);
		ImplementationArtifact ia = new ImplementationArtifact("myOpName", null, "myType", refs);
		
		implementationArtifacts.add(ia);
		
		artifacts.setDeploymentArtifact(deploymentArtifacts);
		artifacts.setImplementationArtifact(implementationArtifacts);
		StringWriter sw = new StringWriter();
		
		artifactMarshaller.marshal(artifacts, sw);
		sw.flush();
		System.out.println(sw);
		
		
	}
	
	public static Document newDocumentFromString(String string) {
		// prepare everything
		InputSource iSource = new InputSource(new StringReader(string));
		
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(false);
			
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse
			doc = db.parse(iSource);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
}
