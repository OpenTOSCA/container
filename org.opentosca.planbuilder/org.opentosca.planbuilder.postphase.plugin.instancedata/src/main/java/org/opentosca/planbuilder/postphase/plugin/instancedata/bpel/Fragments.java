/**
 *
 */
package org.opentosca.planbuilder.postphase.plugin.instancedata.bpel;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.common.file.ResourceAccess;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
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

    /**
     * Creates a BPEL assign activity that reads the property values from a NodeInstance Property response and sets the
     * given variables
     *
     * @param assignName                          the name of the assign activity
     * @param nodeInstancePropertyResponseVarName the name of the variable holding the property data
     * @param propElement2BpelVarNameMap          a Map from DOM Elements (representing Node Properties) to BPEL
     *                                            variable names
     * @return a Node containing a BPEL assign activity
     * @throws IOException  is thrown when reading internal files fail
     * @throws SAXException is thrown when parsing internal files fail
     */
    public Node createAssignFromInstancePropertyToBPELVariableAsNode(final String assignName,
                                                                     final String nodeInstancePropertyResponseVarName,
                                                                     final Map<String, String> propElement2BpelVarNameMap, String namespace) throws IOException,
        SAXException {
        final String templateString =
            createAssignFromInstancePropertyToBPELVariableAsString(assignName, nodeInstancePropertyResponseVarName,
                propElement2BpelVarNameMap, namespace);
        return this.transformStringToNode(templateString);
    }

    public Node transformStringToNode(String xmlString) throws SAXException, IOException {
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Creates a BPEL assign activity that reads the property values from a NodeInstance Property response and sets the
     * given variables
     *
     * @param assignName                          the name of the assign activity
     * @param nodeInstancePropertyResponseVarName the name of the variable holding the property data
     * @param propElement2BpelVarNameMap          a Map from DOM Elements (representing Node Properties) to BPEL
     *                                            variable names
     * @return a String containing a BPEL assign activity
     * @throws IOException is thrown when reading internal files fail
     */
    public String createAssignFromInstancePropertyToBPELVariableAsString(final String assignName,
                                                                         final String nodeInstancePropertyResponseVarName,
                                                                         final Map<String, String> propElement2BpelVarNameMap, String namespace) throws IOException {
        final String template = ResourceAccess.readResourceAsString(getClass().getClassLoader().getResource("core-bpel/BpelCopyFromPropertyVarToNodeInstanceProperty.xml"));

        StringBuilder assignString =
            new StringBuilder("<bpel:assign name=\"" + assignName + "\" xmlns:bpel=\"" + BPELPlan.bpelNamespace + "\" >");

        // <!-- $PropertyVarName, $NodeInstancePropertyRequestVarName,
        // $NodeInstancePropertyLocalName, $NodeInstancePropertyNamespace -->
        for (final String propElement : propElement2BpelVarNameMap.keySet()) {
            String copyString = template.replace("$PropertyVarName", propElement2BpelVarNameMap.get(propElement));
            copyString = copyString.replace("$NodeInstancePropertyRequestVarName", nodeInstancePropertyResponseVarName);
            copyString = copyString.replace("$NodeInstancePropertyLocalName", propElement);
            copyString = copyString.replace("$NodeInstancePropertyNamespace", namespace);
            assignString.append(copyString);
        }

        assignString.append("</bpel:assign>");

        return assignString.toString();
    }

    /**
     * Generates a BPEL4RESTLight extension activity that sets the instance state of the given
     * nodeInstance with the contents of the given string variable
     *
     * @param instanceURLVar the variable holding the url to the node instance
     * @param RequestVarName the variable to take the request body contents from
     * @return a String containing a single BPEL extension activity
     * @throws IOException is thrown when reading a internal file fails
     */
    public String generateBPEL4RESTLightPUTInstanceState(final String instanceURLVar,
                                                         final String RequestVarName) throws IOException {
        // BPEL4RESTLightPUT_NodeInstance_State_InstanceDataAPI.xml
        // <!-- $RequestVarName,$nodeInstanceURLVar -->
        final URL url = getClass().getClassLoader().getResource("instancedata-plugin/BPEL4RESTLightPUT_Instance_State_InstanceDataAPI.xml");
        String bpel4restString = ResourceAccess.readResourceAsString(url);

        bpel4restString = bpel4restString.replace("$instanceURLVar", instanceURLVar);
        bpel4restString = bpel4restString.replace("$RequestVarName", RequestVarName);

        return bpel4restString;
    }

    public String generateBPEL4RESTLightGETInstanceState(final String instanceURLVar,
                                                         final String ResponseVarName) throws IOException {
        // BPEL4RESTLightPUT_NodeInstance_State_InstanceDataAPI.xml
        // <!-- $RequestVarName,$nodeInstanceURLVar -->
        final URL url = getClass().getClassLoader().getResource("instancedata-plugin/BPEL4RESTLightGET_Instance_State_InstanceDataAPI.xml");
        String bpel4restString = ResourceAccess.readResourceAsString(url);

        bpel4restString = bpel4restString.replace("$instanceURLVar", instanceURLVar);
        bpel4restString = bpel4restString.replace("$ResponseVarName", ResponseVarName);

        return bpel4restString;
    }

    public Node generateBPEL4RESTLightGETInstanceStateAsNode(final String instanceURLVar,
                                                             final String ResponseVarName) throws SAXException, IOException {
        return this.toDom(this.generateBPEL4RESTLightGETInstanceState(instanceURLVar, ResponseVarName));
    }

    private Node toDom(String templateString) throws SAXException, IOException {
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    /**
     * Generates a String containing a BPEL assign that reads the value of a NodeInstance create
     * response and writes it into the referenced string variable
     *
     * @param nodeInstanceURLVarName the string variable to write the data into
     * @param nodeInstancePOSTResponseVarName the response variable of a nodeInstance create POST
     * @return a String containing a BPEL assign
     * @throws IOException is thrown when reading a internal file fails
     */
    public String generateAssignFromNodeInstancePOSTResponseToStringVar(final String nodeInstanceURLVarName,
                                                                        final String nodeInstanceIDVarName,
                                                                        final String nodeInstancePOSTResponseVarName) throws IOException {
        // BPELAssignFromNodeInstancePOSTResponseToStringVar.xml
        // <!-- $stringVarName, $NodeInstanceResponseVarName -->
        final URL url = getClass().getClassLoader().getResource("instancedata-plugin/BPELAssignFromNodeInstancePOSTResponseToStringVar.xml");
        String bpelAssignString = ResourceAccess.readResourceAsString(url);

        bpelAssignString = bpelAssignString.replaceAll("\\$stringVarName", nodeInstanceURLVarName);
        bpelAssignString =
            bpelAssignString.replaceAll("\\$NodeInstanceResponseVarName", nodeInstancePOSTResponseVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$nodeInstanceIDVar", nodeInstanceIDVarName);

        return bpelAssignString;
    }

    /**
     * Generates a String containing a BPEL assign that reads the value of a RelationInstance create
     * response and writes it into the referenced string variable
     *
     * @param stringVarName the string variable to write the data into
     * @param relationInstancePOSTResponseVarName the response variable of a relationInstance create
     *        POST
     * @return a String containing a BPEL assign
     * @throws IOException is thrown when reading a internal file fails
     */
    public String generateAssignFromRelationInstancePOSTResponseToStringVar(final String stringVarName,
                                                                            final String relationInstanceIDVar,
                                                                            final String relationInstancePOSTResponseVarName) throws IOException {
        // BPELAssignFromNodeInstancePOSTResponseToStringVar.xml
        // <!-- $stringVarName, $RelationInstanceResponseVarName, relationInstanceIDVar-->
        final URL url = getClass().getClassLoader().getResource("instancedata-plugin/BPELAssignFromRelationInstancePOSTResponseToStringVar.xml");
        String bpelAssignString = ResourceAccess.readResourceAsString(url);

        bpelAssignString = bpelAssignString.replaceAll("\\$stringVarName", stringVarName);
        bpelAssignString =
            bpelAssignString.replaceAll("\\$RelationInstanceResponseVarName", relationInstancePOSTResponseVarName);
        bpelAssignString = bpelAssignString.replaceAll("\\$relationInstanceIDVar", relationInstanceIDVar);

        return bpelAssignString;
    }

    /**
     * Generates a String containing a BPEL4RESTLight extension activity which create a nodeTemplate
     * instance on the given serviceTemplate instance
     *
     * @param serviceInstanceURLVar the variable holding the serviceInstanceUrl
     * @param nodeTemplateId the id of the nodeTemplate to instantiate
     * @param responseVariableName the variable to store the response into
     * @return a String containing a BPEL extension activity
     * @throws IOException is thrown when reading the internal file fails
     */
    public String generateBPEL4RESTLightNodeInstancePOST(final String serviceInstanceURLVar,
                                                         final String serviceInstanceIDVarName,
                                                         final String nodeTemplateId,
                                                         final String responseVariableName) throws IOException {
        // <!-- $serviceInstanceURLVar, $nodeTemplateId, $ResponseVarName -->
        final URL url = getClass().getClassLoader().getResource("instancedata-plugin/BPEL4RESTLightPOST_NodeInstance_InstanceDataAPI.xml");
        String bpel4RestString = ResourceAccess.readResourceAsString(url);

        bpel4RestString = bpel4RestString.replaceAll("\\$serviceInstanceURLVar", serviceInstanceURLVar);
        bpel4RestString = bpel4RestString.replaceAll("\\$serviceInstanceIDVar", serviceInstanceIDVarName);
        bpel4RestString = bpel4RestString.replaceAll("\\$nodeTemplateId", nodeTemplateId);
        bpel4RestString = bpel4RestString.replaceAll("\\$ResponseVarName", responseVariableName);

        return bpel4RestString;
    }

    /**
     * Generates a String containing a BPEL4RESTLight extension activity which creates a Relationship
     * Template instance on the given Service Template instance
     *
     * @param serviceInstanceURLVar the variable holding the serviceInstanceUrl
     * @param relationshipTemplateId the id of the Relationship Template to instantiate
     * @param responseVariableName the variable to store the response into
     * @param sourceInstanceIdVarName the variable name of the instance Id of the source node instance
     *        of the relation to be created
     * @param targetInstanceIdVarName the variable name of the instance Id of the target node instance
     *        of the relation to be created
     * @return a String containing a BPEL extension activity
     * @throws IOException is thrown when reading the internal file fails
     */
    public String generateBPEL4RESTLightRelationInstancePOST(final String serviceInstanceURLVar,
                                                             final String relationshipTemplateId,
                                                             final String requestVariableName,
                                                             final String responseVariableName,
                                                             final String sourceInstanceIdVarName,
                                                             final String targetInstanceIdVarName,
                                                             final String serviceInstanceIdVarName) throws IOException {
        // <!-- $serviceInstanceURLVar, $nodeTemplateId, $ResponseVarName -->
        final URL url = getClass().getClassLoader().getResource("instancedata-plugin/BPEL4RESTLightPOST_RelationInstance_InstanceDataAPI.xml");
        String bpel4RestString = ResourceAccess.readResourceAsString(url);

        bpel4RestString = bpel4RestString.replaceAll("\\$serviceInstanceURLVar", serviceInstanceURLVar);
        bpel4RestString = bpel4RestString.replaceAll("\\$relationshipTemplateId", relationshipTemplateId);
        bpel4RestString = bpel4RestString.replaceAll("\\$RequestVarName", requestVariableName);
        bpel4RestString = bpel4RestString.replaceAll("\\$ResponseVarName", responseVariableName);
        bpel4RestString = bpel4RestString.replaceAll("\\$sourceInstanceIdVarName", sourceInstanceIdVarName);
        bpel4RestString = bpel4RestString.replaceAll("\\$targetInstanceIdVarName", targetInstanceIdVarName);
        bpel4RestString = bpel4RestString.replaceAll("\\$serviceInstanceIdVarName", serviceInstanceIdVarName);

        return bpel4RestString;
    }

    public String generateInstancePropertiesGET(final String instanceUrlVarName,
                                                final String bpel4RestLightResponseVarName) throws IOException {
        final URL url = getClass().getClassLoader().getResource("instancedata-plugin/BPEL4RESTLightGET_Instance_Properties.xml");
        String bpel4restLightGETString = ResourceAccess.readResourceAsString(url);
        // <!-- $urlVarName, $ResponseVarName -->
        bpel4restLightGETString = bpel4restLightGETString.replace("$urlVarName", instanceUrlVarName);
        bpel4restLightGETString = bpel4restLightGETString.replace("$ResponseVarName", bpel4RestLightResponseVarName);
        return bpel4restLightGETString;
    }

    public Node generateInstancePropertiesGETAsNode(final String instanceUrlVarName,
                                                    final String bpel4RestLightResponseVarName) throws SAXException,
        IOException {
        final String templateString = generateInstancePropertiesGET(instanceUrlVarName, bpel4RestLightResponseVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String generateCopyFromStringVarToAnyTypeVar(final String propertyVarName,
                                                        final String nodeInstancePropertyRequestVarName,
                                                        final String nodeInstancePropertyLocalName,
                                                        final String nodeInstancePropertyNamespace) throws IOException {
        final URL url = getClass().getClassLoader().getResource("instancedata-plugin/BpelCopyFromPropertyVarToNodeInstanceProperty.xml");
        String bpelAssignString = ResourceAccess.readResourceAsString(url);
        // <!-- $PropertyVarName, $NodeInstancePropertyRequestVarName,
        // $NodeInstancePropertyLocalName, $NodeInstancePropertyNamespace -->
        bpelAssignString = bpelAssignString.replace("$PropertyVarName", propertyVarName);
        bpelAssignString =
            bpelAssignString.replace("$NodeInstancePropertyRequestVarName", nodeInstancePropertyRequestVarName);
        bpelAssignString = bpelAssignString.replace("$NodeInstancePropertyLocalName", nodeInstancePropertyLocalName);
        bpelAssignString = bpelAssignString.replace("$NodeInstancePropertyNamespace", nodeInstancePropertyNamespace);
        return bpelAssignString;
    }

    public Node generateCopyFromStringVarToAnyTypeVarAsNode(final String propertyVarName,
                                                            final String nodeInstancePropertyRequestVarName,
                                                            final String nodeInstancePropertyLocalName,
                                                            final String nodeInstancePropertyNamespace) throws IOException,
        SAXException {
        final String templateString =
            generateCopyFromStringVarToAnyTypeVar(propertyVarName, nodeInstancePropertyRequestVarName,
                nodeInstancePropertyLocalName, nodeInstancePropertyNamespace);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public Node generateAssignFromPropertyVarToDomMapping(final String nodeInstancePropertyRequestVarName,
                                                          final Map<String, QName> propertyVarToDomMapping) throws SAXException,
        IOException {
        // create empty bpel:assign
        final String bpelAssignString =
            "<bpel:assign xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"assignPropertyVarsToAnyElement"
                + System.currentTimeMillis() + "\" />";
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(bpelAssignString));
        final Document doc = this.docBuilder.parse(is);

        final Node assignNode = doc.getFirstChild();
        for (final String propertyVarName : propertyVarToDomMapping.keySet()) {
            final QName propertyId = propertyVarToDomMapping.get(propertyVarName);
            if (propertyVarName != null & propertyId != null) {

                Node copyNode =
                    generateCopyFromStringVarToAnyTypeVarAsNode(propertyVarName, nodeInstancePropertyRequestVarName,
                        propertyId.getLocalPart(),
                        propertyId.getNamespaceURI());

                copyNode = doc.importNode(copyNode, true);
                assignNode.appendChild(copyNode);
            }
        }

        return assignNode;
    }

    public String generateInstancesBPEL4RESTLightPUT(final String requestVarName,
                                                     final String instanceURLVarName) throws IOException {
        final URL url = getClass().getClassLoader().getResource("instancedata-plugin/BPEL4RESTLightPUT_Instance_InstanceDataAPI.xml");
        String bpel4RESTLightPut = ResourceAccess.readResourceAsString(url);

        // <!-- $RequestVarName,$nodeInstanceURLVar -->
        bpel4RESTLightPut = bpel4RESTLightPut.replace("$RequestVarName", requestVarName);
        bpel4RESTLightPut = bpel4RESTLightPut.replace("$instanceURLVar", instanceURLVarName);
        return bpel4RESTLightPut;
    }

    public Node generateInstancesBPEL4RESTLightPUTAsNode(final String requestVarName,
                                                         final String instanceURLVarName) throws IOException,
        SAXException {
        final String templateString = generateInstancesBPEL4RESTLightPUT(requestVarName, instanceURLVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }
}
