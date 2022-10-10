package org.opentosca.planbuilder.core.bpmn.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPropertyMapping;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is responsible for fetching BoundaryDefinitions mappings and initialize the BuildPlan with appropiate
 * assigns to return property values to the BuildPlan caller
 */
public class ServiceTemplateBoundaryPropertyMappingsToOutputHandler {

    private final static Logger LOG =
        LoggerFactory.getLogger(ServiceTemplateBoundaryPropertyMappingsToOutputHandler.class);

    /**
     * <p>
     * Initializes the response message of the given BuildPlan according to the given BoundaryDefinitions inside the
     * given Definitions document
     * </p>
     *
     * @param definitions the Definitions document to look for BoundaryDefinitions for and contains the ServiceTemplate
     *                    the BuildPlan belongs to
     * @param buildPlan   a initialized BuildPlan
     * @param propMap     a PropMap which contains the names of the different template property variables inside the
     *                    plan
     */
    public HashMap<String, String> initializeBuildPlanOutput(final TDefinitions definitions, final BPMNPlan buildPlan,
                                                             final Property2VariableMapping propMap,
                                                             TServiceTemplate serviceTemplate) {
        HashMap<String, String> propertyOutput = new HashMap<>();
        final ServiceTemplatePropertyToPropertyMapping mapping = getMappings(serviceTemplate, propMap);
        if (mapping == null) {
            ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.warn("Couldn't generate mapping, BuildPlan Output may be empty");
            return propertyOutput;
        }
        propertyOutput = initializeAssignOutput(propMap, mapping, serviceTemplate);
        return propertyOutput;
    }

    /**
     * Generates a copy with a literal value to the outputmessage of the given BuildPlan. The literal consists of the
     * mappings given, where the propertyMap is used identify the propertyVariables inside the buildPlan
     *
     * @param propMap a PropertyMap containing the variable names of the properties
     * @param mapping the mappings from serviceTemplate Properties to template properties
     */
    private HashMap<String, String> initializeAssignOutput(final Property2VariableMapping propMap,
                                                           final ServiceTemplatePropertyToPropertyMapping mapping,
                                                           TServiceTemplate serviceTemplate) {
        HashMap<String, String> propertyOutput = new HashMap<>();

        final List<String> failedServiceTemplateProperties = new ArrayList<>();

        for (final String serviceTemplatePropertyName : mapping.getServiceTemplatePropertyNames()) {

            // add copy to assign
            final String templatePropertyName = mapping.getTemplatePropertyName(serviceTemplatePropertyName);
            if (templatePropertyName == null) {
                ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.warn("TemplatePropertyName is null");
                failedServiceTemplateProperties.add(serviceTemplatePropertyName);
                continue;
            }

            // add to outputmessage
            if (isConcatQuery(templatePropertyName)) {
                String targetPropertyRef = mapping.getTargetPropertyRef(serviceTemplatePropertyName);
                propertyOutput.put(serviceTemplatePropertyName, targetPropertyRef);
            } else {
                final String templateId = mapping.getTemplateId(serviceTemplatePropertyName);
                if (templateId == null) {
                    ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.warn("TemplateId of mapping is null!");
                    failedServiceTemplateProperties.add(serviceTemplatePropertyName);
                    continue;
                }

                boolean assigned = false;
                for (PropertyVariable var : propMap.getPropertyVariables(serviceTemplate, templateId)) {
                    if (var.getPropertyName().equals(templatePropertyName)) {
                        assigned = true;
                        propertyOutput.put(serviceTemplatePropertyName, mapping.getTargetPropertyRef(serviceTemplatePropertyName));
                    }
                }

                if (!assigned) {
                    ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.warn("PropertyVarName is null");
                    failedServiceTemplateProperties.add(serviceTemplatePropertyName);
                    continue;
                }
            }
        }

        for (final String failedServiceTempProp : failedServiceTemplateProperties) {
            mapping.removeServiceTemplatePropertyMapping(failedServiceTempProp);
        }

        return propertyOutput;
    }

