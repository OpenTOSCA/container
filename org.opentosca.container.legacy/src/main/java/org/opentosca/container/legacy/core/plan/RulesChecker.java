package org.opentosca.container.legacy.core.plan;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityTemplate.Properties;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.engine.xml.IXMLSerializer;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.opentosca.container.core.tosca.extension.TPlanDTO.InputParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Service
public class RulesChecker {

  private final static Logger LOG = LoggerFactory.getLogger(RulesChecker.class);

  private final IXMLSerializer serializer;

  @Inject
  public RulesChecker(IXMLSerializerService service) {
    this.serializer = service.getXmlSerializer();
  }

  boolean check(final Csar csar, final TServiceTemplate serviceTemplate, final InputParameters inputParameters) {
    LOG.debug("Checking Rules");
    List<TServiceTemplate> stWhiteRuleList;
    List<TServiceTemplate> stBlackRuleList;
    try {
      stWhiteRuleList = getRules(csar, true);
      stBlackRuleList = getRules(csar, false);
    } catch (UserException | SystemException e) {
      e.printStackTrace();
      return false;
    }

    return checkRules(stWhiteRuleList, "white", serviceTemplate, inputParameters)
      && checkRules(stBlackRuleList, "black", serviceTemplate, inputParameters);
  }

  private boolean checkRules(final List<TServiceTemplate> stRuleList, final String ruleType,
                             final TServiceTemplate serviceTemplate,
                             final InputParameters inputParameters) {

    for (final TServiceTemplate stRule : stRuleList) {

      LOG.debug("Checking Rule: " + stRule.getName() + " RuleType: " + ruleType);

      final TTopologyTemplate rule = stRule.getTopologyTemplate();
      final List<TEntityTemplate> templateRuleList = rule.getNodeTemplateOrRelationshipTemplate();
      for (final TEntityTemplate templateRule : templateRuleList) {
        if (!(templateRule instanceof TRelationshipTemplate)) {
          continue;
        }

        final TRelationshipTemplate relationshipRule = (TRelationshipTemplate) templateRule;
        final TNodeTemplate sourceRuleNTemplate = (TNodeTemplate) relationshipRule.getSourceElement().getRef();

        final TNodeTemplate targetRuleNTemplate = (TNodeTemplate) relationshipRule.getTargetElement().getRef();

        boolean ruleCanBeApplied = false;
        // check for types
        if (sourceRuleNTemplate.getId().equals("*")) {
          final List<TNodeTemplate> nodeTemplates = serviceTemplate.getTopologyTemplate().getNodeTemplates();

          for (final TNodeTemplate nodeTemplate : nodeTemplates) {
            final QName nodeType = nodeTemplate.getType();
            // found matching nodetemplate
            if (nodeType.equals(sourceRuleNTemplate.getType())) {
              LOG.debug("Rule " + stRule.getName() + " can be applied to Service Template: " + serviceTemplate + ". Reason: Matching Source NodeTypes.");
              ruleCanBeApplied = true;
            }
          }
          // check for identical IDs
        } else {
          // check source
          if (ToscaEngine.getNodeTemplate(serviceTemplate, sourceRuleNTemplate.getId()).isPresent()) {
            LOG.debug("Rule " + stRule.getName() + " can be applied to Service Template: " + serviceTemplate + ". Reason: Matching Source NodeTemplateIDs.");
            ruleCanBeApplied = true;
          }
        }

        if (!ruleCanBeApplied) {
          LOG.debug("Rule " + stRule.getName() + " can not be applied to Service Template: " + serviceTemplate + ".Thus, rule is ignored.");
          continue;
        }
        if (targetRuleNTemplate.getId().equals("*")) {
          targetRuleNTemplate.getType();

          boolean found = false;
          while (!found) {
            TNodeTemplate targetNT = ToscaEngine.getRelatedNodeTemplate(serviceTemplate, sourceRuleNTemplate, relationshipRule.getType());

            if (targetNT == null) {
              switch (ruleType) {
                case "white":
                  LOG.warn("Target Node Template not found. Rule not fulfilled.");
                  return false;
                case "black":
                  LOG.debug("Nodes are not matching. Rule is fulfilled.");
                  break;
              }
            } else {
              final QName relatedNodeType = targetNT.getType();
              if (relatedNodeType.equals(targetRuleNTemplate.getType())) {
                found = true;
                LOG.debug("Matching Target Node Type found. Node Template: " + targetNT);

                // comparing properties
                if (arePropertiesMatching(sourceRuleNTemplate, inputParameters, targetRuleNTemplate)) {
                  switch (ruleType) {
                    case "white":
                      LOG.debug("Properties are matching. Rule is fulfilled.");
                      break;
                    case "black":
                      LOG.warn("Rule is not fulfilled. Aborting the Provisioning. Reason: Properties are matching.");
                      return false;
                  }
                } else {
                  switch (ruleType) {
                    case "white":
                      LOG.warn("Rule is not fulfilled. Aborting the Provisioning. Reason: Properties are not matching.");
                      return false;
                    case "black":
                      LOG.debug("Properties are not matching. Rule is fulfilled.");
                      break;
                  }
                }
              }
            }
          }

          // check target nodetemplateID
        } else {
          Optional<TNodeTemplate> nodeTemplate = ToscaEngine.getNodeTemplate(serviceTemplate, targetRuleNTemplate.getId());
          if (nodeTemplate.isPresent()) {
            // comparing properties
            if (arePropertiesMatching(sourceRuleNTemplate, inputParameters, targetRuleNTemplate)) {
              switch (ruleType) {
                case "white":
                  LOG.debug("Properties are matching. Rule is fulfilled.");
                  break;
                case "black":
                  LOG.warn("Rule is not fulfilled. Aborting the Provisioning. Reason: Properties are matching.");
                  return false;
              }
            } else {
              switch (ruleType) {
                case "white":
                  LOG.warn("Rule is not fulfilled. Aborting the Provisioning. Reason: Properties are not matching.");
                  return false;
                case "black":
                  LOG.debug("Properties are not matching. Rule is fulfilled.");
                  break;
              }
            }

            // if source is matching but target isn't, abort
          } else {
            switch (ruleType) {
              case "white":
                LOG.warn("Rule is not fulfilled. Aborting the Provisioning. Reason: Source is matching, but target isn't.");
                return false;
              case "black":
                LOG.debug("Nodes are not matching. Rule is fulfilled.");
                break;
            }
          }
        }
      }
    }
    return true;
  }

