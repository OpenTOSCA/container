package org.opentosca.planbuilder.core.bpel.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.BPELScopeHandler;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan.VariableType;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class NodeRelationInstanceVariablesHandler {

  private static final String InstanceDataAPIUrlKeyword = "instanceDataAPIUrl";
  private static final String InstanceURLVarKeyword = "InstanceURL";
  private static final String InstanceIDVarKeyword = "InstanceID";

  private final BPELPlanHandler bpelProcessHandler;

  private final BPELScopeHandler bpelTemplateScopeHandler;

  private final BPELProcessFragments bpelFragments;

  public NodeRelationInstanceVariablesHandler(final BPELPlanHandler bpelProcessHandler) throws ParserConfigurationException {
    this.bpelTemplateScopeHandler = new BPELScopeHandler();
    this.bpelFragments = new BPELProcessFragments();
    this.bpelProcessHandler = bpelProcessHandler;
  }

  public boolean addIfNullAbortCheck(final BPELPlan plan, final PropertyMap propMap) {
    boolean check = true;
    for (final BPELScopeActivity templatePlan : plan.getTemplateBuildPlans()) {
      if (templatePlan.getNodeTemplate() != null && templatePlan.getNodeTemplate().getProperties() != null) {
        check &= this.addIfNullAbortCheck(templatePlan, propMap);
      }
    }
    return check;
  }

  public boolean addIfNullAbortCheck(final BPELScopeActivity templatePlan, final PropertyMap propMap) {

    for (final String propLocalName : propMap.getPropertyMappingMap(templatePlan.getNodeTemplate().getId())
      .keySet()) {
      final String bpelVarName =
        propMap.getPropertyMappingMap(templatePlan.getNodeTemplate().getId()).get(propLocalName);
      // as the variables are there and only possibly empty we just check
      // the string inside
      final String xpathQuery = "string-length(normalize-space($" + bpelVarName + ")) = 0";
      final QName propertyEmptyFault = new QName("http://opentosca.org/plans/faults", "PropertyValueEmptyFault");
      try {
        Node bpelIf = this.bpelFragments.generateBPELIfTrueThrowFaultAsNode(xpathQuery, propertyEmptyFault);
        bpelIf = templatePlan.getBpelDocument().importNode(bpelIf, true);
        templatePlan.getBpelSequencePrePhaseElement().appendChild(bpelIf);
      } catch (final IOException e) {
        e.printStackTrace();
        return false;
      } catch (final SAXException e) {
        e.printStackTrace();
        return false;
      }
    }

    return true;
  }

  /**
   * Fetches the correct nodeInstanceID link for the given TemplatePlan and sets the value inside a
   * NodeInstanceID bpel variable
   *
   * @param templatePlan              a templatePlan with set variable with name NodeInstanceID
   * @param serviceTemplateUrlVarName the name of the variable holding the url to the serviceTemplate
   * @param instanceDataUrlVarName    the name of the variable holding the url to the instanceDataAPI
   * @return
   */
  public boolean addInstanceFindLogic(final BPELScopeActivity templatePlan, final String serviceTemplateUrlVarName,
                                      final String instanceDataUrlVarName, final String query) {
    // add XML Schema Namespace for the logic
    final String xsdPrefix = "xsd" + System.currentTimeMillis();
    final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
    this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, templatePlan.getBuildPlan());
    // create Response Variable for interaction
    final String instanceDataAPIResponseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();
    this.bpelTemplateScopeHandler.addVariable(instanceDataAPIResponseVarName, VariableType.TYPE,
      new QName(xsdNamespace, "anyType", xsdPrefix), templatePlan);
    // find nodeInstance with query at instanceDataAPI
    try {
      Node nodeInstanceGETNode =
        this.bpelFragments.createRESTExtensionGETForNodeInstanceDataAsNode(serviceTemplateUrlVarName,
          instanceDataAPIResponseVarName,
          templatePlan.getNodeTemplate()
            .getId(),
          query);
      nodeInstanceGETNode = templatePlan.getBpelDocument().importNode(nodeInstanceGETNode, true);
      templatePlan.getBpelSequencePrePhaseElement().appendChild(nodeInstanceGETNode);
    } catch (final SAXException e) {
      e.printStackTrace();
    } catch (final IOException e) {
      e.printStackTrace();
    }

    final String instanceIDVarName = this.findInstanceIdVarName(templatePlan);

    // fetch nodeInstanceID from nodeInstance query
    try {
      Node assignNodeInstanceIDFromInstanceDataAPIQueryResponse =
        this.bpelFragments.createAssignSelectFirstReferenceAndAssignToStringVarAsNode(instanceDataAPIResponseVarName,
          instanceIDVarName);
      assignNodeInstanceIDFromInstanceDataAPIQueryResponse =
        templatePlan.getBpelDocument().importNode(assignNodeInstanceIDFromInstanceDataAPIQueryResponse, true);
      templatePlan.getBpelSequencePrePhaseElement()
        .appendChild(assignNodeInstanceIDFromInstanceDataAPIQueryResponse);
    } catch (final SAXException e) {
      e.printStackTrace();
    } catch (final IOException e) {
      e.printStackTrace();
    }

    return true;
  }

  public boolean addInstanceIDVarToTemplatePlans(final BPELPlan plan) {
    boolean check = true;
    for (final BPELScopeActivity templatePlan : plan.getTemplateBuildPlans()) {
      check &= addInstanceIDVarToTemplatePlan(templatePlan);
    }
    return check;
  }

  public boolean addInstanceIDVarToTemplatePlan(final BPELScopeActivity templatePlan) {
    final String xsdPrefix = "xsd" + System.currentTimeMillis();
    final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";

    this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, templatePlan.getBuildPlan());

    String templateId = "";
    String prefix = "";

    if (templatePlan.getNodeTemplate() != null) {
      templateId = templatePlan.getNodeTemplate().getId();
      prefix = "node";
    } else {
      templateId = templatePlan.getRelationshipTemplate().getId();
      prefix = "relationship";
    }

    final String instanceIdVarName = prefix + InstanceIDVarKeyword + "_" + ModelUtils.makeValidNCName(templateId)
      + "_" + System.currentTimeMillis();

    return this.bpelProcessHandler.addVariable(instanceIdVarName, VariableType.TYPE,
      new QName(xsdNamespace, "string", xsdPrefix),
      templatePlan.getBuildPlan());
  }

  /**
   * Adds a NodeInstanceID Variable to the given TemplatePlan
   *
   * @param templatePlan a TemplatePlan
   * @return true iff adding a NodeInstanceID Var was successful
   */
  public boolean addInstanceURLVarToTemplatePlan(final BPELScopeActivity templatePlan) {
    final String xsdPrefix = "xsd" + System.currentTimeMillis();
    final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";

    this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, templatePlan.getBuildPlan());

    String templateId = "";
    String prefix = "";

    if (templatePlan.getNodeTemplate() != null) {
      templateId = templatePlan.getNodeTemplate().getId();
      prefix = "node";
    } else {
      templateId = templatePlan.getRelationshipTemplate().getId();
      prefix = "relationship";
    }

    final String instanceIdVarName = prefix + InstanceURLVarKeyword + "_" + ModelUtils.makeValidNCName(templateId)
      + "_" + System.currentTimeMillis();

    return this.bpelProcessHandler.addVariable(instanceIdVarName, VariableType.TYPE,
      new QName(xsdNamespace, "string", xsdPrefix),
      templatePlan.getBuildPlan());

  }

  /**
   * Adds a NodeInstanceID Variable to each TemplatePlan inside the given Plan
   *
   * @param plan a plan with TemplatePlans
   * @return
   */
  public boolean addInstanceURLVarToTemplatePlans(final BPELPlan plan) {
    boolean check = true;
    for (final BPELScopeActivity templatePlan : plan.getTemplateBuildPlans()) {
      check &= addInstanceURLVarToTemplatePlan(templatePlan);
    }
    return check;
  }

  public boolean addNodeInstanceFindLogic(final BPELPlan plan, final String queryForNodeInstances) {
    boolean check = true;

    final String serviceTemplateUrlVarName =
      ServiceInstanceVariablesHandler.findServiceTemplateUrlVariableName(this.bpelProcessHandler, plan);

    for (final BPELScopeActivity templatePlan : plan.getTemplateBuildPlans()) {
      if (templatePlan.getNodeTemplate() != null) {
        check &= addInstanceFindLogic(templatePlan, serviceTemplateUrlVarName, InstanceDataAPIUrlKeyword,
          queryForNodeInstances);
      }
    }

    return check;
  }

  /**
   * Adds logic to fetch property data from the instanceDataAPI with the nodeInstanceID variable. The
   * property data is then assigned to appropriate BPEL variables of the given plan.
   *
   * @param plan    a plan containing templatePlans with set nodeInstanceID variables
   * @param propMap a Mapping from NodeTemplate Properties to BPEL Variables
   * @return true if adding logic described above was successful
   */
  public boolean addPropertyVariableUpdateBasedOnNodeInstanceID(final BPELPlan plan, final PropertyMap propMap) {
    boolean check = true;
    for (final BPELScopeActivity templatePlan : plan.getTemplateBuildPlans()) {
      if (templatePlan.getNodeTemplate() != null && templatePlan.getNodeTemplate().getProperties() != null
        && templatePlan.getNodeTemplate().getProperties().getDOMElement() != null) {
        check &= this.addPropertyVariableUpdateBasedOnNodeInstanceID(templatePlan, propMap);
      }
    }
    return check;
  }

  /**
   * Adds logic to fetch property data from the instanceDataAPI with the nodeInstanceID variable. The
   * property data is then assigned to appropriate BPEL Variables of the given templatePlan.
   *
   * @param templatePlan a TemplatePlan of a NodeTemplate that has properties
   * @param propMap      a Mapping from NodeTemplate Properties to BPEL Variables
   * @return true if adding logic described above was successful
   */
  public boolean addPropertyVariableUpdateBasedOnNodeInstanceID(final BPELScopeActivity templatePlan,
                                                                final PropertyMap propMap) {
    // check if everything is available
    if (templatePlan.getNodeTemplate() == null) {
      return false;
    }

    if (templatePlan.getNodeTemplate().getProperties() == null) {
      return false;
    }

    if (this.findInstanceIdVarName(templatePlan) == null) {
      return false;
    }

    final String instanceIdVarName = this.findInstanceIdVarName(templatePlan);

    final AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
    // add XMLSchema Namespace for the logic
    final String xsdPrefix = "xsd" + System.currentTimeMillis();
    final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
    this.bpelProcessHandler.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, templatePlan.getBuildPlan());
    // create Response Variable for interaction
    final String instanceDataAPIResponseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();
    this.bpelTemplateScopeHandler.addVariable(instanceDataAPIResponseVarName, VariableType.TYPE,
      new QName(xsdNamespace, "anyType", xsdPrefix), templatePlan);

    // fetch properties from nodeInstance
    try {
      Node nodeInstancePropertiesGETNode =
        this.bpelFragments.createRESTExtensionGETForNodeInstancePropertiesAsNode(instanceIdVarName,
          instanceDataAPIResponseVarName);
      nodeInstancePropertiesGETNode =
        templatePlan.getBpelDocument().importNode(nodeInstancePropertiesGETNode, true);
      templatePlan.getBpelSequencePrePhaseElement().appendChild(nodeInstancePropertiesGETNode);
    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final SAXException e) {
      e.printStackTrace();
    }

    // assign bpel variables from the requested properties
    // create mapping from property dom nodes to bpelvariable
    final Map<Element, String> element2BpelVarNameMap = new HashMap<>();
    final NodeList propChildNodes = nodeTemplate.getProperties().getDOMElement().getChildNodes();
    for (int index = 0; index < propChildNodes.getLength(); index++) {
      if (propChildNodes.item(index).getNodeType() == Node.ELEMENT_NODE) {
        final Element childElement = (Element) propChildNodes.item(index);
        // find bpelVariable
        final String bpelVarName =
          propMap.getPropertyMappingMap(nodeTemplate.getId()).get(childElement.getLocalName());
        if (bpelVarName != null) {
          element2BpelVarNameMap.put(childElement, bpelVarName);
        }
      }
    }

    try {
      Node assignPropertiesToVariables =
        this.bpelFragments.createAssignFromNodeInstancePropertyToBPELVariableAsNode("assignPropertiesFromResponseToBPELVariable"
          + System.currentTimeMillis(), instanceDataAPIResponseVarName, element2BpelVarNameMap);
      assignPropertiesToVariables = templatePlan.getBpelDocument().importNode(assignPropertiesToVariables, true);
      templatePlan.getBpelSequencePrePhaseElement().appendChild(assignPropertiesToVariables);
    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final SAXException e) {
      e.printStackTrace();
    }

    return true;
  }

  public boolean addPropertyVariableUpdateBasedOnNodeInstanceID(final BPELPlanContext context,
                                                                final AbstractNodeTemplate nodeTemplate) {

    final String instanceIdVarName =
      this.findInstanceIdVarName(context.getMainVariableNames(), nodeTemplate.getId(), true);

    if (instanceIdVarName == null) {
      return false;
    }

    final String xsdPrefix = "xsd" + System.currentTimeMillis();
    final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";

    // create Response Variable for interaction
    final String instanceDataAPIResponseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();

    context.addVariable(instanceDataAPIResponseVarName, VariableType.TYPE,
      new QName(xsdNamespace, "anyType", xsdPrefix));

    // fetch properties from nodeInstance
    try {
      Node nodeInstancePropertiesGETNode =
        this.bpelFragments.createRESTExtensionGETForNodeInstancePropertiesAsNode(instanceIdVarName,
          instanceDataAPIResponseVarName);

      nodeInstancePropertiesGETNode = context.importNode(nodeInstancePropertiesGETNode);
      context.getPrePhaseElement().appendChild(nodeInstancePropertiesGETNode);
    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final SAXException e) {
      e.printStackTrace();
    }

    // assign bpel variables from the requested properties
    // create mapping from property dom nodes to bpelvariable
    final Map<Element, String> element2BpelVarNameMap = new HashMap<>();
    final NodeList propChildNodes = nodeTemplate.getProperties().getDOMElement().getChildNodes();
    for (int index = 0; index < propChildNodes.getLength(); index++) {
      if (propChildNodes.item(index).getNodeType() == Node.ELEMENT_NODE) {
        final Element childElement = (Element) propChildNodes.item(index);
        // find bpelVariable
        final String bpelVarName = context.getVarNameOfTemplateProperty(childElement.getLocalName());
        if (bpelVarName != null) {
          element2BpelVarNameMap.put(childElement, bpelVarName);
        }
      }
    }

    try {
      Node assignPropertiesToVariables =
        this.bpelFragments.createAssignFromNodeInstancePropertyToBPELVariableAsNode("assignPropertiesFromResponseToBPELVariable"
          + System.currentTimeMillis(), instanceDataAPIResponseVarName, element2BpelVarNameMap);
      assignPropertiesToVariables = context.importNode(assignPropertiesToVariables);
      context.getPrePhaseElement().appendChild(assignPropertiesToVariables);
    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final SAXException e) {
      e.printStackTrace();
    }

    return true;
  }

  public String appendCountInstancesLogic(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate,
                                          final String query) {

    final String xsdPrefix = "xsd" + System.currentTimeMillis();
    final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";

    // create Response Variable for interaction
    final String responseVarName = "instanceDataAPIResponseVariable" + System.currentTimeMillis();
    final String counterVarName = "counterVariable" + System.currentTimeMillis();

    context.addGlobalVariable(responseVarName, VariableType.TYPE, new QName(xsdNamespace, "anyType", xsdPrefix));

    final Variable counterVariable = context.createGlobalStringVariable(counterVarName, "0");

    // context.addVariable(counterVarName, VariableType.TYPE, new
    // QName(xsdNamespace, "unsignedInt", xsdPrefix));

    final Node templateMainSequeceNode = context.getPrePhaseElement().getParentNode();
    final Node templateMainScopeNode = templateMainSequeceNode.getParentNode();

    // we'll move the correlation sets down one scope later

    try {

      Node getNodeInstancesREST =
        this.bpelFragments.createRESTExtensionGETForNodeInstanceDataAsNode(ServiceInstanceVariablesHandler.getServiceTemplateURLVariableName(context.getMainVariableNames()),
          responseVarName,
          nodeTemplate.getId(), query);
      getNodeInstancesREST = context.importNode(getNodeInstancesREST);
      templateMainSequeceNode.appendChild(getNodeInstancesREST);

      Node assignCounter =
        this.bpelFragments.createAssignVarToVarWithXpathQueryAsNode("assignInstanceCount_"
            + nodeTemplate.getId() + "_" + context.getIdForNames(), responseVarName, counterVariable.getName(),
          "count(//*[local-name()='NodeTemplateInstance'])");
      assignCounter = context.importNode(assignCounter);
      templateMainSequeceNode.appendChild(assignCounter);
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (final SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // count(//*[local-name()='Reference' and @*[local-name()!='Self']])

    final Element forEachElement = createForEachActivity(context, counterVariable.getName());

    final Element forEachScopeElement = (Element) forEachElement.getElementsByTagName("scope").item(0);

    if (((Element) templateMainScopeNode).getElementsByTagName("correlationSets").getLength() != 0) {
      final Element correlationSets =
        (Element) ((Element) templateMainScopeNode).getElementsByTagName("correlationSets").item(0);

      final Node cloneCorreElement = correlationSets.cloneNode(true);

      forEachScopeElement.appendChild(cloneCorreElement);
      templateMainScopeNode.removeChild(correlationSets);

    }
    final Element sequenceElement = context.createElement(BPELPlan.bpelNamespace, "sequence");

    sequenceElement.appendChild(context.importNode(context.getPrePhaseElement().cloneNode(true)));
    sequenceElement.appendChild(context.importNode(context.getProvisioningPhaseElement().cloneNode(true)));
    sequenceElement.appendChild(context.importNode(context.getPostPhaseElement().cloneNode(true)));

    forEachScopeElement.appendChild(sequenceElement);

    templateMainSequeceNode.removeChild(context.getPrePhaseElement());
    templateMainSequeceNode.removeChild(context.getPostPhaseElement());
    templateMainSequeceNode.removeChild(context.getProvisioningPhaseElement());

    templateMainSequeceNode.appendChild(forEachElement);

    return null;
  }

  public String appendCountInstancesLogic(final BPELPlanContext context,
                                          final AbstractRelationshipTemplate relationshipTemplate) {
    // TODO
    return null;
  }

  public String appendCountInstancesLogic(final BPELPlanContext context, final String query) {
    if (context.getNodeTemplate() == null) {
      return this.appendCountInstancesLogic(context, context.getRelationshipTemplate());
    } else {
      return this.appendCountInstancesLogic(context, context.getNodeTemplate(), query);
    }
  }

  public Element createForEachActivity(final BPELPlanContext context, final String instanceCountVariableName) {
    final Element forEachElement = context.createElement(BPELPlan.bpelNamespace, "forEach");

    // tz
    forEachElement.setAttribute("counterName", "selectInstanceCounter" + System.currentTimeMillis());
    forEachElement.setAttribute("parallel", "no");

    /*
     * <startCounterValue expressionLanguage="anyURI"?> unsigned-integer-expression </startCounterValue>
     * <finalCounterValue expressionLanguage="anyURI"?> unsigned-integer-expression </finalCounterValue>
     * <completionCondition>? <branches expressionLanguage="anyURI"? successfulBranchesOnly="yes|no"?>?
     * unsigned-integer-expression </branches> </completionCondition> <scope ...>...</scope>
     */

    final Element startCounterValueElement = context.createElement(BPELPlan.bpelNamespace, "startCounterValue");

    startCounterValueElement.setAttribute("expressionLanguage", BPELPlan.xpath2Namespace);

    final Text textSectionStartValue = startCounterValueElement.getOwnerDocument().createTextNode("\"1\"");
    startCounterValueElement.appendChild(textSectionStartValue);

    final Element finalCounterValueElement = context.createElement(BPELPlan.bpelNamespace, "finalCounterValue");

    finalCounterValueElement.setAttribute("expressionLanguage", BPELPlan.xpath2Namespace);

    final Text textSectionFinalValue =
      startCounterValueElement.getOwnerDocument().createTextNode("$" + instanceCountVariableName);
    finalCounterValueElement.appendChild(textSectionFinalValue);

    final Element scopeElement = context.createElement(BPELPlan.bpelNamespace, "scope");

    forEachElement.appendChild(startCounterValueElement);
    forEachElement.appendChild(finalCounterValueElement);
    forEachElement.appendChild(scopeElement);

    return forEachElement;
  }

  public String findInstanceIdVarName(final BPELPlan plan, final String templateId, final boolean isNode) {
    return this.findInstanceIdVarName(this.bpelProcessHandler.getMainVariableNames(plan), templateId, isNode);
  }

  public String findInstanceIdVarName(final BPELScopeActivity templatePlan) {
    String templateId = "";

    boolean isNode = true;
    if (templatePlan.getNodeTemplate() != null) {
      templateId = templatePlan.getNodeTemplate().getId();
    } else {
      templateId = templatePlan.getRelationshipTemplate().getId();
      isNode = false;
    }
    return this.findInstanceIdVarName(templatePlan.getBuildPlan(), templateId, isNode);
  }

  private String findInstanceIdVarName(final List<String> varNames, final String templateId, final boolean isNode) {
    final String instanceURLVarName = (isNode ? "node" : "relationship") + InstanceURLVarKeyword + "_"
      + ModelUtils.makeValidNCName(templateId) + "_";
    for (final String varName : varNames) {
      if (varName.contains(instanceURLVarName)) {
        return varName;
      }
    }
    return null;
  }
}
