package org.opentosca.container.core.impl.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityTemplate.Properties;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.opentosca.container.core.tosca.extension.TPlanDTO.InputParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RulesChecker {

  public static ICoreFileService handler;

  private final static Logger LOG = LoggerFactory.getLogger(RulesChecker.class);

  static boolean check(final CSARID csarID, final QName serviceTemplateID, final InputParameters inputParameters) {

    RulesChecker.LOG.debug("Checking Rules");

    List<TServiceTemplate> stWhiteRuleList;
    List<TServiceTemplate> stBlackRuleList;
    try {
      stWhiteRuleList = getRules(csarID, true);
      stBlackRuleList = getRules(csarID, false);
    } catch (UserException | SystemException e) {
      e.printStackTrace();
      return false;
    }

    final boolean whiteRulesFulfilled =
      checkRules(stWhiteRuleList, "white", csarID, serviceTemplateID, inputParameters);
    final boolean blackRulesFulfilled =
      checkRules(stBlackRuleList, "black", csarID, serviceTemplateID, inputParameters);

    return whiteRulesFulfilled && blackRulesFulfilled;
  }

  private static boolean checkRules(final List<TServiceTemplate> stRuleList, final String ruleType,
                                    final CSARID csarID, final QName serviceTemplateID,
                                    final InputParameters inputParameters) {

    for (final TServiceTemplate stRule : stRuleList) {

      RulesChecker.LOG.debug("Checking Rule: " + stRule.getName() + " RuleType: " + ruleType);

      final TTopologyTemplate rule = stRule.getTopologyTemplate();

      final List<TEntityTemplate> templateRuleList = rule.getNodeTemplateOrRelationshipTemplate();

      for (final TEntityTemplate templateRule : templateRuleList) {

        if (templateRule instanceof TRelationshipTemplate) {

          final TRelationshipTemplate relationshipRule = (TRelationshipTemplate) templateRule;

          final TNodeTemplate sourceRuleNTemplate =
            (TNodeTemplate) relationshipRule.getSourceElement().getRef();

          final TNodeTemplate targetRuleNTemplate =
            (TNodeTemplate) relationshipRule.getTargetElement().getRef();

          boolean ruleCanBeApplied = false;

          // check for types
          if (sourceRuleNTemplate.getId().equals("*")) {

            final List<String> nodeTemplates =
              ServiceProxy.toscaEngineService.getNodeTemplatesOfServiceTemplate(csarID,
                serviceTemplateID);

            for (final String nodeTemplate : nodeTemplates) {

              final QName nodeType =
                ServiceProxy.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
                  nodeTemplate);

              // found matching nodetemplate
              if (nodeType.equals(sourceRuleNTemplate.getType())) {
                RulesChecker.LOG.debug("Rule " + stRule.getName()
                  + " can be applied to Service Template: " + serviceTemplateID
                  + ". Reason: Matching Source NodeTypes.");

                ruleCanBeApplied = true;
              }
            }

            // check for identical IDs
          } else {

            // check source
            if (ServiceProxy.toscaEngineService.doesNodeTemplateExist(csarID, serviceTemplateID,
              sourceRuleNTemplate.getId())) {

              RulesChecker.LOG.debug("Rule " + stRule.getName() + " can be applied to Service Template: "
                + serviceTemplateID + ". Reason: Matching Source NodeTemplateIDs.");

              ruleCanBeApplied = true;
            }
          }

          if (ruleCanBeApplied) {

            if (targetRuleNTemplate.getId().equals("*")) {

              targetRuleNTemplate.getType();

              boolean found = false;

              while (!found) {

                final String relatedNodeTemplate =
                  ServiceProxy.toscaEngineService.getRelatedNodeTemplateID(csarID, serviceTemplateID,
                    sourceRuleNTemplate.getId(), relationshipRule.getType());

                if (relatedNodeTemplate != null) {
                  final QName relatedNodeType =
                    ServiceProxy.toscaEngineService.getNodeTypeOfNodeTemplate(csarID,
                      serviceTemplateID, relatedNodeTemplate);

                  if (relatedNodeType.equals(targetRuleNTemplate.getType())) {
                    found = true;
                    RulesChecker.LOG.debug("Matching Target Node Type found. Node Template: "
                      + relatedNodeTemplate);

                    // comparing properties
                    if (arePropertiesMatching(csarID, serviceTemplateID, relatedNodeTemplate,
                      inputParameters, targetRuleNTemplate)) {

                      switch (ruleType) {
                        case "white":
                          RulesChecker.LOG.debug("Properties are matching. Rule is fulfilled.");
                          break;
                        case "black":
                          RulesChecker.LOG.warn("Rule is not fulfilled. Aborting the Provisioning. Reason: Properties are matching.");
                          return false;
                      }
                    } else {
                      switch (ruleType) {
                        case "white":
                          RulesChecker.LOG.warn("Rule is not fulfilled. Aborting the Provisioning. Reason: Properties are not matching.");
                          return false;
                        case "black":
                          RulesChecker.LOG.debug("Properties are not matching. Rule is fulfilled.");
                          break;
                      }
                    }
                  }

                } else {
                  switch (ruleType) {
                    case "white":
                      RulesChecker.LOG.warn("Target Node Template not found. Rule not fulfilled.");
                      return false;
                    case "black":
                      RulesChecker.LOG.debug("Nodes are not matching. Rule is fulfilled.");
                      break;
                  }
                }
              }

              // check target nodetemplateID
            } else {

              if (ServiceProxy.toscaEngineService.doesNodeTemplateExist(csarID, serviceTemplateID,
                targetRuleNTemplate.getId())) {

                // comparing properties
                if (arePropertiesMatching(csarID, serviceTemplateID, targetRuleNTemplate.getId(),
                  inputParameters, targetRuleNTemplate)) {

                  switch (ruleType) {
                    case "white":
                      RulesChecker.LOG.debug("Properties are matching. Rule is fulfilled.");
                      break;
                    case "black":
                      RulesChecker.LOG.warn("Rule is not fulfilled. Aborting the Provisioning. Reason: Properties are matching.");
                      return false;
                  }
                } else {
                  switch (ruleType) {
                    case "white":
                      RulesChecker.LOG.warn("Rule is not fulfilled. Aborting the Provisioning. Reason: Properties are not matching.");
                      return false;
                    case "black":
                      RulesChecker.LOG.debug("Properties are not matching. Rule is fulfilled.");
                      break;
                  }
                }

                // if source is matching but target isn't, abort
              } else {
                switch (ruleType) {
                  case "white":
                    RulesChecker.LOG.warn("Rule is not fulfilled. Aborting the Provisioning. Reason: Source is matching, but target isn't.");
                    return false;
                  case "black":
                    RulesChecker.LOG.debug("Nodes are not matching. Rule is fulfilled.");
                    break;
                }
              }
            }

          } else {
            RulesChecker.LOG.debug("Rule " + stRule.getName() + " can not be applied to Service Template: "
              + serviceTemplateID + ".Thus, rule is ignored.");
          }
        }
      }
    }
    return true;
  }

  static boolean areRulesContained(final CSARID csarID) {

    CSARContent content;
    try {
      content = RulesChecker.handler.getCSAR(csarID);

      final AbstractDirectory dirWhite = content.getDirectory("Rules/Whitelisting");
      final AbstractDirectory dirBlack = content.getDirectory("Rules/Blacklisting");
      if (dirWhite != null || dirBlack != null) {
        RulesChecker.LOG.debug("Deployment Rules found.");
        return true;
      }
      RulesChecker.LOG.debug("No Deployment Rules are defined.");
      return false;
    } catch (final UserException e) {
      e.printStackTrace();
      return false;
    }
  }

  private static List<TServiceTemplate> getRules(final CSARID csarID, final boolean whiteRules) throws UserException,
    SystemException {

    final List<TServiceTemplate> rulesList = new ArrayList<>();

    final CSARContent content = RulesChecker.handler.getCSAR(csarID);
    AbstractDirectory dir;
    if (whiteRules) {
      dir = content.getDirectory("Rules/Whitelisting");
    } else {
      dir = content.getDirectory("Rules/Blacklisting");
    }

    if (dir != null) {
      final Set<AbstractFile> files = dir.getFiles();

      if (files != null) {
        for (final AbstractFile file : files) {

          RulesChecker.LOG.debug("Filepath: " + file.getPath());
          RulesChecker.LOG.debug("File: " + file.getName());

          if (file.getName().endsWith("tosca")) {

            RulesChecker.LOG.debug("Rule found");

            final Definitions def =
              ServiceProxy.xmlSerializerService.getXmlSerializer().unmarshal(file.getFileAsInputStream());

            final List<TExtensibleElements> elementsList =
              def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();

            for (final TExtensibleElements elements : elementsList) {

              final TServiceTemplate st = (TServiceTemplate) elements;

              rulesList.add(st);
            }
          }
        }
      }
    }
    return rulesList;
  }

  private static HashMap<String, String> getPropertiesOfNodeTemplate(final TNodeTemplate nodeTemplate) {

    RulesChecker.LOG.debug("Getting Properties.");
    if (nodeTemplate != null) {
      final Properties properties = nodeTemplate.getProperties();
      if (properties != null) {
        final Object any = properties.getAny();
        if (any instanceof Element) {
          final Element element = (Element) any;
          final Document doc = element.getOwnerDocument();
          return getPropertiesFromDoc(doc);
        } else {
          RulesChecker.LOG.debug("Properties is not of class Element.");
        }
      } else {
        RulesChecker.LOG.debug("Properties are not set.");
      }
    } else {
      RulesChecker.LOG.debug("The requested NodeTemplate was not found.");
    }

    return null;
  }

  private static HashMap<String, String> getPropertiesFromDoc(final Document doc) {

    final HashMap<String, String> propertiesMap = new HashMap<>();

    final NodeList nodeList = doc.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      final Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        final NodeList nodeList2 = node.getChildNodes();
        for (int i2 = 0; i2 < nodeList2.getLength(); i2++) {
          final Node node2 = nodeList2.item(i2);
          if (node2.getNodeType() == Node.ELEMENT_NODE) {
            final String propName = node2.getNodeName();
            final String propValue = node2.getTextContent();
            RulesChecker.LOG.debug("Property: " + propName + " has Value: " + propValue);
            if (propName != null && propValue != null) {
              propertiesMap.put(node2.getNodeName(), node2.getTextContent());
            }
          }
        }
      }
    }
    return propertiesMap;
  }

  private static boolean arePropertiesMatching(final CSARID csarID, final QName serviceTemplateID,
                                               final String relatedNodeTemplate,
                                               final InputParameters inputParameters,
                                               final TNodeTemplate targetRuleNTemplate) {

    final Document propsDoc =
      ServiceProxy.toscaEngineService.getPropertiesOfTemplate(csarID, serviceTemplateID, relatedNodeTemplate);

    final HashMap<String, String> propertiesMap = getPropertiesFromDoc(propsDoc);
    final HashMap<String, String> rulesPropertiesMap = getPropertiesOfNodeTemplate(targetRuleNTemplate);

    for (final String name : rulesPropertiesMap.keySet()) {
      final String value = rulesPropertiesMap.get(name);
      if (propertiesMap.containsKey(name)) {
        if (propertiesMap.get(name) == null || propertiesMap.get(name).contains("get_input:")) {
          for (final TParameterDTO para : inputParameters.getInputParameter()) {
            if (para.getName().equals(name)) {
              if (!para.getValue().equals(value)) {
                RulesChecker.LOG.debug("Property " + name + " not matching. " + para.getValue() + " != "
                  + value);
                return false;
              }
            }
          }
        } else if (!propertiesMap.get(name).equals(value)) {
          RulesChecker.LOG.debug("Property " + name + " not matching! " + propertiesMap.get(name) + " != "
            + value);
          return false;
        }
      }
    }
    return true;
  }

}

