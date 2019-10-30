package org.opentosca.container.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.NotFoundException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPropertyMapping;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceProperty;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceProperty;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PropertyMappingsHelper {
  private static Logger logger = LoggerFactory.getLogger(PropertyMappingsHelper.class);
  private final InstanceService instanceService;
  private final CsarStorageService storage;

  public PropertyMappingsHelper(final InstanceService instanceService, CsarStorageService storage) {
    this.instanceService = instanceService;
    this.storage = storage;
  }

  /**
   * Evaluates the property mappings of a boundary definition's properties against the xml fragment
   * representing these properties and uses node template instances for this purpose.
   *
   * @param serviceInstance the service template instance whose property mappings we want to evaluate
   * @return the xml fragment representing the properties after property mappings are evaluated
   * @throws NotFoundException thrown when the id does not correspond to a service template instance
   */
  public void evaluatePropertyMappings(final ServiceTemplateInstance serviceInstance) throws NotFoundException {
    final Document propertiesAsXML = serviceInstance.getPropertiesAsDocument();
    // check if the serviceInstance has properties
    if (propertiesAsXML == null) {
      return;
    }
    updateServiceInstanceProperties(serviceInstance, propertiesAsXML);
  }

  private void updateServiceInstanceProperties(final ServiceTemplateInstance serviceInstance,
                                               final Document proprtiesAsXML) {
    Csar owningCsar = storage.findById(serviceInstance.getCsarId());
    TServiceTemplate template = null;
    try {
      template = ToscaEngine.resolveServiceTemplate(owningCsar, serviceInstance.getTemplateId());
    } catch (org.opentosca.container.core.common.NotFoundException e) {
      logger.warn("Could not find service template associated with the serviceTemplateInstance {}", serviceInstance.getId(), e);
      return;
    }
    // check if the serviceTemplate has propertyMappings
    final TBoundaryDefinitions.Properties.PropertyMappings propertyMappings = template.getBoundaryDefinitions().getProperties().getPropertyMappings();
    if (propertyMappings == null) {
      // if there are no property mappings there is no need to update. The
      // properties can only be updated by external clients via setting
      // properties by hand
      return;
    }

    // cycle through mappings and update accordingly
    for (final TPropertyMapping mapping : propertyMappings.getPropertyMapping()) {
      final String serviceTemplatePropertyQuery = mapping.getServiceTemplatePropertyRef();
      final List<Element> serviceTemplatePropertyElements = queryElementList((Element) proprtiesAsXML.getFirstChild(), serviceTemplatePropertyQuery);

      // fetch element from serviceTemplateProperties
      if (serviceTemplatePropertyElements.size() != 1) {
        // skip this property, we expect only one
        continue;
      }

      // check whether the targetRef is concat query
      if (isConcatQuery(mapping.getTargetPropertyRef())) {
        // this query needs possibly multiple properties from different nodeInstances

        final String propertyValue = generatePropertyValueFromConcatQuery(mapping.getTargetPropertyRef(), serviceInstance.getNodeTemplateInstances());
        serviceTemplatePropertyElements.get(0).setTextContent(propertyValue);
      } else {
        // this query only fetches a SINGLE element on the properties of
        // the referenced entity
        final NodeTemplateInstance nodeInstance = getNodeInstanceFromMappingObject(serviceInstance, mapping.getTargetObjectRef());

        if (nodeInstance == null) {
          continue;
        }

        final Optional<NodeTemplateInstanceProperty> firstProperty = nodeInstance.getProperties().stream().findFirst();
        if (!firstProperty.isPresent()) {
          // skip it, the mapping is invalid
          continue;
        }

        final Document nodeProperties = this.instanceService.convertPropertyToDocument(firstProperty.get());
        final Element nodePropertiesRoot = (Element) nodeProperties.getFirstChild();
        final String nodeTemplatePropertyQuery = mapping.getTargetPropertyRef();
        final List<Element> nodePropertyElements = queryElementList(nodePropertiesRoot, nodeTemplatePropertyQuery);

        if (nodePropertyElements.size() != 1) {
          // skip this property, we expect only one
          continue;
        }

        // change the serviceTemplateProperty
        serviceTemplatePropertyElements.get(0).setTextContent(nodePropertyElements.get(0).getTextContent());
      }
    }

    try {
      serviceInstance.setProperties(Collections.singleton(this.instanceService.convertDocumentToProperty(proprtiesAsXML,
        ServiceTemplateInstanceProperty.class)));
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
      logger.error("Failed to store properties in service template instance object. Reason {}", e.getMessage());
    }
  }

  private List<Element> queryElementList(final Element node, final String xpathQuery) {
    final List<Element> elements = new ArrayList<>();
    try {
      final XPath xPath = XPathFactory.newInstance().newXPath();
      final NodeList nodes = (NodeList) xPath.evaluate(xpathQuery, node, XPathConstants.NODESET);
      for (int index = 0; index < nodes.getLength(); index++) {
        if (nodes.item(index).getNodeType() == Node.ELEMENT_NODE) {
          elements.add((Element) nodes.item(index));
        }
      }
    } catch (final XPathExpressionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return elements;
  }

  private boolean isConcatQuery(final String xPathQuery) {
    final String testString = xPathQuery.trim();

    if (!testString.startsWith("concat(")) {
      return false;
    }

    String functionContent = testString.substring("concat(".length());
    functionContent = functionContent.substring(0, functionContent.length() - 1);

    final String[] functionParts = functionContent.split(",");

    for (final String functionPart : functionParts) {
      if (functionPart.startsWith("'") && !functionPart.endsWith("'")) {
        return false;
      }
    }

    return true;
  }

  private String generatePropertyValueFromConcatQuery(final String targetPropertyRef,
                                                      final Collection<NodeTemplateInstance> nodeInstance) {
    final String testQuery = targetPropertyRef.trim();

    if (!testQuery.endsWith(")")) {
      return null;
    }

    final int functionOpeningBracket = testQuery.indexOf("(");

    final String functionString = testQuery.substring(0, functionOpeningBracket);

    // simple validity check as we only want to be able to concat strings,
    // but maybe more later
    if (!functionString.equals("concat")) {
      return null;
    }

    final String functionContent =
      testQuery.substring(functionOpeningBracket + 1, testQuery.lastIndexOf(")")).trim();

    final String[] functionParts = functionContent.split(",");

    final List<String> augmentedFunctionParts = new ArrayList<>();

    for (final String functionPart : functionParts) {
      if (functionPart.trim().startsWith("'")) {
        // string function part, just add to list
        augmentedFunctionParts.add(functionPart.trim());
      } else if (functionPart.trim().split("\\.").length == 3) {
        // "DSL" Query
        final String[] queryParts = functionPart.trim().split("\\.");
        // fast check for validity
        if (!queryParts[1].equals("Properties")) {
          return null;
        }

        final String nodeTemplateName = queryParts[0];
        final String propertyName = queryParts[2];

        if (getNodeInstanceWithName(nodeInstance, nodeTemplateName) != null) {

          final String propValue =
            fetchPropertyValueFromNodeInstance(getNodeInstanceWithName(nodeInstance, nodeTemplateName),
              propertyName);

          augmentedFunctionParts.add("'" + propValue + "'");
        }
      }
    }

    // now we have a string of the form:
    // concat('someString','somePropertyValue','someString',..)
    // just make the concat itself instead of running an XPath query

    String resultString = "";
    for (final String functionPart : augmentedFunctionParts) {
      resultString += functionPart.replace("'", "");
    }

    return resultString;
  }

  private NodeTemplateInstance getNodeInstanceWithName(final Collection<NodeTemplateInstance> nodeInstances,
                                                       final String nodeTemplateId) {

    for (final NodeTemplateInstance nodeInstance : nodeInstances) {
      if (nodeInstance.getTemplateId().equals(nodeTemplateId)) {
        return nodeInstance;
      }
    }

    return null;
  }

  private String fetchPropertyValueFromNodeInstance(final NodeTemplateInstance nodeInstance,
                                                    final String propertyLocalName) {
    if (nodeInstance.getProperties() == null) {
      return null;
    }

    return nodeInstance.getPropertiesAsMap().getOrDefault(propertyLocalName, null);
  }

  private NodeTemplateInstance getNodeInstanceFromMappingObject(final ServiceTemplateInstance serviceInstance,
                                                                final Object obj) {
    if (!(obj instanceof TNodeTemplate)) {
      logger.error("Only node templates are supported as target objects for property mappings!");
      return null;
    }
    final TNodeTemplate template = (TNodeTemplate) obj;
    final Collection<NodeTemplateInstance> nodeInstances = serviceInstance.getNodeTemplateInstances();
    if (nodeInstances == null) {
      return null;
    }

    for (final NodeTemplateInstance nodeInstance : nodeInstances) {
      if (nodeInstance.getTemplateId().equals(template.getId())) {
        return nodeInstance;
      }
    }
    return null;
  }
}
