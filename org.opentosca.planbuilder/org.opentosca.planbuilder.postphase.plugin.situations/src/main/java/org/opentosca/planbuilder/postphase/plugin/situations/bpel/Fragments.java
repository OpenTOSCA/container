/**
 *
 */
package org.opentosca.planbuilder.postphase.plugin.situations.bpel;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.common.file.ResourceAccess;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class contains all the BPEL Fragments
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Fragments {

    private final DocumentBuilder docBuilder;

    /**
     * Constructor
     *
     * @throws ParserConfigurationException is thrown when initializing the internal DocumentBuild fails
     */
    public Fragments() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    public String generateBPEL4RESTLightGET(final String urlVarName, final String responseVarName) throws IOException {
        // BPEL4RESTLightGET_ServiceInstance_InstanceDataAPI.xml
        // <!-- $serviceInstanceUrlVarName, $ResponseVarName -->
        String bpelServiceInstanceGETString = readFileAsString("BPEL4RESTLightGET.xml");

        bpelServiceInstanceGETString = bpelServiceInstanceGETString.replace("$urlVarName", urlVarName);
        bpelServiceInstanceGETString = bpelServiceInstanceGETString.replace("$ResponseVarName", responseVarName);
        return bpelServiceInstanceGETString;
    }

    public Node generateBPEL4RESTLightGETAsNode(final String serviceInstanceUrlVarName,
                                                final String responseVarName) throws IOException, SAXException {
        final String templateString = generateBPEL4RESTLightGET(serviceInstanceUrlVarName, responseVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String generateAssignFromInputMessageToStringVariable(final String inputMessageElementLocalName,
                                                                 final String stringVariableName) throws IOException {
        String bpelAssignString = readFileAsString("BpelAssignFromInputToStringVar.xml");
        // <!-- $inputElementLocalName, $StringVariableName, $assignName -->
        bpelAssignString = bpelAssignString.replace("$inputElementLocalName", inputMessageElementLocalName);
        bpelAssignString = bpelAssignString.replace("$StringVariableName", stringVariableName);
        bpelAssignString =
            bpelAssignString.replace("$assignName", "assignFromInputToString" + System.currentTimeMillis());
        return bpelAssignString;
    }

    public Node generateAssignFromInputMessageToStringVariableAsNode(final String inputMessageElementLocalName,
                                                                     final String stringVariableName) throws IOException,
        SAXException {
        final String templateString =
            generateAssignFromInputMessageToStringVariable(inputMessageElementLocalName, stringVariableName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    private String readFileAsString(String fileName) throws IllegalArgumentException, IOException {
        String bpelString = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("/" + fileName));
        return bpelString;
    }
}
