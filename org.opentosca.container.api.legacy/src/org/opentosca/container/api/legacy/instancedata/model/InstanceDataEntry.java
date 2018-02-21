package org.opentosca.container.api.legacy.instancedata.model;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 *
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 *
 */
@XmlRootElement(name = "InstanceDataAPI")
public class InstanceDataEntry {

    private final String version = "0.1";

    private List<SimpleXLink> links = new LinkedList<>();


    protected InstanceDataEntry() {
        super();
    }

    public InstanceDataEntry(final List<SimpleXLink> links) {
        super();
        this.links = links;
    }

    @XmlAttribute(name = "version", required = true)
    public String getVersion() {
        return this.version;
    }

    @XmlElement(name = "Link")
    public List<SimpleXLink> getLinks() {
        return this.links;
    }

    public String toJSON() {

        final JsonObject ret = new JsonObject();
        final JsonArray refs = new JsonArray();

        for (final SimpleXLink ref : this.links) {
            final JsonObject obj = new JsonObject();
            obj.addProperty("type", ref.getType());
            obj.addProperty("href", ref.getHref());
            obj.addProperty("title", ref.getTitle());
            refs.add(obj);
        }
        ret.add("References", refs);

        return ret.toString();
    }

}
