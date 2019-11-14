package org.opentosca.planbuilder.model.plan;

public abstract class AbstractActivity {

    private final String id;
    private final ActivityType type;

    public AbstractActivity(final String id, final ActivityType type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public ActivityType getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractActivity)) {
            return false;
        }

        AbstractActivity act = (AbstractActivity) obj;

        if (!act.getId().equals(this.id)) {
            return false;
        }
        if (!act.getType().equals(this.getType())) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return this.id + ":" + this.type;
    }

    @Override
    public String toString() {
        return "Activity: " + this.id + " Type: " + this.type;
    }
}
