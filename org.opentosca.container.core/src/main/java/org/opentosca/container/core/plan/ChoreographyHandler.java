package org.opentosca.container.core.plan;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;

import org.opentosca.container.core.model.choreography.SituationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChoreographyHandler {

    private final static Logger LOG = LoggerFactory.getLogger(ChoreographyHandler.class);

    /**
     * Check if the given ServiceTemplate is part of a choreographed application deployment
     */
    public boolean isChoreography(final TServiceTemplate serviceTemplate) {
        return Objects.nonNull(getTagWithName(serviceTemplate, "choreography"));
    }

    /**
     * Check if the given ServiceTemplate is part of a choreographed application deployment
     */
    public String getInitiator(final TServiceTemplate serviceTemplate) {
       return getTagWithName(serviceTemplate,"participant");
    }

    /**
     * Get the situation rules to decide which partner to include in a choreography
     */
    public List<SituationRule> getSituationRules(final TServiceTemplate serviceTemplate) {
        List<SituationRule> situationRules = new ArrayList<>();

        // get tag containing the situation rules
        String situationRuleTag = getTagWithName(serviceTemplate, "rules");
        if (Objects.nonNull(situationRuleTag)) {
            String[] situationRuleCandidates = situationRuleTag.split(";");
            LOG.debug("Found {} situation rule candidates!", situationRuleCandidates.length);

            // check validity of rules and parse to rules object
            for (String situationRuleCandidate : situationRuleCandidates) {

                // each rule requires a situation and two alternative partners
                String[] situationRuleParts = situationRuleCandidate.split(",");
                if (situationRuleParts.length != 3){
                    continue;
                }

                String situationUrl = getTagWithName(serviceTemplate, situationRuleParts[0]);
                String situationCompliantPartnerUrl = getTagWithName(serviceTemplate, situationRuleParts[1]);
                String alternativePartnerUrl = getTagWithName(serviceTemplate, situationRuleParts[2]);

                if (Objects.nonNull(situationUrl) && Objects.nonNull(situationCompliantPartnerUrl) && Objects.nonNull(alternativePartnerUrl)){
                    try {
                        situationRules.add(new SituationRule(new URL(situationUrl), new URL(situationCompliantPartnerUrl), new URL(alternativePartnerUrl)));
                    } catch (MalformedURLException e) {
                        LOG.error("Unable to generate situation rule because of malformed URL: {}", e.getMessage());
                    }
                } else {
                    LOG.warn("Unable to retrieve required URLs for rule with name '{}', situation compliant partner '{}', and alternative partner '{}'!",
                        situationRuleParts[0], situationRuleParts[1], situationRuleParts[2]);
                }
            }
        } else {
            LOG.warn("Unable to find situation rule tag!");
        }

        return situationRules;
    }

    /**
     * Get the tag with the given name
     */
    private String getTagWithName(final TServiceTemplate serviceTemplate, String tagName){
        if (Objects.isNull(serviceTemplate.getTags())) {
            return null;
        }

        for (TTag tag : serviceTemplate.getTags().getTag()){
            if (tag.getName().equals(tagName)) {
                return tag.getValue();
            }
        }
        return null;
    }
}
