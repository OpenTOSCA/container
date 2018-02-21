package org.opentosca.container.core.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.opentosca.container.core.model.csar.id.CSARID;

public class ReferenceMap implements Map<CSARID, MapQNameNode> {

    private Map<CSARID, MapQNameNode> csarIDToMapQNameNode = new HashMap<>();


    @Override
    public void clear() {

        this.csarIDToMapQNameNode.clear();
    }

    @Override
    public boolean containsKey(final Object key) {

        return this.csarIDToMapQNameNode.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {

        return this.csarIDToMapQNameNode.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<CSARID, MapQNameNode>> entrySet() {

        return this.csarIDToMapQNameNode.entrySet();
    }

    @Override
    public MapQNameNode get(final Object key) {

        return this.csarIDToMapQNameNode.get(key);
    }

    @Override
    public boolean isEmpty() {

        return this.csarIDToMapQNameNode.isEmpty();
    }

    @Override
    public Set<CSARID> keySet() {

        return this.csarIDToMapQNameNode.keySet();
    }

    @Override
    public MapQNameNode put(final CSARID key, final MapQNameNode value) {

        final MapQNameNode result = this.csarIDToMapQNameNode.put(key, value);
        return result;
    }

    @Override
    public void putAll(final Map<? extends CSARID, ? extends MapQNameNode> m) {

        this.csarIDToMapQNameNode.putAll(m);
    }

    @Override
    public MapQNameNode remove(final Object key) {

        final MapQNameNode result = this.csarIDToMapQNameNode.remove(key);
        return result;
    }

    @Override
    public int size() {

        return this.csarIDToMapQNameNode.size();
    }

    @Override
    public Collection<MapQNameNode> values() {

        return this.csarIDToMapQNameNode.values();
    }

    public Map<CSARID, MapQNameNode> getCsarIDToMapQNameNode() {

        return this.csarIDToMapQNameNode;
    }

    public void setCsarIDToMapQNameNode(final Map<CSARID, MapQNameNode> referenceMap) {

        this.csarIDToMapQNameNode = referenceMap;
    }
}
