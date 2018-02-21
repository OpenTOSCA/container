package org.opentosca.planbuilder.model.plan;

public abstract class AbstractActivity {

    private final String id;
    private final String type;

    public AbstractActivity(final String id, final String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

}
