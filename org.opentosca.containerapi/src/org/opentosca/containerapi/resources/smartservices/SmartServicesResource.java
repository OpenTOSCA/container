package org.opentosca.containerapi.resources.smartservices;

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

import org.opentosca.containerapi.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.ModelRepositoryServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.core.model.artifact.directory.AbstractDirectory;
import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.model.instancedata.ServiceInstance;
import org.opentosca.model.tosca.TExtensibleElements;
import org.opentosca.model.tosca.TServiceTemplate;
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
		References refs = new References();
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "templates"), XLinkConstants.SIMPLE, "SmartServiceTemplates"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "instances"), XLinkConstants.SIMPLE, "SmartServiceInstances"));
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
		References refs = new References();
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "templates"), XLinkConstants.SIMPLE, "SmartServiceTemplates"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "instances"), XLinkConstants.SIMPLE, "SmartServiceInstances"));
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
		References refs = this.getSmartServiceTemplatesReferences();
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
		References refs = this.getSmartServiceTemplatesReferences();
		return Response.ok(refs.getXMLString()).build();
	}

	@GET
	@Path("/instances")
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getInstancesJSON() {

		JsonObject root = new JsonObject();

		JsonObject runningSmartServices = new JsonObject();

		JsonArray sensorDataProviderServices = new JsonArray();
		JsonArray uiProviderServices = new JsonArray();

		try {
			List<ServiceInstance> instances = this.getSmartServiceInstances();

			for (ServiceInstance instance : instances) {
				Map<String, String> instanceProperties = this.getSmartServiceInstanceData(instance);

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

		} catch (UserException e) {
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

			for (ServiceInstance instance : instances) {
				Map<String, String> instanceProperties = this.getSmartServiceInstanceData(instance);

				if (instanceProperties.containsKey("smartServiceType")) {
					switch (instanceProperties.get("smartServiceType")) {
					case "SensorDataProviderService":
						sensorDataProviderServicesElementString += this.createServiceElementAsString(instanceProperties);
						break;
					case "UIProviderService":
						uiProviderServicesElementString += this.createServiceElementAsString(instanceProperties);
						break;
					}
				}
			}
		} catch (UserException e) {
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

	private String createServiceElementAsString(Map<String, String> map) {

		String elementString = "<SmartService>";

		for (String key : map.keySet()) {
			String subElement = this.createElementAsString(key, map.get(key));
			elementString += subElement;
		}

		elementString += "</SmartService>";

		return elementString;
	}

	private String createElementAsString(String localName, String value) {
		String stringElement = "<" + localName + ">" + value + "</" + localName + ">";
		return stringElement;
	}

	private JsonObject createJSON(Map<String, String> map) {

		JsonObject jsonObj = new JsonObject();

		for (String key : map.keySet()) {
			jsonObj.addProperty(key, map.get(key));
		}

		return jsonObj;
	}

	private Map<String, String> getSmartServiceInstanceData(ServiceInstance smartServiceInstance) throws UserException {
		CSARID csarId = smartServiceInstance.getCSAR_ID();
		// we take all properties and add them with their values into the map.
		// Additionally we fetch data set in self-service meta-data
		Map<String, String> kvMap = this.getKVProperties(smartServiceInstance.getProperties());

		AbstractDirectory dir = FileRepositoryServiceHandler.getFileHandler().getCSAR(csarId).getDirectory("SELFSERVICE-Metadata");

		AbstractFile file = dir.getFile("data.xml");

		Map<String, String> selfServiceMap = this.getSelfServiceProperties(file);

		kvMap.putAll(selfServiceMap);

		kvMap.put("id", smartServiceInstance.getServiceInstanceID().toString());

		return kvMap;
	}

	private Map<String, String> getSelfServiceProperties(AbstractFile file) {
		Map<String, String> selfServiceProperties = new HashMap<String, String>();

		try {
			Document doc = Utilities.fileToDom(file.getFile().toFile());

			Element rootElement = doc.getDocumentElement();

			NodeList childNodes = rootElement.getChildNodes();

			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i) instanceof Element) {
					Element element = (Element) childNodes.item(i);
					// the elements are create with DOM level 1 methode
					// getLocalName() is always null..
					String nodeName = element.getNodeName();
					switch (nodeName) {
					case "displayName":
						selfServiceProperties.put("name", element.getNodeValue());
						break;
					case "description":
						selfServiceProperties.put("description", element.getNodeValue());
						break;
					case "iconUrl":
						if (element.getNodeValue() != null) {
							selfServiceProperties.put("smartServiceIconURL", Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "SELFSERVICE-Metadata/" + element.getNodeValue()));
						} else {
							selfServiceProperties.put("smartServiceIconURL", null);
						}
						break;
					}
				}
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return selfServiceProperties;
	}

	private Map<String, String> getKVProperties(Document doc) {
		Map<String, String> kvMap = new HashMap<String, String>();

		Element rootElement = doc.getDocumentElement();

		NodeList childNodes = rootElement.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i) instanceof Element) {
				Element childElement = (Element) childNodes.item(i);

				String key = childElement.getLocalName();
				String val = childElement.getTextContent();

				kvMap.put(key, val);
			}
		}

		return kvMap;
	}

	private References getSmartServiceInstancesReferences() {
		References refs = new References();

		try {
			for (ServiceInstance instance : this.getSmartServiceInstances()) {
				refs.getReference().add(new Reference(instance.getServiceInstanceID().toString(), XLinkConstants.SIMPLE, "smartServiceInstance"));
			}
		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return refs;
	}

	private List<ServiceInstance> getSmartServiceInstances() throws UserException {
		List<ServiceInstance> instances = new ArrayList<ServiceInstance>();
		Map<CSARID, QName> smartServiceTemplates = this.getSmartServiceTemplates();
		IInstanceDataService instancesService = InstanceDataServiceHandler.getInstanceDataService();

		for (CSARID csarId : smartServiceTemplates.keySet()) {

			// TODO/FIXME query with serviceTemplateId doesn't work
			List<ServiceInstance> instanceList = instancesService.getServiceInstances(null, null, null);

			List<ServiceInstance> toAdd = new ArrayList<ServiceInstance>();

			for (ServiceInstance instance : instanceList) {
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
		References refs = new References();

		try {
			Map<CSARID, QName> smartServices = this.getSmartServiceTemplates();
			for (CSARID csarId : smartServices.keySet()) {
				Reference ref = new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString().replace("/SmartServices/templates", "/CSARs/"), csarId.toString() + "/Content/" + ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getDefinitionsLocation(csarId, smartServices.get(csarId))), XLinkConstants.SIMPLE, smartServices.get(csarId).toString());
				refs.getReference().add(ref);
			}
		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return refs;
		}

		return refs;
	}

	private Map<CSARID, QName> getSmartServiceTemplates() throws UserException {
		Map<CSARID, QName> map = new HashMap<CSARID, QName>();

		for (CSARID csarId : this.fileHandler.getCSARIDs()) {
			CSARContent content = this.fileHandler.getCSAR(csarId);
			AbstractFile absFile = content.getRootTOSCA();

			for (QName serviceTemplateId : ModelRepositoryServiceHandler.getModelHandler().getAllDefinitionsIDs(csarId)) {
				String location = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getDefinitionsLocation(csarId, serviceTemplateId);
				if (absFile.getPath().contains(location)) {
					// found root TOSCA

					for (TExtensibleElements extElem : ModelRepositoryServiceHandler.getModelHandler().getDefinitions(csarId, serviceTemplateId).getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
						if (extElem instanceof TServiceTemplate) {
							TServiceTemplate servTemplate = (TServiceTemplate) extElem;
							Object obj = servTemplate.getBoundaryDefinitions().getProperties().getAny();

							String defNs = ModelRepositoryServiceHandler.getModelHandler().getDefinitions(csarId, serviceTemplateId).getTargetNamespace();

							String localName = servTemplate.getId();
							String stNs = servTemplate.getTargetNamespace();

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
								} catch (XPathExpressionException e) {
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

	private boolean containsSmartServiceProperties(Element element) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		final String xpathExprSmartServiceType = "//*[local-name()='smartServiceType']";
		final String xpathExprSmartServiceSensorDataType = "//*[local-name()='sensorDataType']";
		final String xpathExprSmartServiceSensorDataValueType = "//*[local-name()='sensorDataValueType']";

		Node smartServiceTypeNode = (Node) xpath.evaluate(xpathExprSmartServiceType, element, XPathConstants.NODE);
		Node smartServiceSensorDataTypeNode = (Node) xpath.evaluate(xpathExprSmartServiceSensorDataType, element, XPathConstants.NODE);
		Node smartServiceSensorDataValueTypeNode = (Node) xpath.evaluate(xpathExprSmartServiceSensorDataValueType, element, XPathConstants.NODE);

		if (!Utilities.areNotNull(smartServiceTypeNode, smartServiceSensorDataTypeNode, smartServiceSensorDataValueTypeNode)) {
			return false;
		}

		return true;
	}
}