  boolean areRulesContained(final Csar csar) {
    final Path rulesDirectory = csar.getSaveLocation().resolve("Rules");
    final Path dirWhite = rulesDirectory.resolve("Whitelisting");
    final Path dirBlack = rulesDirectory.resolve("Blacklisting");
    if (Files.exists(dirWhite) || Files.exists(dirBlack)) {
      LOG.debug("Deployment Rules found.");
      return true;
    }
    LOG.debug("No Deployment Rules are defined.");
    return false;
  }

  private  List<TServiceTemplate> getRules(final Csar csar, final boolean whiteRules) throws UserException, SystemException {

    Path dir = csar.getSaveLocation().resolve(whiteRules ? "Rules/Whitelisting" : "Rules/Blacklisting");
    final List<TServiceTemplate> rulesList = new ArrayList<>();

    try (DirectoryStream<Path> rulesFiles = Files.newDirectoryStream(dir, "*.tosca")) {
      for (Iterator<Path> rulesFilesIt = rulesFiles.iterator(); rulesFilesIt.hasNext();) {
        Path rulesFile = rulesFilesIt.next();
        LOG.trace("Rules File: {}", rulesFile.toAbsolutePath().toString());
        final Definitions definitions = serializer.unmarshal(Files.newInputStream(rulesFile));
        definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()
          .stream().map(TServiceTemplate.class::cast)
          .forEach(rulesList::add);
      }
    } catch (IOException e) {
      return Collections.emptyList();
    }

    return rulesList;
  }

  private Map<String, String> getPropertiesOfNodeTemplate(final TNodeTemplate nodeTemplate) {

    LOG.debug("Getting Properties.");
    if (nodeTemplate == null) {
      LOG.debug("The requested NodeTemplate was not found.");
      return null;
    }
    final Properties properties = nodeTemplate.getProperties();
    if (properties == null) {
      LOG.debug("Properties are not set.");
      return null;
    }
    final Object any = properties.getInternalAny();
    if (!(any instanceof Element)) {
      LOG.debug("Properties is not of class Element.");
      return null;
    }

    final Element element = (Element) any;
    final Document doc = element.getOwnerDocument();
    return getPropertiesFromDoc(doc);
  }

  private static Map<String, String> getPropertiesFromDoc(final Document doc) {

    final Map<String, String> propertiesMap = new HashMap<>();

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
            LOG.debug("Property: " + propName + " has Value: " + propValue);
            if (propName != null && propValue != null) {
              propertiesMap.put(node2.getNodeName(), node2.getTextContent());
            }
          }
        }
      }
    }
    return propertiesMap;
  }

  private boolean arePropertiesMatching(final TNodeTemplate relatedNodeTemplate,
                                        final InputParameters inputParameters,
                                        final TNodeTemplate targetRuleNTemplate) {
    final Document propsDoc = ToscaEngine.getNodeTemplateProperties(relatedNodeTemplate);

    final Map<String, String> propertiesMap = getPropertiesFromDoc(propsDoc);
    final Map<String, String> rulesPropertiesMap = getPropertiesOfNodeTemplate(targetRuleNTemplate);

    for (final String name : rulesPropertiesMap.keySet()) {
      final String value = rulesPropertiesMap.get(name);
      if (!propertiesMap.containsKey(name)) {
        continue;
      }
      if (propertiesMap.get(name) == null || propertiesMap.get(name).contains("get_input:")) {
        for (final TParameterDTO para : inputParameters.getInputParameter()) {
          if (para.getName().equals(name) && !para.getValue().equals(value)) {
            LOG.debug("Property " + name + " not matching. " + para.getValue() + " != " + value);
            return false;
          }
        }
      } else if (!propertiesMap.get(name).equals(value)) {
        LOG.debug("Property " + name + " not matching! " + propertiesMap.get(name) + " != " + value);
        return false;
      }
    }

    return true;
  }

}

