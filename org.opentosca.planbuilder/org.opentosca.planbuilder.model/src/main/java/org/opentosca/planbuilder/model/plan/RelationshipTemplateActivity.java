package org.opentosca.planbuilder.model.plan;

import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;

public class RelationshipTemplateActivity extends AbstractActivity {

    private final AbstractRelationshipTemplate relationshipTemplate;

    public RelationshipTemplateActivity(final String id, final ActivityType type,
                                        final AbstractRelationshipTemplate relationshipTemplate) {
        super(id, type);
        this.relationshipTemplate = relationshipTemplate;
    }

    public AbstractRelationshipTemplate getRelationshipTemplate() {
        return this.relationshipTemplate;
    }
}
