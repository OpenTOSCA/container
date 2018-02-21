package org.opentosca.container.api.legacy.instancedata.model;

import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.model.instance.ServiceInstance;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 *
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
@XmlRootElement(name = "ServiceInstance")
public class ServiceInstanceEntry {

    private URI serviceInstanceID;
    private String csarID;
    private String serviceTemplateID;
    private String serviceTemplateName;
    private Date created;
    private List<SimpleXLink> links = new LinkedList<>();

    private List<SimpleXLink> nodeInstanceList = new LinkedList<>();

    private List<SimpleXLink> relationInstanceList = new LinkedList<>();

    /**
     * @param serviceInstanceID
     * @param csarID
     * @param serviceTemplateID
     * @param serviceTemplateName
     * @param created
     * @param links
     * @param nodeInstanceList
     */
    public ServiceInstanceEntry(final ServiceInstance si, final List<SimpleXLink> links,
                                final NodeInstanceList nodeInstanceList) {
        super();
        this.serviceInstanceID = si.getServiceInstanceID();
        this.csarID = si.getCSAR_ID().toString();
        this.serviceTemplateID = si.getServiceTemplateID().toString();
        this.serviceTemplateName = si.getServiceTemplateName();
        this.created = si.getCreated();

        this.links = links;
        this.nodeInstanceList = nodeInstanceList.getLinks();
    }

    public ServiceInstanceEntry(final ServiceInstance si, final List<SimpleXLink> links,
                                final RelationInstanceList relationInstanceList) {
        super();
        this.serviceInstanceID = si.getServiceInstanceID();
        this.csarID = si.getCSAR_ID().toString();
        this.serviceTemplateID = si.getServiceTemplateID().toString();
        this.serviceTemplateName = si.getServiceTemplateName();
        this.created = si.getCreated();

        this.links = links;
        this.relationInstanceList = relationInstanceList.getLinks();
    }

    protected ServiceInstanceEntry() {
        super();
    }

    @XmlAttribute(name = "serviceInstanceID", required = true)
    public URI getServiceInstanceID() {
        return this.serviceInstanceID;
    }

    @XmlAttribute(name = "csarID", required = true)
    public String getCsarID() {
        return this.csarID;
    }

    @XmlAttribute(name = "serviceTemplateID", required = true)
    public String getServiceTemplateID() {
        return this.serviceTemplateID;
    }

    @XmlAttribute(name = "serviceTemplateName")
    public String getServiceTemplateName() {
        return this.serviceTemplateName;
    }

    @XmlAttribute(name = "created-at")
    public Date getCreated() {
        return this.created;
    }

    @XmlElement(name = "Link")
    public List<SimpleXLink> getLinks() {
        return this.links;
    }

    @XmlElementWrapper(name = "nodeInstances")
    @XmlElement(name = "nodeInstance")
    public List<SimpleXLink> getNodeInstanceList() {
        return this.nodeInstanceList;
    }

    @XmlElementWrapper(name = "relationInstances")
    @XmlElement(name = "relationInstance")
    public List<SimpleXLink> getRelationInstanceList() {
        return this.relationInstanceList;
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
