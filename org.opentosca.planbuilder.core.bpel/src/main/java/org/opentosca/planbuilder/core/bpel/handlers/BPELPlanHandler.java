package org.opentosca.planbuilder.core.bpel.handlers;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.ode.schemas.dd._2007._03.ObjectFactory;
import org.apache.ode.schemas.dd._2007._03.TDeployment;
import org.apache.ode.schemas.dd._2007._03.TInvoke;
import org.apache.ode.schemas.dd._2007._03.TProcessEvents;
import org.apache.ode.schemas.dd._2007._03.TProvide;
import org.apache.ode.schemas.dd._2007._03.TService;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.Link;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan.VariableType;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.plan.bpel.Deploy;
import org.opentosca.planbuilder.model.plan.bpel.GenericWsdlWrapper;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class is a part of the facade, which is used to work on a BuildPlan. This is class in
 * particular is responsible for the handling of various XML related actions on the whole BPEL
 * process
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPELPlanHandler {

  private final static Logger LOG = LoggerFactory.getLogger(BPELPlanHandler.class);

  private final DocumentBuilderFactory documentBuilderFactory;
  private final DocumentBuilder documentBuilder;

  private final ObjectFactory ddFactory;

  private final BPELScopeHandler bpelScopeHandler;

  /**
   * Default Constructor
   *
   * @throws ParserConfigurationException is thrown when the interal DOM Builders couldn't be
   *                                      initialized
   */
  public BPELPlanHandler() throws ParserConfigurationException {
    this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
    this.documentBuilderFactory.setNamespaceAware(true);
    this.documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
    this.bpelScopeHandler = new BPELScopeHandler();
    this.ddFactory = new ObjectFactory();
  }

  /**
   * Returns a prefix for the given namespace if it is declared in the buildPlan
   *
   * @param namespace the namespace to get the prefix for
   * @return a String containing the prefix, else null
   */
  public String getPrefixForNamespace(final String namespace, final BPELPlan plan) {
    if (plan.namespaceMap.containsValue(namespace)) {
      for (final String key : plan.namespaceMap.keySet()) {
        if (plan.namespaceMap.get(key).equals(namespace)) {
          return key;
        }
      }
    }
    return null;
  }

  public Node importNode(final BPELPlan plan, final Node node) {
    return plan.getBpelDocument().importNode(node, true);
  }

  public String addGlobalStringVariable(final String varNamePrefix, final BPELPlan plan) {
    final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";

    String xsdPrefix = null;
    do {
      xsdPrefix = "xsd" + System.currentTimeMillis();
    } while (!addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, plan));

    String varName = varNamePrefix + System.currentTimeMillis();
    final QName stringXsdDeclQName = new QName(xsdNamespace, "string", xsdPrefix);

    while (!addVariable(varName, BPELPlan.VariableType.TYPE, stringXsdDeclQName, plan)) {
      varName = varNamePrefix + System.currentTimeMillis();
    }
    return varName;
  }

  /**
   * Adds a BPEL copy element given as String to the last assign of the BuildPlan. Note that the
   * string given must be valid
   *
   * @param copyElementString a valid string of a BPEL copy element
   * @param buildPlan         the BuildPlan to add the BPEL copy element to
   * @return true if adding the string was successful, else false
   */
  public boolean addCopyStringToOutputAssign(final String copyElementString, final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Trying to add following copy to outputassign of BuildPlan {}", copyElementString,
      buildPlan.getBpelProcessElement().getAttribute("name"));
    try {
      final InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(copyElementString));
      final Document doc = this.documentBuilder.parse(is);
      Node copyElement = doc.getFirstChild();
      copyElement = buildPlan.getBpelDocument().importNode(copyElement, true);
      if (buildPlan.getBpelMainSequenceOutputAssignElement().getChildNodes().getLength() == 0) {
        buildPlan.getBpelMainSequenceOutputAssignElement().appendChild(copyElement);
      } else {
        final Element outputAssignElement = buildPlan.getBpelMainSequenceOutputAssignElement();
        outputAssignElement.insertBefore(copyElement, outputAssignElement.getFirstChild());
      }
    } catch (final SAXException e) {
      BPELPlanHandler.LOG.error("Failed adding copy to output assign", e);
      return false;
    } catch (final IOException e) {
      BPELPlanHandler.LOG.error("Failed adding copy to output assign", e);
      return false;
    }
    BPELPlanHandler.LOG.debug("Adding copy was successful");
    return true;

  }

  /**
   * Adds an Extension Element to the given BuildPlan
   *
   * @param namespace      the namespace of the extension
   * @param mustUnderstand sets if the extensions must be understood or not
   * @param buildPlan      the BuildPlan to add extension to
   * @return true if adding the extension was successful, else false
   */
  public boolean addExtension(final String namespace, final boolean mustUnderstand, final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Trying to add extension {} with mustUnderstand {} to BuildPlan {}", namespace,
      String.valueOf(mustUnderstand),
      buildPlan.getBpelProcessElement().getAttribute("name"));
    if (hasExtension(namespace, buildPlan)) {
      BPELPlanHandler.LOG.warn("Adding extension failed");
      return false;
    } else {
      final Element extensionElement =
        buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "extension");
      extensionElement.setAttribute("namespace", namespace);
      extensionElement.setAttribute("mustUnderstand", mustUnderstand ? "yes" : "no");
      buildPlan.getBpelExtensionsElement().appendChild(extensionElement);
    }
    BPELPlanHandler.LOG.debug("Adding Extension was successful");
    return true;
  }

  /**
   * Registers and imports a file on a global level into the given BuildPlan
   *
   * @param file      the file with absolute location to add on a global level
   * @param buildPlan the BuildPlan to add the file to
   * @return true if adding the file was successful, else false
   */
  public boolean addImportedFile(final Path file, final BPELPlan buildPlan) {
    return buildPlan.addImportedFile(file);
  }

  /**
   * Adds an ImportElement to the given BuildPlan
   *
   * @param namespace  the namespace of the Import
   * @param location   the location of the document to import
   * @param importType the type of the import
   * @param buildPlan  the BuildPlan to add the Import to
   * @return true if adding the ImportElement was successful, else false
   */
  public boolean addImports(final String namespace, final String location, final BPELPlan.ImportType importType,
                            final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Trying to add Import with namespace {}, location {} and importType {} to BuildPlan {}",
      namespace, location, importType,
      buildPlan.getBpelProcessElement().getAttribute("name"));

    if (this.hasImport(namespace, location, importType, buildPlan)) {
      BPELPlanHandler.LOG.warn("Failed adding Import");
      return false;
    }

    // create new import element
    final Element importElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "import");
    importElement.setAttribute("namespace", namespace);
    importElement.setAttribute("location", location);

    importElement.setAttribute("importType", importType.toString());

    // add to process
    if (buildPlan.getBpelImportElements().isEmpty()) {
      buildPlan.getBpelProcessElement().appendChild(importElement);
    } else {
      buildPlan.getBpelProcessElement().insertBefore(importElement, buildPlan.getBpelImportElements().get(0));
    }
    buildPlan.addBpelImportElement(importElement);

    BPELPlanHandler.LOG.debug("Adding import was successful");
    return true;
  }

  /**
   * Adds an import to the given BuildPlan
   *
   * @param namespace  the namespace of the import
   * @param location   the location attribute of the import
   * @param importType the importType of the import
   * @param buildPlan  the BuildPlan to add the import to
   * @return true if adding the import was successful, else false
   */
  public boolean addImportToBpel(final String namespace, final String location, final String importType,
                                 final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Adding import with namespace {}, location {} and importType to BuildPlan {}",
      namespace, location, importType,
      buildPlan.getBpelProcessElement().getAttribute("name"));
    if (importType.equals(BPELPlan.ImportType.WSDL.toString())) {
      return addImports(namespace, location, BPELPlan.ImportType.WSDL, buildPlan);
    } else if (importType.equals(BPELPlan.ImportType.XSD.toString())) {
      return addImports(namespace, location, BPELPlan.ImportType.XSD, buildPlan);
    } else {
      return false;
    }
  }

  /**
   * Adds an integer variable to the given plan on the global level
   *
   * @param name a name for the variable (no duplicate check)
   * @param plan the plan to add the variable to
   * @return true iff adding the variable was successful
   */
  public boolean addIntegerVariable(final String name, final BPELPlan plan) {
    return addVariable(name, BPELPlan.VariableType.TYPE,
      new QName("http://www.w3.org/2001/XMLSchema", "integer", "xsd"), plan);
  }

  /**
   * Adds an invoke element to the deployment deskriptor of the given BuildPlan
   *
   * @param partnerLinkName the name of the partnerLink the invoke will use
   * @param serviceName     the name of the service that will be invoked
   * @param portName        the port of the invoke
   * @param buildPlan       the BuildPlan to add the invoke to
   * @return true if adding the invoke to the deployment deskriptor was successful, else false
   */

  public boolean addInvokeToDeploy(final String partnerLinkName, final QName serviceName, final String portName,
                                   final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Adding invoke with partnerLink {}, service {} and port {} to BuildPlan {}",
      partnerLinkName, serviceName.toString(), portName,
      buildPlan.getBpelProcessElement().getAttribute("name"));

    for (final TInvoke inv : buildPlan.getDeploymentDeskriptor().getProcess().get(0).getInvoke()) {
      if (inv.getPartnerLink().equals(partnerLinkName)) {
        BPELPlanHandler.LOG.warn("Adding invoke for partnerLink {}, serviceName {} and portName {} failed, there is already a partnerLink with the same Name",
          partnerLinkName, serviceName.toString(), portName);
        return false;
      }
    }
    // set invoke
    final TInvoke invoke = this.ddFactory.createTInvoke();
    invoke.setPartnerLink(partnerLinkName);

    // set service
    final TService service = this.ddFactory.createTService();
    service.setName(serviceName);
    service.setPort(portName);

    invoke.setService(service);

    buildPlan.getDeploymentDeskriptor().getProcess().get(0).getInvoke().add(invoke);

    BPELPlanHandler.LOG.debug("Adding invoke was successful");
    return true;
  }

  /**
   * Adds a link with the given name to the given BuildPlan. Note that links can be added to BPEL
   * flow's without using it in the elements of the flow.
   *
   * @param linkName  the name of the link to set
   * @param buildPlan the BuildPlan to add the link
   * @return true if adding the link was successful, else false
   */
  public boolean addLink(final String linkName, final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Trying to add link {} to BuildPlan {}", linkName,
      buildPlan.getBpelProcessElement().getAttribute("name"));

    if (hasLink(linkName, buildPlan)) {
      BPELPlanHandler.LOG.warn("Adding link failed");
      return false;
    }

    final Element linksElement = buildPlan.getBpelMainFlowLinksElement();
    final Element linkElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "link");

    linkElement.setAttribute("name", linkName);
    linksElement.appendChild(linkElement);
    BPELPlanHandler.LOG.debug("Adding link was successful");
    return true;
  }

  /**
   * Adds a namespace declaration into the given BuildPlan
   *
   * @param prefix    the prefix to use for the namespace
   * @param namespace the namespace
   * @param buildPlan the BuildPlan to set the namespace to
   * @return true if the namespace isn't alread used, else false
   */
  public boolean addNamespaceToBPELDoc(final String prefix, final String namespace, final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Adding namespace {} to BuildPlan {}", namespace, buildPlan.getBpelProcessElement().getAttribute("name"));
    buildPlan.getBpelProcessElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, namespace);
    String test2 = buildPlan.getBpelProcessElement().getAttribute("xmlns:" + prefix);
    return !test2.isEmpty();
  }

  /**
   * Adds a Partnerlink to the given BuildPlan
   *
   * @param partnerLinkName       the name to use for the PartnerLink
   * @param partnerLinkType       the type of the PartnerLink (must be already set)
   * @param myRole                the role of the process inside this partnerLink
   * @param partnerRole           the role of the called entity inside this partnerLink
   * @param initializePartnerRole whether to set initializePartnerRole to 'yes' or 'no'
   * @param buildPlan             the BuildPlan to add the partnerLink to
   * @return true if adding the PartnerLink was successful, else false
   */
  public boolean addPartnerLink(final String partnerLinkName, final QName partnerLinkType, final String myRole,
                                final String partnerRole, final boolean initializePartnerRole,
                                final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Trying to add partnerLink {} with type {}, myRole {}, partnerRole {} and initializePartnerRole {} to BuildPlan {}",
      partnerLinkName, partnerLinkType.toString(), myRole, partnerRole,
      String.valueOf(initializePartnerRole),
      buildPlan.getBpelProcessElement().getAttribute("name"));
    if (hasPartnerLink(partnerLinkName, buildPlan)) {
      BPELPlanHandler.LOG.warn("Failed to add partnerLink");
      return false;
    } else {
      final Element partnerLinksElement = buildPlan.getBpelPartnerLinksElement();
      final Element partnerLinkElement =
        buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "partnerLink");
      partnerLinkElement.setAttribute("name", partnerLinkName);
      partnerLinkElement.setAttribute("partnerLinkType",
        partnerLinkType.getPrefix() + ":" + partnerLinkType.getLocalPart());
      partnerLinkElement.setAttribute("myRole", myRole);
      if (partnerRole != null && !partnerRole.equals("")) {
        partnerLinkElement.setAttribute("partnerRole", partnerRole);
      }

      partnerLinkElement.setAttribute("initializePartnerRole", initializePartnerRole ? "yes" : "no");

      partnerLinksElement.appendChild(partnerLinkElement);
    }
    BPELPlanHandler.LOG.debug("Adding partnerLink was successful");
    return true;
  }

  /**
   * Adds a partnerLinkType which only has one portType (e.g. syncronous)
   *
   * @param partnerLinkTypeName the name for the partnerLinkType
   * @param roleName            the roleName of the Process
   * @param portType            the PortType used in the PartnerLink
   * @param buildPlan           the BuildPlan to add the PartnerLinkType to
   * @return true if adding partnerLink was successful, else false
   */
  public boolean addPartnerLinkType(final String partnerLinkTypeName, final String roleName, final QName portType,
                                    final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Trying to add partnerLinkType {} with roleName {} and portType {} to BuildPlan {}",
      partnerLinkTypeName, roleName, portType.toString(),
      buildPlan.getBpelProcessElement().getAttribute("name"));
    return buildPlan.getWsdl().addPartnerLinkType(partnerLinkTypeName, roleName, portType);
  }

  /**
   * Adds a partnerLinkType which has to PortType (e.g. asynchronous callback)
   *
   * @param partnerLinkTypeName the name for the partnerLinkType
   * @param roleName1           the name for the first role
   * @param portType1           the portType of the first role
   * @param roleName2           the name for the second role
   * @param portType2           the portType for second role
   * @param buildPlan           the BuildPlan to add the partnerLinkType to
   * @return true if adding the partnerLinkType was successful, else false
   */
  public boolean addPartnerLinkType(final String partnerLinkTypeName, final String roleName1, final QName portType1,
                                    final String roleName2, final QName portType2, final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Trying to add partnerLinkType {} with roleName1 {}, portType1 {}, roleName2 {} and portType2 {} to BuildPlan {}",
      partnerLinkTypeName, roleName1, portType1.toString(), roleName2, portType2.toString(),
      buildPlan.getBpelProcessElement().getAttribute("name"));
    return buildPlan.getWsdl().addPartnerLinkType(partnerLinkTypeName, roleName1, portType1, roleName2, portType2);
  }

  public boolean addStringVariable(final String name, final BPELPlan plan) {
    return addVariable(name, BPELPlan.VariableType.TYPE,
      new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"), plan);
  }

  /**
   * Adds a provide element to the deployment deskriptor of the given BuildPlan
   *
   * @param partnerLinkName the name of the partnerlink the provide uses
   * @param serviceName     the service name the provide uses
   * @param portName        the port name the provide uses
   * @param buildPlan       the BuildPlan to add the provide to
   * @return true if adding the provide to the deployment deskriptor was successful, else false
   */
  public boolean addProvideToDeploy(final String partnerLinkName, final QName serviceName, final String portName,
                                    final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Trying to add provide with partnerLink {}, service {} and port {} to BuildPlan {}",
      partnerLinkName, serviceName.toString(), portName,
      buildPlan.getBpelProcessElement().getAttribute("name"));
    for (final TProvide inv : buildPlan.getDeploymentDeskriptor().getProcess().get(0).getProvide()) {
      if (inv.getPartnerLink().equals(partnerLinkName)) {
        BPELPlanHandler.LOG.warn("Adding provide failed");
        return false;
      }
    }
    // set invoke
    final TProvide provide = this.ddFactory.createTProvide();
    provide.setPartnerLink(partnerLinkName);

    // set service
    final TService service = this.ddFactory.createTService();
    service.setName(serviceName);
    service.setPort(portName);

    provide.setService(service);

    buildPlan.getDeploymentDeskriptor().getProcess().get(0).getProvide().add(provide);
    BPELPlanHandler.LOG.debug("Adding provide was successful");
    return true;

  }

  /**
   * Adds a Element of type string to the RequestMessage of the given BuildPlan
   *
   * @param elementName the localName of the element
   * @param buildPlan   the BuildPlan to add the element to
   * @return true if adding the element to RequestMessage was successful, else false
   */
  public boolean addStringElementToPlanRequest(final String elementName, final BPELPlan buildPlan) {
    return buildPlan.getWsdl()
      .addElementToRequestMessage(elementName,
        new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
  }

  /**
   * Adds a element of type string to the ResponseMessage of the given BuildPlan
   *
   * @param elementName the localName of the element
   * @param buildPlan   the BuildPlan to add the element to
   * @return true if adding the element to the ResponseMessage was successful, else false
   */
  public boolean addStringElementToPlanResponse(final String elementName, final BPELPlan buildPlan) {
    return buildPlan.getWsdl()
      .addElementToResponseMessage(elementName,
        new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
  }

  /**
   * Adds the given variable to the process of the given buildplan
   *
   * @param name          the name of the variable
   * @param variableType  the type of the variable, e.g. MessageType
   * @param declarationId the QName of the schema declaration, e.g. {ownSchema}Local. Note: Prefix
   *                      must be set.
   * @param buildPlan     the buildPlan to add the variable to
   * @return true if adding a variable to the plan was successful
   */
  public boolean addVariable(final String name, final BPELPlan.VariableType variableType, final QName declarationId,
                             final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Trying to add variable {} with type {} and declarationId {} to Plan {}", name,
      variableType, declarationId.toString(),
      buildPlan.getBpelProcessElement().getAttribute("name"));
    if (hasVariable(name, buildPlan)) {
      BPELPlanHandler.LOG.warn("Adding variable failed, as it is already declared");
      return false;
    }

    // fetch variables element and create variable element
    final Element variablesElement = buildPlan.getBpelProcessVariablesElement();
    final Element variableElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "variable");

    // set the type and declaration id
    switch (variableType) {
      case MESSAGE:
        variableElement.setAttribute("messageType",
          declarationId.getPrefix() + ":" + declarationId.getLocalPart());
        break;
      case TYPE:
        variableElement.setAttribute("type", declarationId.getPrefix() + ":" + declarationId.getLocalPart());
        break;
      case ELEMENT:
        variableElement.setAttribute("element", declarationId.getPrefix() + ":" + declarationId.getLocalPart());
        break;
      default:
        ;
        break;
    }

    // set name
    variableElement.setAttribute("name", name);

    // append to variables element
    variablesElement.appendChild(variableElement);
    BPELPlanHandler.LOG.debug("Adding variable was successful");
    return true;
  }

  /**
   * Adds a copy from a String variable to the specified Element inside the output message of the
   * given BuildPlan
   *
   * @param variableName      the variableName of the given BuildPlan the value should be assigned from
   * @param outputElementName the Element LocalName inside the outputMessage of the BuildPlan
   * @param buildPlan         the BuildPlan to add the copy to
   * @return true if adding the copy was successful, else false
   */
  public boolean assginOutputWithVariableValue(final String variableName, final String outputElementName,
                                               final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Trying to add copy from variable {} to element {} of OutputMessage of BuildPlan {}",
      variableName, outputElementName,
      buildPlan.getBpelProcessElement().getAttribute("name"));
    final Element outputAssignElement = buildPlan.getBpelMainSequenceOutputAssignElement();
    // create copy elements
    final Element copyElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "copy");
    final Element fromElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "from");
    final Element toElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "to");
    final Element queryElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "query");
    final CDATASection cdataSection = buildPlan.getBpelDocument().createCDATASection("tns:" + outputElementName);

    // set attributes
    fromElement.setAttribute("variable", variableName);
    toElement.setAttribute("variable", "output");
    toElement.setAttribute("part", "payload");

    // set query element
    // e.g. <bpel:query
    // queryLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0"><![CDATA[tns:ami]]></bpel:query>
    queryElement.setAttribute("queryLanguage", "urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0");
    queryElement.appendChild(cdataSection);

    // set everything together
    toElement.appendChild(queryElement);
    copyElement.appendChild(fromElement);
    copyElement.appendChild(toElement);
    outputAssignElement.appendChild(copyElement);
    BPELPlanHandler.LOG.debug("Adding copy was successful");
    return true;
  }

  /**
   * Assigns a String value to a variable inside the assign of the main sequence of the given
   * buildplan
   *
   * @param variableName  the variableName of the variable to set
   * @param variableValue the value to set
   * @param buildPlan     the buildPlan where this has to happen
   * @return true
   */
  public boolean assignInitValueToVariable(final String variableName, final String variableValue,
                                           final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Trying to add assign of variable {} with value {} to BuildPlan {}", variableName,
      variableValue, buildPlan.getBpelProcessElement().getAttribute("name"));
    final Element propertyAssignElement = buildPlan.getBpelMainSequencePropertyAssignElement();
    // create copy element
    final Element copyElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "copy");
    final Element fromElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "from");
    final Element literalElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "literal");
    literalElement.setTextContent(variableValue);
    fromElement.appendChild(literalElement);
    final Element toElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "to");
    toElement.setAttribute("variable", variableName);
    copyElement.appendChild(fromElement);
    copyElement.appendChild(toElement);
    propertyAssignElement.appendChild(copyElement);

    BPELPlanHandler.LOG.debug("Adding assing was successful");
    // TODO check if a false can be made
    return true;
  }

  /**
   * <p>
   * Assigns a value of a variable from the given input request element inside the main entry assign
   * of the given buildPlan.
   * </p>
   *
   * @param variableName           the name of the variable the value should be assigned
   * @param inputVariableLocalName the localName of the element inside the input message of the given
   *                               buildPlan
   * @param buildPlan              the buildPlan to work with
   * @return true iff adding the assign was successful
   */
  public boolean assignVariableValueFromInput(final String variableName, final String inputVariableLocalName,
                                              final BPELPlan buildPlan) {
    final Element propertyAssignElement = buildPlan.getBpelMainSequencePropertyAssignElement();
    // create copy element
    final Element copyElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "copy");
    final Element fromElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "from");

    fromElement.setAttribute("part", "payload");
    fromElement.setAttribute("variable", "input");

    final Element queryElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "query");
    queryElement.setAttribute("queryLanguage", "urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0");

    queryElement.appendChild(buildPlan.getBpelDocument().createCDATASection("//*[local-name()='"
      + inputVariableLocalName + "']/text()"));

    /*
     * <bpel:from part="payload" variable="input"> <bpel:query
     * queryLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0"><![
     * CDATA[//*[local-name()='instanceDataAPIUrl']/text()]]></bpel:query>
     */

    fromElement.appendChild(queryElement);
    final Element toElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "to");
    toElement.setAttribute("variable", variableName);
    copyElement.appendChild(fromElement);
    copyElement.appendChild(toElement);
    propertyAssignElement.appendChild(copyElement);

    BPELPlanHandler.LOG.debug("Adding assing was successful");
    // TODO check if a false can be made
    return true;
  }

  /**
   * Creates a Plan with an empty skeleton for the given ServiceTemplate
   *
   * @param serviceTemplate the ServiceTemplate to generate a Plan Skeleton for
   * @return an empty Plan Skeleton
   */
  public BPELPlan createEmptyBPELPlan(final String processNamespace, final String processName,
                                      final AbstractPlan abstractPlan, final String inputOperationName) {
    BPELPlanHandler.LOG.debug("Creating BuildPlan for ServiceTemplate {}",
      abstractPlan.getServiceTemplate().getQName().toString());

    final BPELPlan buildPlan =
      new BPELPlan(abstractPlan.getId(), abstractPlan.getType(), abstractPlan.getDefinitions(),
        abstractPlan.getServiceTemplate(), abstractPlan.getActivites(), abstractPlan.getLinks());
    ;

    // init wsdl doc
    try {
      buildPlan.setProcessWsdl(new GenericWsdlWrapper(abstractPlan.getType(), inputOperationName));
    } catch (final IOException e) {
      BPELPlanHandler.LOG.error("Internal error while initializing WSDL for BuildPlan", e);
    }

    initializeXMLElements(buildPlan);

    // add new deployment deskriptor
    buildPlan.setDeploymentDeskriptor(new Deploy());

    // set name of process and wsdl
    setId(processNamespace, processName, buildPlan);
    setWsdlId(processNamespace, processName, buildPlan);

    // add import for the process wsdl
    addImports(processNamespace, buildPlan.getWsdl().getFileName(), BPELPlan.ImportType.WSDL, buildPlan);

    // add partnerlink to the process. note/FIXME?: the partnerlinktype of
    // the process itself is alread initialized with setting the name of the
    // process wsdl
    //
    // e.g.<bpel:partnerLink name="client"
    // partnerLinkType="tns:bamoodlebuildplan"
    // myRole="bamoodlebuildplanProvider"
    // partnerRole="bamoodlebuildplanRequester" />
    addPartnerLink("client", new QName(processNamespace, processName, "tns"), processName + "Provider",
      processName + "Requester", true, buildPlan);

    // add input and output variables
    //
    // e.g.
    // <!-- Reference to the message passed as input during initiation -->
    // <bpel:variable name="input"
    // messageType="tns:bamoodlebuildplanRequestMessage" />
    //
    // <!-- Reference to the message that will be sent back to the requester
    // during
    // callback -->
    // <bpel:variable name="VmApache_Endpoint" type="ns1:string" />
    // <bpel:variable name="VmMySql_Endpoint" type="ns1:string"/>
    // <bpel:variable name="output"
    // messageType="tns:bamoodlebuildplanResponseMessage" />

    addVariable("input", BPELPlan.VariableType.MESSAGE,
      new QName(processNamespace, processName + "RequestMessage", "tns"), buildPlan);
    addVariable("output", BPELPlan.VariableType.MESSAGE,
      new QName(processNamespace, processName + "ResponseMessage", "tns"), buildPlan);

    // set the receive and callback invoke elements
    // <bpel:receive name="receiveInput" partnerLink="client"
    // portType="tns:bamoodlebuildplan" operation="initiate"
    // variable="input"
    // createInstance="yes" />
    //
    // <bpel:invoke name="callbackClient"
    // partnerLink="client"
    // portType="tns:bamoodlebuildplanCallback"
    // operation="onResult"
    // inputVariable="output"
    // />
    final Element receiveElement = buildPlan.getBpelMainSequenceReceiveElement();
    setAttribute(receiveElement, "name", "receiveInput");

    setAttribute(receiveElement, "operation", inputOperationName);

    setAttribute(receiveElement, "variable", "input");
    setAttribute(receiveElement, "createInstance", "yes");
    setAttribute(receiveElement, "partnerLink", "client");
    setAttribute(receiveElement, "portType", "tns:" + processName);

    final Element invokeElement = buildPlan.getBpelMainSequenceCallbackInvokeElement();
    setAttribute(invokeElement, "name", "callbackClient");
    setAttribute(invokeElement, "partnerLink", "client");
    // FIXME serious hack here
    setAttribute(invokeElement, "portType", "tns:" + processName + "Callback");
    setAttribute(invokeElement, "operation", "onResult");
    setAttribute(invokeElement, "inputVariable", "output");

    // set deployment deskriptor
    final Deploy deployment = buildPlan.getDeploymentDeskriptor();
    final List<TDeployment.Process> processes = deployment.getProcess();

    // generate process element and set name
    final TDeployment.Process process = this.ddFactory.createTDeploymentProcess();
    process.setName(new QName(processNamespace, processName));

    final TProcessEvents events = this.ddFactory.createTProcessEvents();
    events.setGenerate("all");
    process.setProcessEvents(events);

    // get invokes, generate invoke for callback, add to invokes
    final List<TInvoke> invokes = process.getInvoke();
    final TInvoke callbackInvoke = this.ddFactory.createTInvoke();
    callbackInvoke.setPartnerLink("client");
    // create "callbackservice"
    final TService callbackService = this.ddFactory.createTService();
    // example servicename : Wordpress_buildPlanServiceCallback
    callbackService.setName(new QName(processNamespace, processName + "ServiceCallback"));
    callbackService.setPort(processName + "PortCallbackPort");
    callbackInvoke.setService(callbackService);
    invokes.add(callbackInvoke);

    // get provides, generate provide element, add to process
    final List<TProvide> provides = process.getProvide();
    final TProvide provide = this.ddFactory.createTProvide();
    provide.setPartnerLink("client");
    final TService provideService = this.ddFactory.createTService();
    provideService.setName(new QName(processNamespace, processName + "Service"));
    provideService.setPort(processName + "Port");
    provide.setService(provideService);

    provides.add(provide);

    // add process to processes
    processes.add(process);

    return buildPlan;
  }

  /**
   * Returns a List of Strings which represent all Links declared in the given BuildPlan
   *
   * @param buildPlan the BuildPlan whose declared Links should be returned
   * @return a List of Strings containing all Links of the given BuildPlan
   */
  public List<String> getAllLinks(final BPELPlan buildPlan) {
    final Element flowLinks = buildPlan.getBpelMainFlowLinksElement();
    final List<String> linkNames = new ArrayList<>();
    final NodeList children = flowLinks.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i).getNodeName().equals("link")) {
        linkNames.add(children.item(i).getAttributes().getNamedItem("name").getNodeValue());
      }
      if (children.item(i).getLocalName().equals("link")) {
        linkNames.add(children.item(i).getAttributes().getNamedItem("name").getNodeValue());
      }
    }
    return linkNames;
  }

  /**
   * Returns a List of Names of variables defined in the globla scope of the given plan
   *
   * @param plan a BPEL plan
   * @return a List of Strings containing the names of the variables defined inside the given plan
   */
  public List<String> getMainVariableNames(final BPELPlan plan) {
    final List<String> names = new ArrayList<>();
    final NodeList childNodes = plan.getBpelProcessVariablesElement().getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      final Node child = childNodes.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        final String varName = child.getAttributes().getNamedItem("name").getNodeValue();
        names.add(varName);
      }
    }
    return names;
  }

  /**
   * Returns all TemplateBuildPlans of the given BuildPlan which handle RelationshipTemplates
   *
   * @param buildPlan the BuildPlan to get the TemplateBuildPlans from
   * @return a List of TemplateBuildPlans which handle RelationshipTemplates
   */
  public List<BPELScope> getRelationshipTemplatePlans(final BPELPlan buildPlan) {
    final List<BPELScope> relationshipPlans = new ArrayList<>();
    for (final BPELScope template : buildPlan.getTemplateBuildPlans()) {
      if (this.bpelScopeHandler.isRelationshipTemplatePlan(template)) {
        relationshipPlans.add(template);
      }
    }
    return relationshipPlans;
  }

  /**
   * Returns a TemplateBuildPlan which handles the Template with the given id
   *
   * @param id        the id of template inside a TopologyTemplate
   * @param buildPlan the BuildPlan to look in
   * @return a TemplateBuildPlan if it handles a Template with the given id, else null
   */
  public BPELScope getTemplateBuildPlanById(final String id, final BPELPlan buildPlan) {
    for (final BPELScope template : buildPlan.getTemplateBuildPlans()) {
      // FIXME it looks a bit hacky.. it looks even more hacky if you look
      // at getRelationshipTemplatePlans(..), the ifs
      if (template.getNodeTemplate() != null && template.getNodeTemplate().getId().equals(id)) {
        return template;
      }
      if (template.getRelationshipTemplate() != null && template.getRelationshipTemplate().getId().equals(id)) {
        return template;
      }
    }
    return null;
  }

  /**
   * Chechs whether the given BuildPlan has a extension with the given namespace
   *
   * @param namespace the namespace of the extension
   * @param buildPlan the BuildPlan to check with
   * @return true if the BuidlPlan has an extension with the given namespace, else false
   */
  private boolean hasExtension(final String namespace, final BPELPlan buildPlan) {
    return ModelUtils.hasChildElementWithAttribute(buildPlan.getBpelExtensionsElement(), "namespace", namespace);
  }

  /**
   * Checks whether an ImportElement is already add
   *
   * @param namespace the namespace of the import
   * @param location  the location of the import
   * @param type      the type of the import
   * @param buildPlan the BuildPlan to check on
   * @return true if the BuildPlan already has the specified import, else false
   */
  public boolean hasImport(final String namespace, final String location, final BPELPlan.ImportType type,
                           final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Checking if import with namespace " + namespace + " and location " + location
      + " is already imported");
    for (final Element importElement : buildPlan.getBpelImportElements()) {
      BPELPlanHandler.LOG.debug("Checking import element");
      int checkInt = 0;
      if (importElement.hasAttribute("namespace") && importElement.getAttribute("namespace").equals(namespace)) {
        BPELPlanHandler.LOG.debug("Found import with same namespace");
        checkInt++;
      }
      if (importElement.hasAttribute("location") && importElement.getAttribute("location").equals(location)) {
        BPELPlanHandler.LOG.debug("Found import with same location");
        checkInt++;
      }
      if (checkInt == 2) {
        return true;
      }
      if (importElement.hasAttribute("type") && importElement.getAttribute("type").equals(type.toString())) {
        BPELPlanHandler.LOG.debug("Found import with same type");
        checkInt++;
      }
      if (checkInt == 3) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns if the given import is already imported in the given BuildPlan
   *
   * @param namespace  the namespace of the import
   * @param location   the location of the import
   * @param importType the importType of the import
   * @param buildPlan  the BuildPlan to look inside for the import
   * @return true if the import is already present in the given BuildPlan, else false
   */
  public boolean hasImport(final String namespace, final String location, final String importType,
                           final BPELPlan buildPlan) {
    if (importType.equals(BPELPlan.ImportType.WSDL.toString())) {
      return this.hasImport(namespace, location, BPELPlan.ImportType.WSDL, buildPlan);
    } else if (importType.equals(BPELPlan.ImportType.XSD.toString())) {
      return this.hasImport(namespace, location, BPELPlan.ImportType.XSD, buildPlan);
    } else {
      return false;
    }
  }

  /**
   * Checks whether the given BuildPlan has a link with the given name
   *
   * @param linkName  the name of the link to check with
   * @param buildPlan the BuildPlan to check for the link
   * @return true if the BuildPlan has a link with the given name, else false
   */
  private boolean hasLink(final String linkName, final BPELPlan buildPlan) {
    return ModelUtils.hasChildElementWithAttribute(buildPlan.getBpelMainFlowLinksElement(), "name", linkName);
  }

  /**
   * Checks whether the given BuildPlan has partnerLink with the given name
   *
   * @param name      the name of the partnerLink
   * @param buildPlan the BuildPlan to check in
   * @return true if the BuildPlan has partnerLink with the given name
   */
  private boolean hasPartnerLink(final String name, final BPELPlan buildPlan) {
    return ModelUtils.hasChildElementWithAttribute(buildPlan.getBpelPartnerLinksElement(), "name", name);
  }

  /**
   * Checks whether the given BuildPlan has a variable with the given name
   *
   * @param name      the name of the variable
   * @param buildPlan the BuildPlan to check in
   * @return true if the given BuildPlan has a variable with the given name, else false
   */
  private boolean hasVariable(final String name, final BPELPlan buildPlan) {
    return ModelUtils.hasChildElementWithAttribute(buildPlan.getBpelProcessVariablesElement(), "name", name);
  }

  public void initializeBPELSkeleton(final BPELPlan plan, final String csarName) {
    plan.setCsarName(csarName);

    final Map<AbstractActivity, BPELScope> abstract2bpelMap = new HashMap<>();

    for (final AbstractActivity activity : plan.getActivites()) {
      if (activity instanceof NodeTemplateActivity) {
        final NodeTemplateActivity ntActivity = (NodeTemplateActivity) activity;
        final BPELScope newEmpty3SequenceScopeBPELActivity =
          this.bpelScopeHandler.createTemplateBuildPlan(ntActivity, plan);
        plan.addTemplateBuildPlan(newEmpty3SequenceScopeBPELActivity);
        abstract2bpelMap.put(ntActivity, newEmpty3SequenceScopeBPELActivity);
      } else if (activity instanceof RelationshipTemplateActivity) {
        final RelationshipTemplateActivity rtActivity = (RelationshipTemplateActivity) activity;
        final BPELScope newEmpty3SequenceScopeBPELActivity =
          this.bpelScopeHandler.createTemplateBuildPlan(rtActivity, plan);
        plan.addTemplateBuildPlan(newEmpty3SequenceScopeBPELActivity);
        abstract2bpelMap.put(rtActivity, newEmpty3SequenceScopeBPELActivity);
      }
    }

    plan.setAbstract2BPELMapping(abstract2bpelMap);

    // connect the templates
    initializeConnectionsAsLinkInBPELPlan(plan);
  }

  private void initializeConnectionsAsLinkInBPELPlan(final BPELPlan plan) {
    for (final Link link : plan.getLinks()) {
      final BPELScope source = plan.getAbstract2BPEL().get(link.getSrcActiv());
      final BPELScope target = plan.getAbstract2BPEL().get(link.getTrgActiv());

      if (source == null | target == null) {
        continue;
      }

      final String linkName = "connection_"
        + (source.getNodeTemplate() != null ? source.getNodeTemplate().getId()
        : source.getRelationshipTemplate().getId())
        + "_" + (target.getNodeTemplate() != null ? target.getNodeTemplate().getId()
        : target.getRelationshipTemplate().getId());
      addLink(linkName, plan);

      this.bpelScopeHandler.connect(source, target, linkName);

    }
  }

  public boolean assignInitValueToVariable(Variable var, String value, BPELPlan plan) {
    return assignInitValueToVariable(var.getVariableName(), value, plan);
  }

  /**
   * Initializes the XML DOM elements inside the given BuildPlan
   *
   * @param newBuildPlan a new BuildPlan
   */
  public void initializeXMLElements(final BPELPlan newBuildPlan) {
    newBuildPlan.setBpelDocument(this.documentBuilder.newDocument());

    // initialize processElement and append to document
    newBuildPlan.setBpelProcessElement(newBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace,
      "process"));
    newBuildPlan.getBpelDocument().appendChild(newBuildPlan.getBpelProcessElement());

    // FIXME declare xml schema namespace
    newBuildPlan.getBpelProcessElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd",
      "http://www.w3.org/2001/XMLSchema");

    // init import files list
    newBuildPlan.setImportedFiles(new HashSet<Path>());

    // initialize and append extensions element to process
    newBuildPlan.setBpelExtensionsElement(newBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace,
      "extensions"));
    newBuildPlan.getBpelProcessElement().appendChild(newBuildPlan.getBpelExtensionsElement());

    // init and append imports element
    newBuildPlan.setBpelImportElements(new ArrayList<Element>());

    // TODO this is here to not to forget that the imports elements aren't
    // attached, cause there are none and import elements aren't nested in a
    // list element
    //
    // this.bpelImportsElement = this.bpelProcessDocument.createElementNS(
    // BuildPlan.bpelNamespace, "imports");
    // this.bpelProcessElement.appendChild(bpelImportsElement);

    // init and append partnerlink element
    newBuildPlan.setBpelPartnerLinksElement(newBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace,
      "partnerLinks"));
    newBuildPlan.getBpelProcessElement().appendChild(newBuildPlan.getBpelPartnerLinksElement());

    // initialize and append variables element
    newBuildPlan.setBpelProcessVariablesElement(newBuildPlan.getBpelDocument()
      .createElementNS(BPELPlan.bpelNamespace, "variables"));
    newBuildPlan.getBpelProcessElement().appendChild(newBuildPlan.getBpelProcessVariablesElement());

    // init and append main sequence to process element
    newBuildPlan.setBpelMainSequenceElement(newBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace,
      "sequence"));
    newBuildPlan.getBpelProcessElement().appendChild(newBuildPlan.getBpelMainSequenceElement());

    // init and append main sequence receive element to main sequence
    // element
    newBuildPlan.setBpelMainSequenceReceiveElement(newBuildPlan.getBpelDocument()
      .createElementNS(BPELPlan.bpelNamespace, "receive"));
    newBuildPlan.getBpelMainSequenceElement().appendChild(newBuildPlan.getBpelMainSequenceReceiveElement());

    // init and append main sequence property assign element to main
    // sequence element
    newBuildPlan.setBpelMainSequencePropertyAssignElement(newBuildPlan.getBpelDocument()
      .createElementNS(BPELPlan.bpelNamespace,
        "assign"));
    newBuildPlan.getBpelMainSequenceElement().appendChild(newBuildPlan.getBpelMainSequencePropertyAssignElement());

    // init and append main sequence flow element to main sequence element
    newBuildPlan.setBpelMainFlowElement(newBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace,
      "flow"));
    newBuildPlan.getBpelMainSequenceElement().appendChild(newBuildPlan.getBpelMainFlowElement());

    // init and append flow links element
    newBuildPlan.setBpelMainFlowLinksElement(newBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace,
      "links"));
    newBuildPlan.getBpelMainFlowElement().appendChild(newBuildPlan.getBpelMainFlowLinksElement());

    // init and append output assign element
    newBuildPlan.setBpelMainSequenceOutputAssignElement(newBuildPlan.getBpelDocument()
      .createElementNS(BPELPlan.bpelNamespace,
        "assign"));
    newBuildPlan.getBpelMainSequenceElement().appendChild(newBuildPlan.getBpelMainSequenceOutputAssignElement());

    // init and append main sequence callback invoke element to main
    // sequence element
    newBuildPlan.setBpelMainSequenceCallbackInvokeElement(newBuildPlan.getBpelDocument()
      .createElementNS(BPELPlan.bpelNamespace,
        "invoke"));
    newBuildPlan.getBpelMainSequenceElement().appendChild(newBuildPlan.getBpelMainSequenceCallbackInvokeElement());
  }

  /**
   * Checks whether the variable given by name is initialized at the beginning of the plan
   *
   * @param variableName the name of the variable to check for
   * @param buildPlan    the BuildPlan to check in
   * @return true if there is a copy element inside the main assign element of the given BuildPlan
   */
  public boolean isVariableInitialized(final String variableName, final BPELPlan buildPlan) {
    final Element propertyAssignElement = buildPlan.getBpelMainSequencePropertyAssignElement();
    // get all copy elements
    for (int i = 0; i < propertyAssignElement.getChildNodes().getLength(); i++) {
      if (propertyAssignElement.getChildNodes().item(i).getLocalName().equals("copy")) {
        final Node copyElement = propertyAssignElement.getChildNodes().item(i);
        for (int j = 0; j < copyElement.getChildNodes().getLength(); j++) {
          if (copyElement.getChildNodes().item(j).getLocalName().equals("to")) {
            final Node toElement = copyElement.getChildNodes().item(j);
            if (toElement.getAttributes().getNamedItem("variable").getNodeValue().equals(variableName)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * Registers an extension in the given BuildPlan
   *
   * @param namespace      the namespace of the extension
   * @param mustUnderstand sets if the extension must be understood
   * @param buildPlan      the BuildPlan to add to the given BuildPlan
   * @return true if adding the extension was successful, else false
   */
  public boolean registerExtension(final String namespace, final boolean mustUnderstand, final BPELPlan buildPlan) {
    return addExtension(namespace, mustUnderstand, buildPlan);
  }

  /**
   * Removes a link with given name from the given BuildPlan
   *
   * @param link      the name of the link to remove
   * @param buildPlan the BuildPlan to remove the link from
   */
  public void removeLink(final String link, final BPELPlan buildPlan) {
    final NodeList children = buildPlan.getBpelMainFlowLinksElement().getChildNodes();
    Node toRemove = null;
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(link)) {
        toRemove = children.item(i);
      }
      if (children.item(i).getAttributes().getNamedItem("name").getTextContent().equals(link)) {
        toRemove = children.item(i);
      }

    }
    if (toRemove != null) {
      buildPlan.getBpelMainFlowLinksElement().removeChild(toRemove);
    }

  }

  /**
   * Sets the given element with the given attribute and value
   *
   * @param element   the Element to set
   * @param attrName  the attribute name to set
   * @param attrValue the value for the attribute
   */
  public void setAttribute(final Element element, final String attrName, final String attrValue) {
    BPELPlanHandler.LOG.debug("Setting attribute {} with value {} on Element {}", attrName, attrValue,
      element.getLocalName());
    // TODO check why this method is here
    element.setAttribute(attrName, attrValue);
  }

  /**
   * Sets the name and namespace of the given buildPlan
   *
   * @param namespace the namespace to set
   * @param name      the name to set
   * @param buildPlan the buildPlan to change
   */
  public void setId(final String namespace, final String name, final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Setting name {} with namespace {} BuidlPlan", name, namespace);
    // change the bpel document
    buildPlan.getBpelProcessElement().setAttribute("name", name);
    buildPlan.getBpelProcessElement().setAttribute("targetNamespace", namespace);
    // FIXME should work if we ask the internet, but we will see later
    buildPlan.getBpelProcessElement().setAttribute("xmlns:tns", namespace);
  }

  /**
   * Sets ID's on the WSDL of the given BuildPlan
   *
   * @param namespace the namespace for the WSDL
   * @param name      the name for the WSDL
   * @param buildPlan the BuildPlan to work on
   */
  public void setWsdlId(final String namespace, final String name, final BPELPlan buildPlan) {
    BPELPlanHandler.LOG.debug("Setting name {} and namespace {} of WSDL of BuildPlan {}", name, namespace,
      buildPlan.getBpelProcessElement().getAttribute("name"));
    final GenericWsdlWrapper wsdl = buildPlan.getWsdl();
    wsdl.setId(namespace, name);
  }

  /**
   * Imports the given QName Namespace into the BuildPlan
   *
   * @param bpelPlanContext TODO
   * @param qname           a QName to import
   * @return the QName with set prefix
   */
  public QName importNamespace(final QName qname, final BPELPlan plan) {
    String prefix = qname.getPrefix();
    final String namespace = qname.getNamespaceURI();
    boolean prefixInUse = false;
    boolean namespaceInUse = false;

    // check if prefix is in use
    if (prefix != null && !prefix.isEmpty()) {
      prefixInUse = plan.namespaceMap.containsKey(prefix);
    }

    // check if namespace is in use
    if (namespace != null && !namespace.isEmpty()) {
      namespaceInUse = plan.namespaceMap.containsValue(namespace);
    }

    // TODO refactor this whole thing
    if (prefixInUse & namespaceInUse) {
      // both is already registered, this means we set the prefix of the
      // given qname to the prefix used in the system
      for (final String key : plan.namespaceMap.keySet()) {
        if (plan.namespaceMap.get(key).equals(namespace)) {
          prefix = key;
        }
      }
    } else if (!prefixInUse & namespaceInUse) {
      // the prefix isn't in use, but the namespace is, re-set the prefix
      for (final String key : plan.namespaceMap.keySet()) {
        if (plan.namespaceMap.get(key).equals(namespace)) {
          prefix = key;
        }
      }
    } else if (!prefixInUse & !namespaceInUse) {
      // just add the namespace and prefix to the system
      if (prefix == null || prefix.isEmpty()) {
        // generate new prefix
        prefix = "ns" + plan.namespaceMap.keySet().size();
      }
      plan.namespaceMap.put(prefix, namespace);

      addNamespaceToBPELDoc(prefix, namespace, plan);

    } else {
      if (prefix == null || prefix.isEmpty()) {
        // generate new prefix
        prefix = "ns" + plan.namespaceMap.keySet().size();
      }
      plan.namespaceMap.put(prefix, namespace);
      addNamespaceToBPELDoc(prefix, namespace, plan);
    }
    return new QName(namespace, qname.getLocalPart(), prefix);
  }

  public String createAnyTypeVar(final BPELPlan plan) {
    // add XMLSchema Namespace for the logic
    final String xsdPrefix = "xsd" + System.currentTimeMillis();
    final String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
    this.addNamespaceToBPELDoc(xsdPrefix, xsdNamespace, plan);
    // create Response Variable for interaction
    final String varName = "anyTypeVariable" + System.currentTimeMillis();
    this.addVariable(varName, VariableType.TYPE, new QName(xsdNamespace, "anyType", xsdPrefix), plan);
    return varName;
  }
}
