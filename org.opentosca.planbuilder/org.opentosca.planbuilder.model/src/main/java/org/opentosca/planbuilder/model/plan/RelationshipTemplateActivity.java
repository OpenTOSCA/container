package org.opentosca.planbuilder.model.plan;

import org.eclipse.winery.model.tosca.TRelationshipTemplate;

public class RelationshipTemplateActivity extends AbstractActivity {

    private final TRelationshipTemplate relationshipTemplate;
    private int visitedCounter;

    public RelationshipTemplateActivity(final String id, final ActivityType type,
                                        final TRelationshipTemplate relationshipTemplate) {
        super(id, type);
        this.relationshipTemplate = relationshipTemplate;
        this.visitedCounter = 0;
    }

    public TRelationshipTemplate getRelationshipTemplate() {
        return this.relationshipTemplate;
    }

    public int getVisitedCounter() {
        return this.visitedCounter;
    }
     public void setVisitedCounter () {
        this.visitedCounter++;
     }
}