package org.opentosca.container.core.model.choreography;

import java.net.URL;

public class SituationRule {

    private URL situationRuleUrl;

    private String situationCompliantPartnerName;

    private URL situationCompliantPartnerUrl;

    private String alternativePartnerName;

    private URL alternativePartnerUrl;
    
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
