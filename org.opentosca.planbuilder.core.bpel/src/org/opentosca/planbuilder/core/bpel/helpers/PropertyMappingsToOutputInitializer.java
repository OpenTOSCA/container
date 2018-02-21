package org.opentosca.planbuilder.core.bpel.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractBoundaryDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractPropertyMapping;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplateProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * This Class is responsible for fetching BoundaryDefinitions mappings and initialize the BuildPlan
 * with appropiate assigns to return property values to the BuildPlan caller
 * </p>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class PropertyMappingsToOutputInitializer {

    private final static Logger LOG = LoggerFactory.getLogger(PropertyMappingsToOutputInitializer.class);
    private final XPathFactory xPathfactory;

    /**
     * <p>
     * This class is a wrapper, which holds a mapping from ServiceTemplate Property, Template and
     * Template Property
     * </p>
     * Copyright 2013 IAAS University of Stuttgart <br>
     * <br>
     *
     * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
     *
     */
    private class ServiceTemplatePropertyToPropertyMapping {

        // internal array, basically n rows 3 columns
        private String[][] internalArray = new String[1][3];

        /**
         * Adds a mapping from ServiceTemplate Property, Template and Template Property
         *
         * @param serviceTemplatePropertyLocalName the localName of a serviceTemplate property
         * @param templateId the template Id
         * @param templatePropertyLocalName the localName of a template id
         */
        protected void addMapping(final String serviceTemplatePropertyLocalName, final String templateId,
                                  final String templatePropertyLocalName) {
            PropertyMappingsToOutputInitializer.LOG.debug("Adding ServiceTemplate Property Mapping, serviceTemplate property localName {}, templateId {} and template property localName {}",
                                                          serviceTemplatePropertyLocalName, templateId,
                                                          templatePropertyLocalName);
            if (this.internalArray.length == 1) {
                // nothing stored inside array yet
                this.internalArray[0][0] = serviceTemplatePropertyLocalName;
                this.internalArray[0][1] = templateId;
                this.internalArray[0][2] = templatePropertyLocalName;
                this.increaseArraySize();
            } else {
                this.internalArray[this.internalArray.length - 1][0] = serviceTemplatePropertyLocalName;
                this.internalArray[this.internalArray.length - 1][1] = templateId;
                this.internalArray[this.internalArray.length - 1][2] = templatePropertyLocalName;
                this.increaseArraySize();
            }
            this.printInternalArray();
        }

        private void printInternalArray() {
            for (int index_1 = 0; index_1 < this.internalArray.length; index_1++) {
                for (int index_2 = 0; index_2 < this.internalArray[index_1].length; index_2++) {
                    PropertyMappingsToOutputInitializer.LOG.debug("index1: " + index_1 + " index2: " + index_2
                        + " value: " + this.internalArray[index_1][index_2]);
                }
            }
        }

        /**
         * Removes a SericeTemplate Property Mapping
         *
         * @param serviceTemplatePropertyName a localName of serviceTemplate property
         */
        protected void removeServiceTemplatePropertyMapping(final String serviceTemplatePropertyName) {
            PropertyMappingsToOutputInitializer.LOG.debug("Removin ServiceTemplate Property Mapping for serviceTemplate Property {}",
                                                          serviceTemplatePropertyName);
            for (int index = 0; index < this.internalArray.length; index++) {
                if (this.internalArray[index][0] != null
                    && this.internalArray[index][0].equals(serviceTemplatePropertyName)) {
                    // TODO pretty ugly, but should work
                    this.internalArray[index][0] = null;
                    this.internalArray[index][1] = null;
                    this.internalArray[index][2] = null;
                }
            }
        }

        /**
         * Returns all ServiceTemplate Property localName inside this wrapper
         *
         * @return a List of Strings which are ServiceTemplate property localNames
         */
        protected List<String> getServiceTemplatePropertyNames() {
            final List<String> names = new ArrayList<>();
            for (final String[] element : this.internalArray) {
                if (element[0] != null) {
                    names.add(element[0]);
                }
            }
            return names;
        }

        /**
         * Returns the templateId of the ServiceTemplate Property Mapping
         *
         * @param serviceTemplateLocalName a localName of a ServiceTemplate property
         * @return a String which is a templateId else null
         */
        protected String getTemplateId(final String serviceTemplateLocalName) {
            for (final String[] element : this.internalArray) {
                if (element[0] != null && element[0].equals(serviceTemplateLocalName)) {
                    return element[1];
                }
            }
            return null;
        }

        /**
         * Returns a Template property localName for the given serviceTemplate property localName
         *
         * @param serviceTemplateLocalName a localName of a serviceTemplate Property
         * @return a String which is a localName of a Template property, else null
         */
        protected String getTemplatePropertyName(final String serviceTemplateLocalName) {
            for (final String[] element : this.internalArray) {
                if (element[0] != null && element[0].equals(serviceTemplateLocalName)) {
                    return element[2];
                }
            }
            return null;
        }

        /**
         * Increases the size of the internal array by 1 row
         */
        private void increaseArraySize() {
            final int arrayLength = this.internalArray.length;
            final String[][] newArray = new String[arrayLength + 1][3];
            for (int index = 0; index < arrayLength; index++) {
                // copy serviceTemplatePropertyName
                newArray[index][0] = this.internalArray[index][0];
                // copy templateId
                newArray[index][1] = this.internalArray[index][1];
                // copy templatePropLocalName
                newArray[index][2] = this.internalArray[index][2];
            }
            this.internalArray = newArray;
        }

    }

    public PropertyMappingsToOutputInitializer() {
        this.xPathfactory = XPathFactory.newInstance();
    }

    /**
     * <p>
     * Initializes the response message of the given BuildPlan according to the given
     * BoundaryDefinitions inside the given Definitions document
     * </p>
     *
     * @param definitions the Definitions document to look for BoundaryDefinitions for and contains the
     *        ServiceTemplate the BuildPlan belongs to
     * @param buildPlan a initialized BuildPlan
     * @param propMap a PropMap which contains the names of the different template property variables
     *        inside the plan
     */
    public void initializeBuildPlanOutput(final AbstractDefinitions definitions, final BPELPlan buildPlan,
                                          final PropertyMap propMap) {
        final QName serviceTemplateId = buildPlan.getServiceTemplate().getQName();

        // fetch serviceTemplate and boundaryDefinitions
        AbstractServiceTemplate buildPlanServiceTemplate = null;
        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            if (serviceTemplate.getQName().equals(serviceTemplateId)) {
                PropertyMappingsToOutputInitializer.LOG.debug("Found ServiceTemplate {}", serviceTemplate.getQName());
                buildPlanServiceTemplate = serviceTemplate;
            }
        }

        if (buildPlanServiceTemplate == null) {
            PropertyMappingsToOutputInitializer.LOG.error("ServiceTemplate {} was not found in given Definitions {}",
                                                          serviceTemplateId.toString(),
                                                          "{" + definitions.getTargetNamespace() + "}"
                                                              + definitions.getId());
            return;
        }

        final ServiceTemplatePropertyToPropertyMapping mapping = this.getMappings(buildPlanServiceTemplate, propMap);
        if (mapping == null) {
            PropertyMappingsToOutputInitializer.LOG.warn("Couldn't generate mapping, BuildPlan Output may be empty");
            return;
        }
        this.initializeAssignOutput(buildPlan, propMap, mapping);

    }

    /**
     * Generates a copy with a literal value to the outputmessage of the given BuildPlan. The literal
     * consists of the mappings given, where the propertyMap is used identify the propertyVariables
     * inside the buildPlan
     *
     * @param buildPlan the BuildPlan to add the copy to
     * @param propMap a PropertyMap containing the variable names of the properties
     * @param mapping the mappings from serviceTemplate Properties to template properties
     */
    private void initializeAssignOutput(final BPELPlan buildPlan, final PropertyMap propMap,
                                        final ServiceTemplatePropertyToPropertyMapping mapping) {
        try {
            final BPELPlanHandler buildPlanHandler = new BPELPlanHandler();
            final BPELPlanHandler processHandler = new BPELPlanHandler();

            final List<String> failedServiceTemplateProperties = new ArrayList<>();

            for (final String serviceTemplatePropertyName : mapping.getServiceTemplatePropertyNames()) {

                // add copy to assign
                final String templatePropertyName = mapping.getTemplatePropertyName(serviceTemplatePropertyName);
                if (templatePropertyName == null) {
                    PropertyMappingsToOutputInitializer.LOG.warn("TemplatePropertyName is null");
                    failedServiceTemplateProperties.add(serviceTemplatePropertyName);
                    continue;
                }

                // add to outputmessage
                buildPlanHandler.addStringElementToPlanResponse(serviceTemplatePropertyName, buildPlan);

                if (this.isConcatQuery(templatePropertyName)) {
                    processHandler.addCopyStringToOutputAssign(this.generateCopyFromQueryToOutputAsString(templatePropertyName,
                                                                                                          "//*[local-name()='"
                                                                                                              + serviceTemplatePropertyName
                                                                                                              + "']"),
                                                               buildPlan);
                } else {
                    final String templateId = mapping.getTemplateId(serviceTemplatePropertyName);
                    if (templateId == null) {
                        PropertyMappingsToOutputInitializer.LOG.warn("TemplateId of mapping is null!");
                        failedServiceTemplateProperties.add(serviceTemplatePropertyName);
                        continue;
                    }
                    final String propVarName = propMap.getPropertyMappingMap(templateId).get(templatePropertyName);
                    if (propVarName == null) {
                        PropertyMappingsToOutputInitializer.LOG.warn("PropertyVarName is null");
                        failedServiceTemplateProperties.add(serviceTemplatePropertyName);
                        continue;
                    }
                    processHandler.assginOutputWithVariableValue(propVarName, serviceTemplatePropertyName, buildPlan);
                }
            }

            for (final String failedServiceTempProp : failedServiceTemplateProperties) {
                mapping.removeServiceTemplatePropertyMapping(failedServiceTempProp);
            }

        }
        catch (final ParserConfigurationException e) {
            PropertyMappingsToOutputInitializer.LOG.error("Couldn't initialize a Handler, BuildPlan OutputMessage may be empty",
                                                          e);
            return;
        }
    }

    /**
     * Generates a copy element with from and to elements as String. The given mapping controls what the
     * from will assign to the outputmessage
     *
     * @param mapping the ServiceTemplate Property to Template Property mappings
     * @param buildPlan the BuildPlan to generate the copy for
     * @return a String containing a valid BPEL Copy Element
     */
    private String generateCopyFromQueryToOutputAsString(final String fromQuery, final String toQuery) {
        String copyString = "<bpel:copy xmlns:bpel=\"" + BPELPlan.bpelNamespace
            + "\"><bpel:from expressionLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[";
        copyString += fromQuery + "]]></bpel:from>";
        copyString +=
            "<bpel:to variable=\"output\" part=\"payload\"><bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA["
                + toQuery + "]]></bpel:query></bpel:to></bpel:copy>";
        return copyString;
    }

    /**
     * Generates a BPEL Literal element as String. The literal contains a valid OutputMessage Element of
     * the given BuildPlan.
     *
     * @param mapping the ServiceTemplate Property to Template Property mappings
     * @param buildPlan the BuildPlan to generate the Literal for
     * @return a valid BPEL Literal element as String
     */
    private String generateLiteralAssignForOutput(final ServiceTemplatePropertyToPropertyMapping mapping,
                                                  final BPELPlan buildPlan) {
        final String responseMessageLocalName = buildPlan.getWsdl().getResponseMessageLocalName();

        // <bpel:literal>
        // <impl:getPublicDNS xmlns:impl="http://ec2vm.aws.ia.opentosca.org"
        // xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        // <impl:CorrelationId>impl:CorrelationId</impl:CorrelationId>
        // <impl:instanceId>impl:instanceId</impl:instanceId>
        // <impl:region>impl:region</impl:region>
        // <impl:accessKey>impl:accessKey</impl:accessKey>
        // <impl:secretKey>impl:secretKey</impl:secretKey>
        // </impl:getPublicDNS>
        // </bpel:literal>

        String literalString = "<bpel:literal xmlns:bpel=\"" + BPELPlan.bpelNamespace + "\">";
        literalString += "<impl:" + responseMessageLocalName + " xmlns:impl=\""
            + buildPlan.getWsdl().getTargetNamespace() + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >";
        for (final String localName : mapping.getServiceTemplatePropertyNames()) {
            literalString += "<impl:" + localName + ">impl:" + localName + "</impl:" + localName + ">";
        }
        literalString += "</impl:" + responseMessageLocalName + "></bpel:literal>";

        return literalString;
    }

    /**
     * Calculates the ServiceTemplate Property to Template Property mappings for the given
     * ServiceTemplate
     *
     * @param buildPlanServiceTemplate a ServiceTemplate
     * @param propMap
     * @return a Mapping from ServiceTemplate properties to Template properties
     */
    private ServiceTemplatePropertyToPropertyMapping getMappings(final AbstractServiceTemplate buildPlanServiceTemplate,
                                                                 final PropertyMap propMap) {
        final ServiceTemplatePropertyToPropertyMapping mappingWrapper = new ServiceTemplatePropertyToPropertyMapping();

        final AbstractBoundaryDefinitions boundaryDefinitions = buildPlanServiceTemplate.getBoundaryDefinitions();
        if (boundaryDefinitions == null) {
            PropertyMappingsToOutputInitializer.LOG.warn("No BoundaryDefinitions in ServiceTemplate {} found. Output of BuildPlan maybe empty.",
                                                         buildPlanServiceTemplate.getQName().toString());
            return null;
        }

        // get Properties
        final AbstractServiceTemplateProperties serviceTemplateProps = boundaryDefinitions.getProperties();

        if (serviceTemplateProps == null) {
            PropertyMappingsToOutputInitializer.LOG.warn("ServiceTemplate has no Properties defined");
            return null;
        }
        // get the propertyElement and propertyMappings
        final AbstractProperties serviceTemplateProperties = serviceTemplateProps.getProperties();

        if (serviceTemplateProperties == null) {
            PropertyMappingsToOutputInitializer.LOG.warn("ServiceTemplate has no Properties defined");
            return null;
        }

        final List<AbstractPropertyMapping> propertyMappings = serviceTemplateProps.getPropertyMappings();
        final Element propElement = serviceTemplateProperties.getDOMElement();

        if (propElement == null) {
            PropertyMappingsToOutputInitializer.LOG.warn("ServiceTemplate has no Properties defined");
            return null;
        }

        // example:
        // <BoundaryDefinitions>
        // <Properties>
        // <ex:Property>someDefaultValue</ex:Property>
        // <PropertyMappings>
        // <PropertyMapping serviceTemplatePropertyRef="/ex:Property"
        // targetObjectRef="nodeTemplateID"
        // targetPropertyRef="/nodeTemplateIdLocalName"/> +
        // </PropertyMappings/> ?
        // </Properties>

        for (final AbstractPropertyMapping propertyMapping : propertyMappings) {

            // these two will be used to create a propery reference for the
            // internal property variable of the plan
            final String templateId = propertyMapping.getTargetObjectRef();

            final String serviceTemplatePropertyRef = propertyMapping.getServiceTemplatePropertyRef();

            final String targetPropertyRef = propertyMapping.getTargetPropertyRef();

            // this will be a localName in the output
            final String serviceTemplatePropLocalName =
                this.getTemplatePropertyLocalName(propElement, propertyMapping.getServiceTemplatePropertyRef());

            String templatePropLocalName = null;
            boolean isConcatQuery = false;
            if (this.isConcatQuery(targetPropertyRef)) {
                isConcatQuery = true;
                templatePropLocalName = this.injectBPELVariables(propertyMapping.getTargetPropertyRef(), propMap);

            } else {
                Element templateElement = null;

                if (this.getNodeTemplate(buildPlanServiceTemplate, templateId) != null) {
                    templateElement =
                        this.getNodeTemplate(buildPlanServiceTemplate, templateId).getProperties().getDOMElement();
                } else if (this.getRelationshipTemplate(buildPlanServiceTemplate, templateId) != null) {
                    templateElement = this.getRelationshipTemplate(buildPlanServiceTemplate, templateId).getProperties()
                                          .getDOMElement();
                }

                if (templateElement == null) {
                    PropertyMappingsToOutputInitializer.LOG.warn("Referenced Template {} in ServiceTemplate {} has no Properties defined, continueing with other PropertyMapping",
                                                                 templateId,
                                                                 buildPlanServiceTemplate.getQName().toString());
                    continue;
                }

                PropertyMappingsToOutputInitializer.LOG.debug("Adding Mapping for ServiceTemplateProperty {}, TemplateId {} and TemplateProperty {}",
                                                              serviceTemplatePropLocalName, templateId,
                                                              templateElement.getLocalName());

                templatePropLocalName =
                    this.getTemplatePropertyLocalName(templateElement, propertyMapping.getTargetPropertyRef());
            }

            if (templatePropLocalName == null) {
                PropertyMappingsToOutputInitializer.LOG.warn("Referenced Template {} in ServiceTemplate {} has no Properties defined, continueing with other PropertyMapping",
                                                             templateId,
                                                             buildPlanServiceTemplate.getQName().toString());
                continue;
            }

            if (serviceTemplatePropLocalName == null) {
                PropertyMappingsToOutputInitializer.LOG.warn("Couldn't find Property Element for ServiceTemplate {} , continueing with other PropertyMapping",
                                                             buildPlanServiceTemplate.getQName().toString());
                continue;
            }
            if (!isConcatQuery && templateId == null) {
                PropertyMappingsToOutputInitializer.LOG.warn("targetObjectRef for ServiceTemplate {} not set, continueing with other PropertyMapping",
                                                             buildPlanServiceTemplate.getQName().toString());
                continue;
            }

            mappingWrapper.addMapping(serviceTemplatePropLocalName, templateId, templatePropLocalName);

        }

        return mappingWrapper;
    }

    private String injectBPELVariables(final String targetPropertyRef, final PropertyMap propMap) {
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
                augmentedFunctionParts.add(functionPart);
            } else if (functionPart.trim().split("\\.").length == 3) {
                // "DSL" Query
                final String[] queryParts = functionPart.trim().split("\\.");
                // fast check for validity
                if (!queryParts[1].equals("Properties")) {
                    return null;
                }

                final String nodeTemplateName = queryParts[0];
                final String propertyName = queryParts[2];

                final Map<String, String> propertyName2BPELVarMap = propMap.getPropertyMappingMap(nodeTemplateName);
                if (propertyName2BPELVarMap == null) {
                    return null;
                } else if (propertyName2BPELVarMap.isEmpty()) {
                    return null;
                }

                final String bpelVarName = propertyName2BPELVarMap.get(propertyName);
                if (bpelVarName == null) {
                    return null;
                }

                augmentedFunctionParts.add("$" + bpelVarName);
            }
        }

        String resultString = functionString + "(";
        for (final String functionPart : augmentedFunctionParts) {
            resultString += functionPart + ",";
        }

        resultString = resultString.substring(0, resultString.length() - 1) + ")";

        return resultString;
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

    /**
     * Checks whether the given XPath 1.0 Query is referencing multiple NodeTemplate PropertyElements in
     * the form of fkt(propertyQuery,propertyQuery,..).
     *
     * @param xpathQuery a String containing a XPath 1.0 Query
     * @return true if the given query references a single NodeTemplate Property
     */
    private boolean isMultiQuery(final String xpathQuery, final Element topologyTemplateElement,
                                 final List<AbstractNodeTemplate> nodeTemplates) {

        final String testQuery = xpathQuery.trim();

        if (!testQuery.endsWith(")")) {
            return false;
        }

        final int functionOpeningBracket = testQuery.indexOf("(");

        final String functionString = testQuery.substring(0, functionOpeningBracket);

        // simple validity check as we only want to be able to concat strings,
        // but maybe more later
        if (!functionString.equals("concat")) {
            return false;
        }

        final String functionContent =
            testQuery.substring(functionOpeningBracket + 1, testQuery.lastIndexOf(")")).trim();
        final List<String> functionParts = this.seperateIntoStringsAndQueries(functionContent);

        for (final String part : functionParts) {
            if (!this.isStringPart(part) | this.isQueryPart(part, topologyTemplateElement, nodeTemplates) == null) {
                return false;
            }
        }

        return true;
    }

    private Map<AbstractNodeTemplate, String> isQueryPart(final String queryPart, final Element topologyTemplateElement,
                                                          final List<AbstractNodeTemplate> nodeTemplates) {

        final XPath xpath = this.xPathfactory.newXPath();
        try {
            final Node propertyNode = (Node) xpath.evaluate(queryPart, topologyTemplateElement, XPathConstants.NODE);

            if (propertyNode == null) {
                return null;
            }

            if (propertyNode.getNodeType() != Node.ELEMENT_NODE) {
                return null;
            }

            final String foundNodeTemplateName = this.getNodeTemplateNameWithWalk(propertyNode);
            if (foundNodeTemplateName != null) {
                for (final AbstractNodeTemplate nodeTemplate : nodeTemplates) {
                    if (nodeTemplate.getName().equals(foundNodeTemplateName)) {
                        if (this.getListOfChildLocalNames(nodeTemplate.getProperties().getDOMElement())
                                .contains(this.getLocalNameWithoutPrefi((Element) propertyNode))) {
                            final Map<AbstractNodeTemplate, String> result = new HashMap<>();
                            result.put(nodeTemplate, this.getLocalNameWithoutPrefi((Element) propertyNode));
                            return result;
                        }
                    }
                }
            }

        }
        catch (final XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return null;
    }

    private String getLocalNameWithoutPrefi(final Element e) {
        if (e.getLocalName().contains(":")) {
            final String localName = e.getLocalName();
            return localName.substring(localName.indexOf(":") + 1);
        }
        return e.getLocalName();
    }

    private List<String> getListOfChildLocalNames(final Element element) {
        final List<String> localNames = new ArrayList<>();
        final NodeList childNodes = element.getChildNodes();
        for (int index = 0; index < childNodes.getLength(); index++) {
            if (childNodes.item(index).getNodeType() == Node.ELEMENT_NODE) {
                localNames.add(childNodes.item(index).getLocalName());
            }
        }

        return localNames;
    }

    private String getNodeTemplateNameWithWalk(final Node node) {
        final XPath xpath = this.xPathfactory.newXPath();
        try {
            final Node nodeTemplateNode =
                (Node) xpath.evaluate("/*/*/*[local-name()='NodeTemplate']", node, XPathConstants.NODE);

            if (nodeTemplateNode.getNodeType() == Node.ELEMENT_NODE) {
                if (nodeTemplateNode.hasAttributes()) {
                    if (nodeTemplateNode.getAttributes().getNamedItem("Name") != null) {
                        return ((Element) nodeTemplateNode).getAttribute("Name");
                    }
                }
            }
        }
        catch (final XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    private boolean isStringPart(final String part) {
        return part.startsWith("\"") & part.endsWith("\"") | part.startsWith("'") & part.endsWith("'");
    }

    private List<String> seperateIntoStringsAndQueries(final String str) {
        final List<String> parts = new ArrayList<>();
        final String[] strs = str.split(",");
        for (final String s : strs) {
            parts.add(s);
        }
        return parts;
    }

    private int countCharIString(final String str, final char chr) {
        int count = 0;
        int index = 0;

        while (index != -1) {
            index = str.indexOf(chr, index);
            if (index != -1) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the localName of an element which is referenced by the XPath expression inside the
     * PropertyMappings
     *
     * @param serviceTemplatePropElement the first element inside the Properties Element
     * @param xpathExpr an XPath Expression
     * @return a localName when the XPath expression returned exactly one Node, else null
     */
    private String getTemplatePropertyLocalName(final Element serviceTemplatePropElement, final String xpathExpr) {
        try {
            final XPath xPath = XPathFactory.newInstance().newXPath();
            PropertyMappingsToOutputInitializer.LOG.debug("Executing XPath Expression {} on Node {}", xpathExpr,
                                                          serviceTemplatePropElement);
            final NodeList nodes =
                (NodeList) xPath.evaluate(xpathExpr, serviceTemplatePropElement, XPathConstants.NODESET);

            // we assume that the expression gives us a single node
            if (nodes.getLength() == 1) {
                final Node node = nodes.item(0);
                return node.getLocalName();
            } else {
                PropertyMappingsToOutputInitializer.LOG.error("XPath expression {} on Element {} returned multiple Nodes",
                                                              xpathExpr, serviceTemplatePropElement);
                return null;
            }

        }
        catch (final XPathExpressionException e1) {
            PropertyMappingsToOutputInitializer.LOG.error("XPath Expression for serviceTemplatePropetyRef isn't valid",
                                                          e1);
        }
        return null;
    }

    /**
     * Returns an AbstractNodeTemplate of the given serviceTemplate and TemplateId
     *
     * @param serviceTemplate the ServiceTemplate to search in
     * @param templateId the Id of the Template
     * @return an AbstractNodeTemplate with the specified Id, else null
     */
    private AbstractNodeTemplate getNodeTemplate(final AbstractServiceTemplate serviceTemplate,
                                                 final String templateId) {
        for (final AbstractNodeTemplate nodeTemplate : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
            if (nodeTemplate.getId().equals(templateId)) {
                return nodeTemplate;
            }
        }
        return null;
    }

    /**
     * Returns an AbstractRelationshipTemplate of the given serviceTemplate and TemplateId
     *
     * @param serviceTemplate the ServiceTemplate to search in
     * @param templateId the If of the template to search for
     * @return an AbstractRelationshipTemplate with the specified Id, else null
     */
    private AbstractRelationshipTemplate getRelationshipTemplate(final AbstractServiceTemplate serviceTemplate,
                                                                 final String templateId) {
        for (final AbstractRelationshipTemplate relationshipTemplate : serviceTemplate.getTopologyTemplate()
                                                                                      .getRelationshipTemplates()) {
            if (relationshipTemplate.getId().equals(templateId)) {
                return relationshipTemplate;
            }
        }
        return null;
    }

}
