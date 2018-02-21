package org.opentosca.planbuilder.model.plan;

import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;

public class ARelationshipTemplateActivity extends AbstractActivity {

    private final AbstractRelationshipTemplate relationshipTemplate;

    public ARelationshipTemplateActivity(final String id, final String type,
                                         final AbstractRelationshipTemplate relationshipTemplate) {
        super(id, type);
        this.relationshipTemplate = relationshipTemplate;
    }

    public AbstractRelationshipTemplate getRelationshipTemplate() {
        return this.relationshipTemplate;
    }

}
