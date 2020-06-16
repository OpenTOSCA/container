package org.opentosca.deployment.checks;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.planbuilder.model.utils.ModelUtils;

import org.opentosca.container.core.tosca.convention.Utils;

/**
 * Utility class
 */
public abstract class TestUtil {

  public static synchronized void resolveInfrastructureNodes(final NodeTemplateInstance nodeTemplateInstance,
                                                             final TestContext context,
                                                             final Set<NodeTemplateInstance> nodes) {

    final List<RelationshipTemplateInstance> outgoingRelations =
      nodeTemplateInstance.getOutgoingRelations().stream()
        .filter(r -> r.getTemplateType().equals(Types.dependsOnRelationType)
          || r.getTemplateType().equals(Types.deployedOnRelationType)
          || r.getTemplateType().equals(Types.hostedOnRelationType))
        .collect(Collectors.toList());

    for (final RelationshipTemplateInstance r : outgoingRelations) {

      final NodeTemplateInstance target = r.getTarget();
      final TNodeTemplate targetTemplate = context.getNodeTemplate(target);

      if (Utils.isSupportedInfrastructureNodeType(ModelUtils.getNodeBaseType(context.getCsar(), targetTemplate).getQName())
        || Utils.isSupportedCloudProviderNodeType(ModelUtils.getNodeBaseType(context.getCsar(), targetTemplate).getQName())) {
        nodes.add(target);
      }

      resolveInfrastructureNodes(target, context, nodes);
    }
  }

  public static synchronized void resolveChildNodes(final NodeTemplateInstance nodeTemplateInstance,
                                                    final TestContext context,
                                                    final Set<NodeTemplateInstance> nodes) {
    // Only follow deployedOn and hostedOn relations
    final List<RelationshipTemplateInstance> outgoingRelations =
      nodeTemplateInstance.getOutgoingRelations().stream()
        .filter(r -> r.getTemplateType().equals(Types.deployedOnRelationType)
          || r.getTemplateType().equals(Types.hostedOnRelationType))
        .collect(Collectors.toList());
    for (final RelationshipTemplateInstance r : outgoingRelations) {
      final NodeTemplateInstance target = r.getTarget();
      nodes.add(target);
      resolveChildNodes(target, context, nodes);
    }
  }

  public static synchronized <T> Map<String, String> map(final Set<T> nodes,
                                                         final Function<? super T, ? extends Map<String, String>> mapper) {
    return nodes.stream().map(mapper).filter(Objects::nonNull).collect(Hashtable::new, Map::putAll, Map::putAll);
  }
}
