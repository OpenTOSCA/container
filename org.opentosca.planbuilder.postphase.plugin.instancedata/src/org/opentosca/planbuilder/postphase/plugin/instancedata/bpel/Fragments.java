/**
 *
 */
package org.opentosca.planbuilder.postphase.plugin.instancedata.bpel;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.FrameworkUtil;
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

    private final DocumentBuilderFactory docFactory;
    private final DocumentBuilder docBuilder;


    /**
     * Constructor
     *
     * @throws ParserConfigurationException is thrown when initializing the internal DocumentBuild fails
     */
    public Fragments() throws ParserConfigurationException {
        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docFactory.setNamespaceAware(true);
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    /**
     * Creates a BPEL4RESTLight DELETE Activity with the given BPELVar as Url to request on.
     *
     * @param bpelVarName the variable containing an URL
     * @param responseVarName the variable to hold the response
     * @return a String containing a BPEL4RESTLight Activity
     * @throws IOException is thrown when reading internal files fails
     */
    public String createRESTDeleteOnURLBPELVarAsString(final String bpelVarName, final String responseVarName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BPEL4RESTLightDELETE.xml");
        // <!-- $urlVarName, $ResponseVarName -->

        final File bpelFragmentFile = new File(FileLocator.toFileURL(url).getPath());

        String template = FileUtils.readFileToString(bpelFragmentFile);
        template = template.replace("$urlVarName", bpelVarName);
        template = template.replace("$ResponseVarName", responseVarName);

        return template;
    }

    /**
     * Creates a BPEL4RESTLight DELETE Activity with the given BPELVar as Url to request on.
     *
     * @param bpelVarName the variable containing an URL
     * @param responseVarName the variable to hold the response
     * @return a String containing a BPEL4RESTLight Activity
     * @throws IOException is thrown when reading internal files fails
     * @throws SAXException is thrown when parsing internal files fails
     */
    public Node createRESTDeleteOnURLBPELVarAsNode(final String bpelVarName, final String responseVarName)
        throws IOException, SAXException {
        final String templateString = this.createRESTDeleteOnURLBPELVarAsString(bpelVarName, responseVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
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
    public String generateBPEL4RESTLightPUTInstanceState(final String instanceURLVar, final String RequestVarName)
        throws IOException {
        // BPEL4RESTLightPUT_NodeInstance_State_InstanceDataAPI.xml
        // <!-- $RequestVarName,$nodeInstanceURLVar -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BPEL4RESTLightPUT_Instance_State_InstanceDataAPI.xml");
        final File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
        String bpel4restString = FileUtils.readFileToString(bpel4RestFile);

        bpel4restString = bpel4restString.replace("$instanceURLVar", instanceURLVar);
        bpel4restString = bpel4restString.replace("$RequestVarName", RequestVarName);

        return bpel4restString;
    }

    /**
     * Generates a String containing a BPEL assign that reads the value of a NodeInstance create
     * response and writes it into the referenced string variable
     *
     * @param stringVarName the string variable to write the data into
     * @param nodeInstancePOSTResponseVarName the response variable of a nodeInstance create POST
     * @return a String containing a BPEL assign
     * @throws IOException is thrown when reading a internal file fails
     */
    public String generateAssignFromNodeInstancePOSTResponseToStringVar(final String stringVarName,
                    final String nodeInstancePOSTResponseVarName)
        throws IOException {
        // BPELAssignFromNodeInstancePOSTResponseToStringVar.xml
        // <!-- $stringVarName, $NodeInstanceResponseVarName -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BPELAssignFromNodeInstancePOSTResponseToStringVar.xml");
        final File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelAssignString = FileUtils.readFileToString(bpel4RestFile);

        bpelAssignString = bpelAssignString.replace("$stringVarName", stringVarName);
        bpelAssignString = bpelAssignString.replace("$NodeInstanceResponseVarName", nodeInstancePOSTResponseVarName);

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
                    final String relationInstancePOSTResponseVarName)
        throws IOException {
        // BPELAssignFromNodeInstancePOSTResponseToStringVar.xml
        // <!-- $stringVarName, $NodeInstanceResponseVarName -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BPELAssignFromRelationInstancePOSTResponseToStringVar.xml");
        final File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelAssignString = FileUtils.readFileToString(bpel4RestFile);

        bpelAssignString = bpelAssignString.replace("$stringVarName", stringVarName);
        bpelAssignString = bpelAssignString.replace("$RelationInstanceResponseVarName",
            relationInstancePOSTResponseVarName);

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
                    final String nodeTemplateId, final String responseVariableName)
        throws IOException {
        // <!-- $serviceInstanceURLVar, $nodeTemplateId, $ResponseVarName -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BPEL4RESTLightPOST_NodeInstance_InstanceDataAPI.xml");
        final File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
        String bpel4RestString = FileUtils.readFileToString(bpel4RestFile);

        bpel4RestString = bpel4RestString.replace("$serviceInstanceURLVar", serviceInstanceURLVar);
        bpel4RestString = bpel4RestString.replace("$nodeTemplateId", nodeTemplateId);
        bpel4RestString = bpel4RestString.replace("$ResponseVarName", responseVariableName);

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
                    final String relationshipTemplateId, final String responseVariableName,
                    final String sourceInstanceIdVarName, final String targetInstanceIdVarName)
        throws IOException {
        // <!-- $serviceInstanceURLVar, $nodeTemplateId, $ResponseVarName -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BPEL4RESTLightPOST_RelationInstance_InstanceDataAPI.xml");
        final File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
        String bpel4RestString = FileUtils.readFileToString(bpel4RestFile);

        bpel4RestString = bpel4RestString.replace("$serviceInstanceURLVar", serviceInstanceURLVar);
        bpel4RestString = bpel4RestString.replace("$relationshipTemplateId", relationshipTemplateId);
        bpel4RestString = bpel4RestString.replace("$ResponseVarName", responseVariableName);
        bpel4RestString = bpel4RestString.replace("$sourceInstanceIdVarName", sourceInstanceIdVarName);
        bpel4RestString = bpel4RestString.replace("$targetInstanceIdVarName", targetInstanceIdVarName);

        return bpel4RestString;
    }

    public String generateBPEL4RESTLightServiceInstancePOST(final String instanceDataAPIUrlVariableName,
                    final String csarId, final QName serviceTemplateId, final String responseVariableName)
        throws IOException {
        // tags in xml snippet: $InstanceDataURLVar, $CSARName,
        // $serviceTemplateId, $ResponseVarName
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BPEL4RESTLightPOST_ServiceInstance_InstanceDataAPI.xml");
        final File bpel4RestFile = new File(FileLocator.toFileURL(url).getPath());
        String bpel4RestString = FileUtils.readFileToString(bpel4RestFile);

        bpel4RestString = bpel4RestString.replace("$InstanceDataURLVar", instanceDataAPIUrlVariableName);
        bpel4RestString = bpel4RestString.replace("$CSARName", csarId);
        bpel4RestString = bpel4RestString.replace("$serviceTemplateId", serviceTemplateId.toString());
        bpel4RestString = bpel4RestString.replace("$ResponseVarName", responseVariableName);

        return bpel4RestString;
    }

    public Node generateBPEL4RESTLightServiceInstancePOSTAsNode(final String instanceDataAPIUrlVariableName,
                    final String csarId, final QName serviceTemplateId, final String responseVariableName)
        throws IOException, SAXException {
        final String templateString = this.generateBPEL4RESTLightServiceInstancePOST(instanceDataAPIUrlVariableName,
            csarId, serviceTemplateId, responseVariableName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String generateServiceInstanceURLVarAssign(final String serviceInstanceResponseVarName,
                    final String serviceInstanceURLVarName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BpelAssignServiceInstancePOSTResponse.xml");
        final File bpelAssigntFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelAssignString = FileUtils.readFileToString(bpelAssigntFile);
        // <!-- $assignName $ServiceInstanceResponseVarName
        // $ServiceInstanceURLVarName-->

        bpelAssignString = bpelAssignString.replace("$assignName",
            "assignServiceInstance" + System.currentTimeMillis());
        bpelAssignString = bpelAssignString.replace("$ServiceInstanceResponseVarName", serviceInstanceResponseVarName);
        bpelAssignString = bpelAssignString.replace("$ServiceInstanceURLVarName", serviceInstanceURLVarName);
        return bpelAssignString;
    }

    public Node generateServiceInstanceURLVarAssignAsNode(final String serviceInstanceResponseVarName,
                    final String serviceInstanceURLVarName)
        throws IOException, SAXException {
        final String templateString = this.generateServiceInstanceURLVarAssign(serviceInstanceResponseVarName,
            serviceInstanceURLVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public Node generateNodeInstancesQueryGETasNode(final String instanceDataUrlVarName, final String responseVarName,
                    final QName nodeType)
        throws IOException, SAXException {
        final String templateString = this.generateNodeInstancePropertiesGET(instanceDataUrlVarName, responseVarName,
            nodeType);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String generateInstancePropertiesGET(final String instanceUrlVarName,
                    final String bpel4RestLightResponseVarName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BPEL4RESTLightGET_Instance_Properties.xml");
        final File bpel4restLightGETFile = new File(FileLocator.toFileURL(url).getPath());
        String bpel4restLightGETString = FileUtils.readFileToString(bpel4restLightGETFile);
        // <!-- $urlVarName, $ResponseVarName -->
        bpel4restLightGETString = bpel4restLightGETString.replace("$urlVarName", instanceUrlVarName);
        bpel4restLightGETString = bpel4restLightGETString.replace("$ResponseVarName", bpel4RestLightResponseVarName);
        return bpel4restLightGETString;
    }

    public Node generateInstancePropertiesGETAsNode(final String instanceUrlVarName,
                    final String bpel4RestLightResponseVarName)
        throws SAXException, IOException {
        final String templateString = this.generateInstancePropertiesGET(instanceUrlVarName,
            bpel4RestLightResponseVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String generateAssignFromNodeInstanceResonseToStringVar(final String stringVarName,
                    final String nodeInstanceResponseVarName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BpelAssignFromNodeInstanceRequestToStringVar.xml");
        final File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);
        // <!-- $stringVarName, $NodeInstanceResponseVarName -->
        bpelAssignString = bpelAssignString.replace("$stringVarName", stringVarName);
        bpelAssignString = bpelAssignString.replace("$NodeInstanceResponseVarName", nodeInstanceResponseVarName);
        return bpelAssignString;
    }

    public Node generateAssignFromNodeInstanceResponseToStringVarAsNode(final String stringVarName,
                    final String nodeInstanceResponseVarName)
        throws IOException, SAXException {
        final String templateString = this.generateAssignFromNodeInstanceResonseToStringVar(stringVarName,
            nodeInstanceResponseVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String generateNodeInstancePropertiesGET(final String instanceDataUrlVarName, final String responseVarName,
                    final QName nodeType)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BPEL4RESTLightGET_NodeInstance_InstanceDataAPI.xml");
        final File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);

        // $InstanceDataURLVar, $ResponseVarName, $nodeType

        bpelAssignString = bpelAssignString.replace("$InstanceDataURLVar", instanceDataUrlVarName);
        bpelAssignString = bpelAssignString.replace("$ResponseVarName", responseVarName);
        bpelAssignString = bpelAssignString.replace("$nodeType", nodeType.toString());
        return bpelAssignString;
    }

    public String generateServiceInstanceRequestToStringVarAssign(final String stringVarName,
                    final String serviceInstanceResponseVarName, final int nodeInstanceIndex)
        throws IOException {
        // <!-- $stringVarName, $ServiceInstanceResponseVarName,
        // $nodeInstanceIndex -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BpelAssignFromServiceInstanceRequestToStringVar.xml");
        final File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);

        bpelAssignString = bpelAssignString.replace("$stringVarName", stringVarName);
        bpelAssignString = bpelAssignString.replace("$ServiceInstanceResponseVarName", serviceInstanceResponseVarName);
        bpelAssignString = bpelAssignString.replace("$nodeInstanceIndex", String.valueOf(nodeInstanceIndex));

        return bpelAssignString;
    }

    public Node generateServiceInstanceRequestToStringVarAssignAsNode(final String stringVarName,
                    final String serviceInstanceResponseVarName, final int nodeInstanceIndex)
        throws IOException, SAXException {
        final String templateString = this.generateServiceInstanceRequestToStringVarAssign(stringVarName,
            serviceInstanceResponseVarName, nodeInstanceIndex);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String generateBPEL4RESTLightGET(final String urlVarName, final String responseVarName) throws IOException {
        // BPEL4RESTLightGET_ServiceInstance_InstanceDataAPI.xml
        // <!-- $serviceInstanceUrlVarName, $ResponseVarName -->
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BPEL4RESTLightGET.xml");
        final File bpelServiceInstanceGETFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelServiceInstanceGETString = FileUtils.readFileToString(bpelServiceInstanceGETFile);

        bpelServiceInstanceGETString = bpelServiceInstanceGETString.replace("$urlVarName", urlVarName);
        bpelServiceInstanceGETString = bpelServiceInstanceGETString.replace("$ResponseVarName", responseVarName);
        return bpelServiceInstanceGETString;
    }

    public Node generateBPEL4RESTLightGETAsNode(final String serviceInstanceUrlVarName, final String responseVarName)
        throws IOException, SAXException {
        final String templateString = this.generateBPEL4RESTLightGET(serviceInstanceUrlVarName, responseVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String generateAssignFromInputMessageToStringVariable(final String inputMessageElementLocalName,
                    final String stringVariableName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BpelAssignFromInputToStringVar.xml");
        final File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);
        // <!-- $inputElementLocalName, $StringVariableName, $assignName -->
        bpelAssignString = bpelAssignString.replace("$inputElementLocalName", inputMessageElementLocalName);
        bpelAssignString = bpelAssignString.replace("$StringVariableName", stringVariableName);
        bpelAssignString = bpelAssignString.replace("$assignName",
            "assignFromInputToString" + System.currentTimeMillis());
        return bpelAssignString;
    }

    public Node generateAssignFromInputMessageToStringVariableAsNode(final String inputMessageElementLocalName,
                    final String stringVariableName)
        throws IOException, SAXException {
        final String templateString = this.generateAssignFromInputMessageToStringVariable(inputMessageElementLocalName,
            stringVariableName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public String generateCopyFromStringVarToAnyTypeVar(final String propertyVarName,
                    final String nodeInstancePropertyRequestVarName, final String nodeInstancePropertyLocalName,
                    final String nodeInstancePropertyNamespace)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BpelCopyFromPropertyVarToNodeInstanceProperty.xml");
        final File bpelAssignFile = new File(FileLocator.toFileURL(url).getPath());
        String bpelAssignString = FileUtils.readFileToString(bpelAssignFile);
        // <!-- $PropertyVarName, $NodeInstancePropertyRequestVarName,
        // $NodeInstancePropertyLocalName, $NodeInstancePropertyNamespace -->
        bpelAssignString = bpelAssignString.replace("$PropertyVarName", propertyVarName);
        bpelAssignString = bpelAssignString.replace("$NodeInstancePropertyRequestVarName",
            nodeInstancePropertyRequestVarName);
        bpelAssignString = bpelAssignString.replace("$NodeInstancePropertyLocalName", nodeInstancePropertyLocalName);
        bpelAssignString = bpelAssignString.replace("$NodeInstancePropertyNamespace", nodeInstancePropertyNamespace);
        return bpelAssignString;
    }

    public Node generateCopyFromStringVarToAnyTypeVarAsNode(final String propertyVarName,
                    final String nodeInstancePropertyRequestVarName, final String nodeInstancePropertyLocalName,
                    final String nodeInstancePropertyNamespace)
        throws IOException, SAXException {
        final String templateString = this.generateCopyFromStringVarToAnyTypeVar(propertyVarName,
            nodeInstancePropertyRequestVarName, nodeInstancePropertyLocalName, nodeInstancePropertyNamespace);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    public Node generateAssignFromPropertyVarToDomMapping(final String nodeInstancePropertyRequestVarName,
                    final Map<String, Node> propertyVarToDomMapping)
        throws SAXException, IOException {
        // create empty bpel:assign
        final String bpelAssignString = "<bpel:assign xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"assignPropertyVarsToAnyElement"
            + System.currentTimeMillis() + "\" />";
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(bpelAssignString));
        final Document doc = this.docBuilder.parse(is);

        final Node assignNode = doc.getFirstChild();
        for (final String propertyVarName : propertyVarToDomMapping.keySet()) {
            final Node propertyNode = propertyVarToDomMapping.get(propertyVarName);
            Node copyNode = this.generateCopyFromStringVarToAnyTypeVarAsNode(propertyVarName,
                nodeInstancePropertyRequestVarName, propertyNode.getLocalName(), propertyNode.getNamespaceURI());

            copyNode = doc.importNode(copyNode, true);
            assignNode.appendChild(copyNode);
        }

        return assignNode;
    }

    public String generateInstancesBPEL4RESTLightPUT(final String requestVarName, final String instanceURLVarName)
        throws IOException {
        final URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
                                     .getResource("BPEL4RESTLightPUT_Instance_InstanceDataAPI.xml");
        final File bpel4RESTLightPUTFile = new File(FileLocator.toFileURL(url).getPath());
        String bpel4RESTLightPut = FileUtils.readFileToString(bpel4RESTLightPUTFile);

        // <!-- $RequestVarName,$nodeInstanceURLVar -->
        bpel4RESTLightPut = bpel4RESTLightPut.replace("$RequestVarName", requestVarName);
        bpel4RESTLightPut = bpel4RESTLightPut.replace("$instanceURLVar", instanceURLVarName);
        return bpel4RESTLightPut;
    }

    public Node generateInstancesBPEL4RESTLightPUTAsNode(final String requestVarName, final String instanceURLVarName)
        throws IOException, SAXException {
        final String templateString = this.generateInstancesBPEL4RESTLightPUT(requestVarName, instanceURLVarName);
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(templateString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

}
