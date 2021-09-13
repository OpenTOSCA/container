package org.opentosca.planbuilder.model.plan;

import org.eclipse.winery.model.tosca.TRelationshipTemplate;


public class RelationshipTemplateActivity extends AbstractActivity {

    private final TRelationshipTemplate relationshipTemplate;

    public RelationshipTemplateActivity(final String id, final ActivityType type,
                                        final TRelationshipTemplate relationshipTemplate) {
        super(id, type);
        this.relationshipTemplate = relationshipTemplate;
    }

    public TRelationshipTemplate getRelationshipTemplate() {
        return this.relationshipTemplate;
    }
}
