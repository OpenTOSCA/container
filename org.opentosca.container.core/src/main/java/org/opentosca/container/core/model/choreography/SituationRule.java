package org.opentosca.container.core.model.choreography;

import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SituationRule {

    private URL situationRuleUrl;

    private String situationCompliantPartnerName;

    private URL situationCompliantPartnerUrl;

    private String alternativePartnerName;

    private URL alternativePartnerUrl;
}
