package org.opentosca.planbuilder.core.plugins.context;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TNodeTemplate;

public class DeployTechDescriptorMapping {
    private final Collection<PropertyVariable> propVars;

    public DeployTechDescriptorMapping() {
        propVars = new HashSet<>();
    }

    public Optional<PropertyVariable> getVariableByNodeAndProp(TNodeTemplate nodeTemplate, String descriptorProp) {
        return propVars.stream()
            .filter(propVar -> Objects.equals(propVar.getNodeTemplate(), nodeTemplate))
            .filter(propVar -> Objects.equals(propVar.getPropertyName(), descriptorProp))
            .findAny();
    }

    public Collection<PropertyVariable> getVariablesByNode(TNodeTemplate nodeTemplate) {
        return propVars.stream()
            .filter(propVar -> Objects.equals(propVar.getNodeTemplate(), nodeTemplate))
            .collect(Collectors.toSet());
    }

    public void addVarMapping(PropertyVariable propVar) {
        propVars.add(propVar);
    }
}