    /**
     * Calculates the ServiceTemplate Property to Template Property mappings for the given ServiceTemplate
     *
     * @param buildPlanServiceTemplate a ServiceTemplate
     * @return a Mapping from ServiceTemplate properties to Template properties
     */
    private ServiceTemplatePropertyToPropertyMapping getMappings(final TServiceTemplate buildPlanServiceTemplate,
                                                                 final Property2VariableMapping propMap) {
        QName serviceTemplateQName = new QName(buildPlanServiceTemplate.getTargetNamespace(), buildPlanServiceTemplate.getId());
        final ServiceTemplatePropertyToPropertyMapping mappingWrapper = new ServiceTemplatePropertyToPropertyMapping();

        final TBoundaryDefinitions boundaryDefinitions = buildPlanServiceTemplate.getBoundaryDefinitions();
        if (boundaryDefinitions == null) {
            ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.warn("No BoundaryDefinitions in ServiceTemplate {} found. Output of BuildPlan maybe empty.",
                new QName(buildPlanServiceTemplate.getTargetNamespace(), buildPlanServiceTemplate.getId()));
            return null;
        }

        // get Properties
        final TBoundaryDefinitions.Properties serviceTemplateProps = boundaryDefinitions.getProperties();

        if (serviceTemplateProps == null) {
            ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.warn("ServiceTemplate has no Properties defined");
            return null;
        }
        // get the propertyElement and propertyMappings
        final TBoundaryDefinitions.Properties serviceTemplateProperties = serviceTemplateProps;

        if (serviceTemplateProperties == null) {
            ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.warn("ServiceTemplate has no Properties defined");
            return null;
        }

        final List<TPropertyMapping> propertyMappings = serviceTemplateProps.getPropertyMappings();

        final Element propElement = (Element) serviceTemplateProperties.getAny();

        if (propElement == null) {
            ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.warn("ServiceTemplate has no Properties defined");
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

        for (final TPropertyMapping propertyMapping : propertyMappings) {

            // these two will be used to create a propery reference for the
            // internal property variable of the plan
            final String templateId = propertyMapping.getTargetObjectRef().getId();
            final String targetPropertyRef = propertyMapping.getTargetPropertyRef();
            // this will be a localName in the output
            final String serviceTemplatePropLocalName = getTemplatePropertyLocalName(propElement, propertyMapping.getServiceTemplatePropertyRef());
            String templatePropLocalName = this.determinePropLocalName(targetPropertyRef, propertyMapping, buildPlanServiceTemplate, templateId, serviceTemplateQName, propMap, serviceTemplatePropLocalName);
            if (templatePropLocalName == null) {
                ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.warn("Referenced Template {} in ServiceTemplate {} has no Properties defined, continueing with other PropertyMapping",
                    templateId,
                    serviceTemplateQName);
                continue;
            }

            mappingWrapper.addMapping(serviceTemplatePropLocalName, templateId, templatePropLocalName, targetPropertyRef);
        }

        return mappingWrapper;
    }

    private String determinePropLocalName(String targetPropertyRef, TPropertyMapping propertyMapping, TServiceTemplate buildPlanServiceTemplate,
                                          String templateId, QName serviceTemplateQName, Property2VariableMapping propMap, String serviceTemplatePropLocalName) {
        String templatePropLocalName = null;
        boolean isConcatQuery = false;
        if (isConcatQuery(targetPropertyRef)) {
            isConcatQuery = true;
            templatePropLocalName =
                injectBPMNVariables(propertyMapping.getTargetPropertyRef(), propMap, buildPlanServiceTemplate);
        } else {
            TEntityTemplate.Properties props = null;

            if (getNodeTemplate(buildPlanServiceTemplate, templateId) != null) {
                props =
                    getNodeTemplate(buildPlanServiceTemplate, templateId).getProperties();
            } else if (getRelationshipTemplate(buildPlanServiceTemplate, templateId) != null) {
                props =
                    getRelationshipTemplate(buildPlanServiceTemplate, templateId).getProperties();
            }

            if (props != null && ModelUtils.asMap(props).isEmpty()) {
                ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.warn("Referenced Template {} in ServiceTemplate {} has no Properties defined, continueing with other PropertyMapping",
                    templateId,
                    serviceTemplateQName
                        .toString());
                return null;
            }

            ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.debug("Adding Mapping for ServiceTemplateProperty {}, TemplateId {}",
                serviceTemplatePropLocalName,
                templateId);

            templatePropLocalName = ModelUtils.asMap(props).get(propertyMapping.getTargetPropertyRef());
        }

        if (templatePropLocalName == null) {
            ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.warn("Referenced Template {} in ServiceTemplate {} has no Properties defined, continueing with other PropertyMapping",
                templateId,
                serviceTemplateQName
                    .toString());
            return null;
        }

        if (serviceTemplatePropLocalName == null) {
            ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.warn("Couldn't find Property Element for ServiceTemplate {} , continueing with other PropertyMapping",
                serviceTemplateQName
                    .toString());
            return null;
        }
        if (!isConcatQuery && templateId == null) {
            ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.warn("targetObjectRef for ServiceTemplate {} not set, continueing with other PropertyMapping",
                serviceTemplateQName
                    .toString());
            return null;
        }

        return templatePropLocalName;
    }

    private String injectBPMNVariables(final String targetPropertyRef, final Property2VariableMapping propMap,
                                       TServiceTemplate serviceTemplate) {
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
            } else if (functionPart.trim().split("\\.Properties\\.").length == 2) {
                // "DSL" Query
                final String[] queryParts = functionPart.trim().split("\\.Properties\\.");

                final String nodeTemplateName = queryParts[0];
                final String propertyName = queryParts[1];

                boolean addedVar = false;
                for (PropertyVariable var : propMap.getPropertyVariables(serviceTemplate, nodeTemplateName)) {
                    if (var.getPropertyName().equals(propertyName)) {
                        addedVar = true;
                        augmentedFunctionParts.add("$" + var.getVariableName());
                    }
                }

                if (!addedVar) {
                    return null;
                }
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
     * Returns the localName of an element which is referenced by the XPath expression inside the PropertyMappings
     *
     * @param serviceTemplatePropElement the first element inside the Properties Element
     * @param xpathExpr                  an XPath Expression
     * @return a localName when the XPath expression returned exactly one Node, else null
     */
    private String getTemplatePropertyLocalName(final Element serviceTemplatePropElement, final String xpathExpr) {
        try {
            final XPath xPath = XPathFactory.newInstance().newXPath();
            ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.debug("Executing XPath Expression {} on Node {}",
                xpathExpr, serviceTemplatePropElement);
            final NodeList nodes =
                (NodeList) xPath.evaluate(xpathExpr, serviceTemplatePropElement, XPathConstants.NODESET);

            // we assume that the expression gives us a single node
            if (nodes.getLength() == 1) {
                final Node node = nodes.item(0);
                return node.getLocalName();
            } else {
                ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.error("XPath expression {} on Element {} returned multiple Nodes",
                    xpathExpr, serviceTemplatePropElement);
                return null;
            }
        } catch (final XPathExpressionException e1) {
            ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.error("XPath Expression for serviceTemplatePropetyRef isn't valid",
                e1);
        }
        return null;
    }

    /**
     * Returns an TNodeTemplate of the given serviceTemplate and TemplateId
     *
     * @param serviceTemplate the ServiceTemplate to search in
     * @param templateId      the Id of the Template
     * @return an TNodeTemplate with the specified Id, else null
     */
    private TNodeTemplate getNodeTemplate(final TServiceTemplate serviceTemplate,
                                          final String templateId) {
        for (final TNodeTemplate nodeTemplate : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
            if (nodeTemplate.getId().equals(templateId)) {
                return nodeTemplate;
            }
        }
        return null;
    }

    /**
     * Returns an TRelationshipTemplate of the given serviceTemplate and TemplateId
     *
     * @param serviceTemplate the ServiceTemplate to search in
     * @param templateId      the If of the template to search for
     * @return an TRelationshipTemplate with the specified Id, else null
     */
    private TRelationshipTemplate getRelationshipTemplate(final TServiceTemplate serviceTemplate,
                                                          final String templateId) {
        for (final TRelationshipTemplate relationshipTemplate : serviceTemplate.getTopologyTemplate()
            .getRelationshipTemplates()) {
            if (relationshipTemplate.getId().equals(templateId)) {
                return relationshipTemplate;
            }
        }
        return null;
    }

    /**
     * <p>
     * This class is a wrapper, which holds a mapping from ServiceTemplate Property, Template and Template Property
     * </p>
     * Copyright 2013-2022 IAAS University of Stuttgart <br>
     * <br>
     *
     * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
     */
    private class ServiceTemplatePropertyToPropertyMapping {

        // internal array, basically n rows 3 columns
        private String[][] internalArray = new String[1][4];

        /**
         * Adds a mapping from ServiceTemplate Property, Template and Template Property
         *
         * @param serviceTemplatePropertyLocalName the localName of a serviceTemplate property
         * @param templateId                       the template Id
         * @param templatePropertyLocalName        the localName of a template id
         */
        protected void addMapping(final String serviceTemplatePropertyLocalName, final String templateId,
                                  final String templatePropertyLocalName, final String targetPropertyRef) {
            ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.debug("Adding ServiceTemplate Property Mapping, serviceTemplate property localName {}, templateId {} and template property localName {}",
                serviceTemplatePropertyLocalName,
                templateId, templatePropertyLocalName);
            if (this.internalArray.length == 1) {
                // nothing stored inside array yet
                this.internalArray[0][0] = serviceTemplatePropertyLocalName;
                this.internalArray[0][1] = templateId;
                this.internalArray[0][2] = templatePropertyLocalName;
                this.internalArray[0][3] = targetPropertyRef;
            } else {
                this.internalArray[this.internalArray.length - 1][0] = serviceTemplatePropertyLocalName;
                this.internalArray[this.internalArray.length - 1][1] = templateId;
                this.internalArray[this.internalArray.length - 1][2] = templatePropertyLocalName;
                this.internalArray[this.internalArray.length - 1][3] = targetPropertyRef;
            }
            increaseArraySize();
            printInternalArray();
        }

        private void printInternalArray() {
            for (int index_1 = 0; index_1 < this.internalArray.length; index_1++) {
                for (int index_2 = 0; index_2 < this.internalArray[index_1].length; index_2++) {
                    ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.debug("index1: " + index_1 + " index2: "
                        + index_2 + " value: " + this.internalArray[index_1][index_2]);
                }
            }
        }

        /**
         * Removes a SericeTemplate Property Mapping
         *
         * @param serviceTemplatePropertyName a localName of serviceTemplate property
         */
        protected void removeServiceTemplatePropertyMapping(final String serviceTemplatePropertyName) {
            ServiceTemplateBoundaryPropertyMappingsToOutputHandler.LOG.debug("Removin ServiceTemplate Property Mapping for serviceTemplate Property {}",
                serviceTemplatePropertyName);
            for (int index = 0; index < this.internalArray.length; index++) {
                if (this.internalArray[index][0] != null
                    && this.internalArray[index][0].equals(serviceTemplatePropertyName)) {
                    // TODO pretty ugly, but should work
                    this.internalArray[index][0] = null;
                    this.internalArray[index][1] = null;
                    this.internalArray[index][2] = null;
                    this.internalArray[index][3] = null;
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

        protected String getTargetPropertyRef(final String serviceTemplateLocalName) {
            for (final String[] element : this.internalArray) {
                if (element[0] != null && element[0].equals(serviceTemplateLocalName)) {
                    return element[3];
                }
            }
            return null;
        }

        /**
         * Increases the size of the internal array by 1 row
         */
        private void increaseArraySize() {
            final int arrayLength = this.internalArray.length;
            final String[][] newArray = new String[arrayLength + 1][4];
            for (int index = 0; index < arrayLength; index++) {
                // copy serviceTemplatePropertyName
                newArray[index][0] = this.internalArray[index][0];
                // copy templateId
                newArray[index][1] = this.internalArray[index][1];
                // copy templatePropLocalName
                newArray[index][2] = this.internalArray[index][2];
                // copy targetPropertyRef
                newArray[index][3] = this.internalArray[index][3];
            }
            this.internalArray = newArray;
        }
    }
}
