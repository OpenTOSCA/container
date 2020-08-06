package org.opentosca.planbuilder.model.plan;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractActivity {

    private final String id;
    private final ActivityType type;
    private final Map<String, Object> metadata;

    public AbstractActivity(final String id, final ActivityType type) {
        this.id = id;
        this.type = type;
        this.metadata = new HashMap<String, Object>();
    }

    public AbstractActivity(final String id, final ActivityType type, final Map<String, Object> metadata) {
        this.id = id;
        this.type = type;
        this.metadata = metadata;
    }

    public String getId() {
        return this.id;
    }

    public ActivityType getType() {
        return this.type;
    }

    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    public Object addMetadata(final String name, final Object value) {
        return this.metadata.put(name, value);
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
        return "Activity: " + this.id + " Type: " + this.type;
    }
}
