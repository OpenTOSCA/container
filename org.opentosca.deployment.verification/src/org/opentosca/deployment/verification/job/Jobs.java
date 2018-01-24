package org.opentosca.deployment.verification.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.utils.Utils;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

/**
 * Utility class used by Job implementations.
 */
public abstract class Jobs {

  public static String resolveHostname(final Map<String, String> properties) {
    String hostname = properties.get("hostname");
    if (Strings.isNullOrEmpty(hostname)) {
      hostname = properties.get("host");
    }
    if (Strings.isNullOrEmpty(hostname)) {
      hostname = properties.get("vmip");
    }
    if (Strings.isNullOrEmpty(hostname)) {
      hostname = properties.get("ipaddress");
    }
    if (Strings.isNullOrEmpty(hostname)) {
      hostname = properties.get("ip_address");
    }
    if (Strings.isNullOrEmpty(hostname)) {
      hostname = properties.get("containerip");
    }
    return hostname;
  }

  public static Integer resolvePort(final Map<String, String> properties) {
    String port = properties.get("port");
    if (Strings.isNullOrEmpty(port)) {
      port = properties.get("sshport");
    }
    if (Strings.isNullOrEmpty(port)) {
      port = properties.get("dbmsport");
    }
    return Ints.tryParse(port);
  }

  public static void resolveInfrastructureNodes(final NodeTemplateInstance nodeTemplateInstance,
      final VerificationContext context, final Set<NodeTemplateInstance> infrastructureNodes) {

    List<RelationshipTemplateInstance> outgoingRelations =
        nodeTemplateInstance.getOutgoingRelations().stream()
            .filter(r -> r.getTemplateType().equals(Utils.TOSCABASETYPE_DEPENDSON)
                || r.getTemplateType().equals(Utils.TOSCABASETYPE_DEPLOYEDON)
                || r.getTemplateType().equals(Utils.TOSCABASETYPE_HOSTEDON))
            .collect(Collectors.toList());

    for (RelationshipTemplateInstance r : outgoingRelations) {

      final NodeTemplateInstance target = r.getTarget();
      final AbstractNodeTemplate targetTemplate = context.getNodeTemplate(target);

      if (org.opentosca.container.core.tosca.convention.Utils
          .isSupportedInfrastructureNodeType(Utils.getNodeBaseType(targetTemplate))
          || org.opentosca.container.core.tosca.convention.Utils
              .isSupportedCloudProviderNodeType(Utils.getNodeBaseType(targetTemplate))) {
        infrastructureNodes.add(target);
      }

      resolveInfrastructureNodes(target, context, infrastructureNodes);
    }
  }

  public static Map<String, String> mergePlanProperties(final Set<NodeTemplateInstance> nodes) {
    return nodes.stream().map(n -> n.getPropertiesAsMap()).collect(HashMap::new, Map::putAll,
        Map::putAll);
  }

  public static String resolveUrl(final Map<String, String> properties) {
    return properties.get("selfserviceapplicationurl");
  }
}
