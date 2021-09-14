package org.opentosca.container.core.model.choreography;

import java.net.URL;

public class SituationRule {

    private final URL situationRuleUrl;

    private final String situationCompliantPartnerName;

    private final URL situationCompliantPartnerUrl;

    private final String alternativePartnerName;

    private final URL alternativePartnerUrl;

    public SituationRule (URL situationRuleUrl, String situationCompliantPartnerName, URL situationCompliantPartnerUrl, String alternativePartnerName, URL alternativePartnerUrl) {
    	this.situationRuleUrl = situationRuleUrl;
    	this.situationCompliantPartnerName = situationCompliantPartnerName;
    	this.situationCompliantPartnerUrl = situationCompliantPartnerUrl;
    	this.alternativePartnerName = alternativePartnerName;
    	this.alternativePartnerUrl = alternativePartnerUrl;
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
