package org.opentosca.container.api.legacy.resources.smartservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.container.api.legacy.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.container.api.legacy.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.container.api.legacy.osgi.servicegetter.ModelRepositoryServiceHandler;
import org.opentosca.container.api.legacy.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.service.IInstanceDataService;
import org.opentosca.container.core.tosca.model.TExtensibleElements;
import org.opentosca.container.core.tosca.model.TServiceTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
@Path("/SmartServices")
public class SmartServicesResource {

    @Context
    UriInfo uriInfo;

    private final ICoreFileService fileHandler;


    public SmartServicesResource() {
        this.fileHandler = FileRepositoryServiceHandler.getFileHandler();
    }

    /**
     * Returns links to subresources as JSON
     *
     * @return a list of links to templates and instances of Smart Services
     */
    @GET
    @Produces(ResourceConstants.LINKED_JSON)
    public Response getJSONLinks() {
        final References refs = new References();
        refs.getReference()
            .add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "templates"),
                XLinkConstants.SIMPLE, "SmartServiceTemplates"));
        refs.getReference()
            .add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "instances"),
                XLinkConstants.SIMPLE, "SmartServiceInstances"));
        return Response.ok(refs.getJSONString()).build();
    }

    /**
     * Returns links to subresources as XML
     *
     * @return a list of links to templates and instances of Smart Services
     */
    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getXMLLinks() {
        final References refs = new References();
        refs.getReference()
            .add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "templates"),
                XLinkConstants.SIMPLE, "SmartServiceTemplates"));
        refs.getReference()
            .add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "instances"),
                XLinkConstants.SIMPLE, "SmartServiceInstances"));
        return Response.ok(refs.getXMLString()).build();
    }

    /**
     * Returns a list of links to Smart Services inside the model API as JSON
     *
     * @return a list of links to Smart Service templates inside the model API
     */
    @GET
    @Path("/templates")
    @Produces(ResourceConstants.LINKED_JSON)
    public Response getTemplateJSONLinks() {
        final References refs = this.getSmartServiceTemplatesReferences();
        return Response.ok(refs.getJSONString()).build();
    }

    /**
     * Returns a list of links to Smart Services inside the model API as XML
     *
     * @return a list of links to Smart Service templates inside the model API
     */
    @GET
    @Path("/templates")
    @Produces(ResourceConstants.LINKED_XML)
    public Response getTemplateXMLLinks() {
        final References refs = this.getSmartServiceTemplatesReferences();
        return Response.ok(refs.getXMLString()).build();
    }

    @GET
    @Path("/instances")
    @Produces(ResourceConstants.LINKED_JSON)
    public Response getInstancesJSON() {

        final JsonObject root = new JsonObject();

        final JsonObject runningSmartServices = new JsonObject();

        final JsonArray sensorDataProviderServices = new JsonArray();
        final JsonArray uiProviderServices = new JsonArray();

        try {
            final List<ServiceInstance> instances = this.getSmartServiceInstances();

            for (final ServiceInstance instance : instances) {
                final Map<String, String> instanceProperties = this.getSmartServiceInstanceData(instance);

                if (instanceProperties.containsKey("smartServiceType")) {
                    switch (instanceProperties.get("smartServiceType")) {
                        case "SensorDataProviderService":
                            sensorDataProviderServices.add(this.createJSON(instanceProperties));
                            break;
                        case "UIProviderService":
                            uiProviderServices.add(this.createJSON(instanceProperties));
                            break;
                    }
                }

            }

        }
        catch (final UserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        runningSmartServices.add("SensorDataProviderServices", sensorDataProviderServices);
        runningSmartServices.add("UIProviderServices", uiProviderServices);

        root.add("RunningSmartService", runningSmartServices);

        return Response.ok(root.toString()).build();
    }

    @GET
    @Path("/instances")
    @Produces(ResourceConstants.LINKED_XML)
    public Response getInstancesXML() {
        String rootElementString = "<RunningSmartServices>";

        String sensorDataProviderServicesElementString = "<SensorDataProviderServices>";
        String uiProviderServicesElementString = "<UiProviderServices>";

        List<ServiceInstance> instances;
        try {
            instances = this.getSmartServiceInstances();

            for (final ServiceInstance instance : instances) {
                final Map<String, String> instanceProperties = this.getSmartServiceInstanceData(instance);

                if (instanceProperties.containsKey("smartServiceType")) {
                    switch (instanceProperties.get("smartServiceType")) {
                        case "SensorDataProviderService":
                            sensorDataProviderServicesElementString +=
                                this.createServiceElementAsString(instanceProperties);
                            break;
                        case "UIProviderService":
                            uiProviderServicesElementString += this.createServiceElementAsString(instanceProperties);
                            break;
                    }
                }
            }
        }
        catch (final UserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        sensorDataProviderServicesElementString += "</SensorDataProviderServices>";
        uiProviderServicesElementString += "</UiProviderServices>";

        rootElementString += sensorDataProviderServicesElementString;
        rootElementString += uiProviderServicesElementString;
        rootElementString += "</RunningSmartServices>";

        return Response.ok(rootElementString).build();
    }

    private String createServiceElementAsString(final Map<String, String> map) {

        String elementString = "<SmartService>";

        for (final String key : map.keySet()) {
            final String subElement = this.createElementAsString(key, map.get(key));
            elementString += subElement;
        }

        elementString += "</SmartService>";

        return elementString;
    }

    private String createElementAsString(final String localName, final String value) {
        final String stringElement = "<" + localName + ">" + value + "</" + localName + ">";
        return stringElement;
    }

    private JsonObject createJSON(final Map<String, String> map) {

        final JsonObject jsonObj = new JsonObject();

        for (final String key : map.keySet()) {
            jsonObj.addProperty(key, map.get(key));
        }

        return jsonObj;
    }

    private Map<String, String> getSmartServiceInstanceData(final ServiceInstance smartServiceInstance) throws UserException {
        final CSARID csarId = smartServiceInstance.getCSAR_ID();
        // we take all properties and add them with their values into the map.
        // Additionally we fetch data set in self-service meta-data
        final Map<String, String> kvMap = this.getKVProperties(smartServiceInstance.getProperties());

        final AbstractDirectory dir =
            FileRepositoryServiceHandler.getFileHandler().getCSAR(csarId).getDirectory("SELFSERVICE-Metadata");

        final AbstractFile file = dir.getFile("data.xml");

        final Map<String, String> selfServiceMap = this.getSelfServiceProperties(file);

        kvMap.putAll(selfServiceMap);

        kvMap.put("id", smartServiceInstance.getServiceInstanceID().toString());

        return kvMap;
    }

    private Map<String, String> getSelfServiceProperties(final AbstractFile file) {
        final Map<String, String> selfServiceProperties = new HashMap<>();

        try {
            final Document doc = Utilities.fileToDom(file.getFile().toFile());

            final Element rootElement = doc.getDocumentElement();

            final NodeList childNodes = rootElement.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i) instanceof Element) {
                    final Element element = (Element) childNodes.item(i);
                    // the elements are create with DOM level 1 methode
                    // getLocalName() is always null..
                    final String nodeName = element.getNodeName();
                    switch (nodeName) {
                        case "displayName":
                            selfServiceProperties.put("name", element.getNodeValue());
                            break;
                        case "description":
                            selfServiceProperties.put("description", element.getNodeValue());
                            break;
                        case "iconUrl":
                            if (element.getNodeValue() != null) {
                                selfServiceProperties.put("smartServiceIconURL",
                                                          Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(),
                                                                             "SELFSERVICE-Metadata/"
                                                                                 + element.getNodeValue()));
                            } else {
                                selfServiceProperties.put("smartServiceIconURL", null);
                            }
                            break;
                    }
                }
            }

        }
        catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final SystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return selfServiceProperties;
    }

    private Map<String, String> getKVProperties(final Document doc) {
        final Map<String, String> kvMap = new HashMap<>();

        final Element rootElement = doc.getDocumentElement();

        final NodeList childNodes = rootElement.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                final Element childElement = (Element) childNodes.item(i);

                final String key = childElement.getLocalName();
                final String val = childElement.getTextContent();

                kvMap.put(key, val);
            }
        }

        return kvMap;
    }

    private References getSmartServiceInstancesReferences() {
        final References refs = new References();

        try {
            for (final ServiceInstance instance : this.getSmartServiceInstances()) {
                refs.getReference().add(new Reference(instance.getServiceInstanceID().toString(), XLinkConstants.SIMPLE,
                    "smartServiceInstance"));
            }
        }
        catch (final UserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return refs;
    }

    private List<ServiceInstance> getSmartServiceInstances() throws UserException {
        final List<ServiceInstance> instances = new ArrayList<>();
        final Map<CSARID, QName> smartServiceTemplates = this.getSmartServiceTemplates();
        final IInstanceDataService instancesService = InstanceDataServiceHandler.getInstanceDataService();

        for (final CSARID csarId : smartServiceTemplates.keySet()) {

            // TODO/FIXME query with serviceTemplateId doesn't work
            final List<ServiceInstance> instanceList = instancesService.getServiceInstances(null, null, null);

            final List<ServiceInstance> toAdd = new ArrayList<>();

            for (final ServiceInstance instance : instanceList) {
                if (instance.getServiceTemplateID().equals(smartServiceTemplates.get(csarId))) {
                    toAdd.add(instance);
                }
            }

            if (!toAdd.isEmpty()) {
                instances.addAll(toAdd);
            }

        }

        return instances;
    }

    private References getSmartServiceTemplatesReferences() {
        final References refs = new References();

        try {
            final Map<CSARID, QName> smartServices = this.getSmartServiceTemplates();
            for (final CSARID csarId : smartServices.keySet()) {
                final Reference ref = new Reference(
                    Utilities.buildURI(this.uriInfo.getAbsolutePath().toString().replace("/SmartServices/templates",
                                                                                         "/CSARs/"),
                                       csarId.toString() + "/Content/"
                                           + ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper()
                                                                .getDefinitionsLocation(csarId,
                                                                                        smartServices.get(csarId))),
                    XLinkConstants.SIMPLE, smartServices.get(csarId).toString());
                refs.getReference().add(ref);
            }
        }
        catch (final UserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return refs;
        }

        return refs;
    }

    private Map<CSARID, QName> getSmartServiceTemplates() throws UserException {
        final Map<CSARID, QName> map = new HashMap<>();

        for (final CSARID csarId : this.fileHandler.getCSARIDs()) {
            final CSARContent content = this.fileHandler.getCSAR(csarId);
            final AbstractFile absFile = content.getRootTOSCA();

            for (final QName serviceTemplateId : ModelRepositoryServiceHandler.getModelHandler()
                                                                              .getAllDefinitionsIDs(csarId)) {
                final String location = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper()
                                                           .getDefinitionsLocation(csarId, serviceTemplateId);
                if (absFile.getPath().contains(location)) {
                    // found root TOSCA

                    for (final TExtensibleElements extElem : ModelRepositoryServiceHandler.getModelHandler()
                                                                                          .getDefinitions(csarId,
                                                                                                          serviceTemplateId)
                                                                                          .getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
                        if (extElem instanceof TServiceTemplate) {
                            final TServiceTemplate servTemplate = (TServiceTemplate) extElem;
                            final Object obj = servTemplate.getBoundaryDefinitions().getProperties().getAny();

                            final String defNs = ModelRepositoryServiceHandler.getModelHandler()
                                                                              .getDefinitions(csarId, serviceTemplateId)
                                                                              .getTargetNamespace();

                            final String localName = servTemplate.getId();
                            final String stNs = servTemplate.getTargetNamespace();

                            if (obj instanceof Element) {
                                try {
                                    if (this.containsSmartServiceProperties((Element) obj)) {
                                        if (stNs == null) {
                                            map.put(csarId, new QName(defNs, localName));
                                        } else {
                                            map.put(csarId, new QName(stNs, localName));
                                        }
                                    } else {
                                        // if this serviceTemplate isn't a smart
                                        // service we can skip the csar
                                        break;
                                    }
                                }
                                catch (final XPathExpressionException e) {
                                    e.printStackTrace();
                                    // skip
                                    break;
                                }
                            }

                        }
                    }
                }
            }

        }

        return map;
    }

    private boolean containsSmartServiceProperties(final Element element) throws XPathExpressionException {
        final XPath xpath = XPathFactory.newInstance().newXPath();
        final String xpathExprSmartServiceType = "//*[local-name()='smartServiceType']";
        final String xpathExprSmartServiceSensorDataType = "//*[local-name()='sensorDataType']";
        final String xpathExprSmartServiceSensorDataValueType = "//*[local-name()='sensorDataValueType']";

        final Node smartServiceTypeNode =
            (Node) xpath.evaluate(xpathExprSmartServiceType, element, XPathConstants.NODE);
        final Node smartServiceSensorDataTypeNode =
            (Node) xpath.evaluate(xpathExprSmartServiceSensorDataType, element, XPathConstants.NODE);
        final Node smartServiceSensorDataValueTypeNode =
            (Node) xpath.evaluate(xpathExprSmartServiceSensorDataValueType, element, XPathConstants.NODE);

        if (!Utilities.areNotNull(smartServiceTypeNode, smartServiceSensorDataTypeNode,
                                  smartServiceSensorDataValueTypeNode)) {
            return false;
        }

        return true;
    }
}
