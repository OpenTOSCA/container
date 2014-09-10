package org.opentosca.util.jpa.converters.test;

import org.opentosca.util.jpa.converters.DOMDocumentConverter;

/**
 * @author Marcus Eisele - marcus.eisele@gmail.com
 * 
 */
public class DOMDocumentConverterTest {
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 3325055996997008356L;
	
	public static void main(String[] args) {
		
		// prepare xml String
		// String xmlString =
		// "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><bla xmls=\"http://example.com\"><user pwd=\"none\">Klaus</user></bla>";
		String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><demo:ApacheWebServerProperties xmlns:demo=\"http://www.example.com/demo\" xmlns=\"http://docs.oasis-open.org/tosca/ns/2011/12\\\" xmlns:binz_linux=\"http://ec2linux.aws.ia.opentosca.org\\\" xmlns:binz_vm=\"http://ec2vm.aws.ia.opentosca.org\\\" xmlns:opentosca=\"http://www.uni-stuttgart.de/opentosca\\\" xmlns:toscatypes=\"http://www.example.com/ToscaTypes\\\" xmlns:ustutt=\"http://www.uni-stuttgart.de/tosca\\\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\\\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\\\"><demo:httpdport>80</demo:httpdport>					</demo:ApacheWebServerProperties>";
		DOMDocumentConverter domConv = new DOMDocumentConverter();
		Object dom = domConv.convertDataValueToObjectValue(xmlString, null);
		
		String convString = (String) domConv.convertObjectValueToDataValue(dom,
				null);
		System.out.println("converted:\n" + xmlString + "\n" + convString);
	}
}
