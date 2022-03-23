package org.opentosca.container.core.model.choreography;

import java.net.URL;

public class SituationRule {

    private final URL situationRuleUrl;

    private final String situationCompliantPartnerName;

    private final String alternativePartnerName;

    public SituationRule(URL situationRuleUrl, String situationCompliantPartnerName, String alternativePartnerName) {
        this.situationRuleUrl = situationRuleUrl;
        this.situationCompliantPartnerName = situationCompliantPartnerName;
        this.alternativePartnerName = alternativePartnerName;
    }

    public String getSituationCompliantPartnerName() {
        return this.situationCompliantPartnerName;
    }

    public String getAlternativePartnerName() {
        return this.alternativePartnerName;
    }

    public URL getSituationRuleUrl() {
        return this.situationRuleUrl;
    }
}
