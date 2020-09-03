package org.opentosca.container.core.plan;

import java.util.Objects;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;

import org.springframework.stereotype.Service;

@Service
public class ChoreographyHandler {

    /**
     * Check if the given ServiceTemplate is part of a choreographed application deployment
     */
    public boolean isChoreography(final TServiceTemplate serviceTemplate) {
        if (Objects.isNull(serviceTemplate.getTags())) {
            return false;
        }

        return serviceTemplate.getTags().getTag().stream()
            .anyMatch(tag -> tag.getName().equals("choreography"));
    }
}
