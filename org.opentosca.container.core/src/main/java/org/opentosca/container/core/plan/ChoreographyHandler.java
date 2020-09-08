package org.opentosca.container.core.plan;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.HttpMethod;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.opentosca.container.core.model.choreography.SituationRule;
import org.opentosca.container.core.model.csar.Csar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChoreographyHandler {

    private final static Logger LOG = LoggerFactory.getLogger(ChoreographyHandler.class);

    private final static QName LOCATION_ATTRIBUTE = QName.valueOf("{http://www.opentosca.org/winery/extensions/tosca/2013/02/12}location");

    /**
     * Check if the given ServiceTemplate is part of a choreographed application deployment
     */
    public boolean isChoreography(final TServiceTemplate serviceTemplate) {
        return Objects.nonNull(getTagWithName(serviceTemplate, "choreography"));
    }

    /**
     * Get the participant tag of the given ServiceTemplate
     */
    public String getInitiator(final TServiceTemplate serviceTemplate) {
        return getTagWithName(serviceTemplate, "participant");
    }

    /**
     * Get the app_chor_id tag of the given ServiceTemplate
     */
    public String getAppChorId(final TServiceTemplate serviceTemplate) {
        return getTagWithName(serviceTemplate, "app_chor_id");
    }

    /**
     * Get the situation rules to decide which partner to include in a choreography
     */
    public List<SituationRule> getSituationRules(final TServiceTemplate serviceTemplate) {
        List<SituationRule> situationRules = new ArrayList<>();

        // get tag containing the situation rules
        String situationRuleTag = getTagWithName(serviceTemplate, "partnerselection_rules");
        if (Objects.nonNull(situationRuleTag)) {
            String[] situationRuleCandidates = situationRuleTag.split(";");
            LOG.debug("Found {} situation rule candidate(s)!", situationRuleCandidates.length);

            // check validity of rules and parse to rules object
            for (String situationRuleCandidate : situationRuleCandidates) {

                // each rule requires a situation and two alternative partners
                String[] situationRuleParts = situationRuleCandidate.split(",");
                if (situationRuleParts.length != 3) {
                    continue;
                }

                String situationUrl = getTagWithName(serviceTemplate, situationRuleParts[0]);
                String situationCompliantPartnerUrl = getTagWithName(serviceTemplate, situationRuleParts[1]);
                String alternativePartnerUrl = getTagWithName(serviceTemplate, situationRuleParts[2]);

                if (Objects.nonNull(situationUrl) && Objects.nonNull(situationCompliantPartnerUrl) && Objects.nonNull(alternativePartnerUrl)) {
                    try {
                        situationRules.add(new SituationRule(new URL(situationUrl), situationRuleParts[1], new URL(situationCompliantPartnerUrl), situationRuleParts[2], new URL(alternativePartnerUrl)));
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
     * Get the list of involved partners based on available selection rules
     *
     * @param situationRules a list of situation rules to filter the required partners
     * @param possiblePartners a list of possible partners from the ServiceTemplate tags
     * @return a list of filtered partners
     */
    public List<String> getPartnersBasedOnSelectionRule(List<SituationRule> situationRules, List<String> possiblePartners) {
        List<String> partners = new ArrayList<>();

        // check all situation rules and add the corresponding partners
        for (SituationRule situationRule : situationRules) {
            possiblePartners.remove(situationRule.getSituationCompliantPartnerName());
            possiblePartners.remove(situationRule.getAlternativePartnerName());

            if (isSituationRuleActive(situationRule.getSituationRuleUrl())) {
                LOG.debug("Adding compliant partner '{}' for situation with URL: {}", situationRule.getSituationCompliantPartnerName(), situationRule.getSituationRuleUrl());
                partners.add(situationRule.getSituationCompliantPartnerName());
            } else {
                LOG.debug("Adding alternative partner '{}' for situation with URL: {}", situationRule.getAlternativePartnerName(), situationRule.getSituationRuleUrl());
                partners.add(situationRule.getAlternativePartnerName());
            }
        }

        LOG.debug("Number of situation independent partners: {}", possiblePartners.size());
        LOG.debug("Number of situation dependent partners: {}", partners.size());
        partners.addAll(possiblePartners);

        return partners;
    }

    /**
     * Check if the situation on the given URL is active
     *
     * @param situationUrl the URL to the situation rule to evaluate
     * @return <code>true</code> if the referenced situation rule is active, <code>false</code> otherwise
     */
    private boolean isSituationRuleActive(URL situationUrl) {
        try {
            // retrieve situation
            HttpURLConnection connection = (HttpURLConnection) situationUrl.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(HttpMethod.GET);
            connection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
            connection.connect();
            String json = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);

            // read active part and parse to boolean to check if situation is active
            Map<String, Object> map = new ObjectMapper().readValue(json, Map.class);
            if (!map.containsKey("active")) {
                LOG.warn("Situation at URL '{}' is invalid!", situationUrl);
                return false;
            }
            return Boolean.parseBoolean(map.get("active").toString());
        } catch (IOException e) {
            LOG.debug("Unable to parse situation from URL {}: {}", situationUrl, e.getMessage());
            return false;
        }
    }

    /**
     * Get the endpoints of all choreography partners from the ServiceTemplate.
     *
     * @param serviceTemplate the ServiceTemplate for the choreography
     * @return a list of tags containing the partner name as key and the endpoints as value or
     * <code>null</code> if no tags are defined on the ServiceTemplate
     */
    public List<TTag> getPartnerEndpoints(final TServiceTemplate serviceTemplate) {

        // get the tags containing the endpoints of the partners
        if (Objects.isNull(serviceTemplate.getTags())) {
            LOG.error("Unable to retrieve tags for ServiceTemplate with ID {}.", serviceTemplate.getId());
            return null;
        }

        List<TTag> tags = Lists.newArrayList(serviceTemplate.getTags().getTag().iterator());
        LOG.debug("Number of tags: {}", tags.size());

        // get the provider names defined in the NodeTemplates to check which tag names specify a partner endpoint
        final List<String> partnerNames =
            serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().stream()
                .filter(entity -> entity instanceof TNodeTemplate).map(TExtensibleElements::getOtherAttributes)
                .map(attributes -> attributes.get(LOCATION_ATTRIBUTE))
                .flatMap(locationString -> Arrays.stream(locationString.split(",")))
                .distinct()
                .collect(Collectors.toList());
        LOG.debug("Number of partners: {}", partnerNames.size());

        // remove tags that do not specify a partner endpoint and get endpoints
        tags.removeIf(tag -> !partnerNames.contains(tag.getName()));

        LOG.debug("Number of tags after filtering for partners: {}", tags.size());
        return tags;
    }
    
    public String getPossiblePartners(final TNodeTemplate nodeTemplate, Collection<String> participants) {
    	if(nodeTemplate.getOtherAttributes().get(LOCATION_ATTRIBUTE) != null) {
    		for(String participant : nodeTemplate.getOtherAttributes().get(LOCATION_ATTRIBUTE).split(",")) {
    			if(participants.contains(participant)) {
    				return participant;
    			}
    		}
    	}
    	return null;
    }

    /**
     * Get the tag with the given name
     */
    private String getTagWithName(final TServiceTemplate serviceTemplate, String tagName) {
        if (Objects.isNull(serviceTemplate.getTags())) {
            return null;
        }

        for (TTag tag : serviceTemplate.getTags().getTag()) {
            if (tag.getName().equals(tagName)) {
                return tag.getValue();
            }
        }
        return null;
    }
    

    
    public Csar getChoreographyCsar(String appChoreoId, Collection<Csar> csars) {
    	for(Csar csar : csars) {
    		TServiceTemplate serviceTemplate = csar.entryServiceTemplate();
    		String tagAppChorId = new ChoreographyHandler().getAppChorId(serviceTemplate);
    		
    		if(Objects.nonNull(tagAppChorId) && tagAppChorId.equals(appChoreoId)) {
    			return csar;    			
    		}   		
    	} 
    	return null;
    }
}
