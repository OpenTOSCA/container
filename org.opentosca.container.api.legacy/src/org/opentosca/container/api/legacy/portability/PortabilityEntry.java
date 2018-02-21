package org.opentosca.container.api.legacy.portability;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.legacy.instancedata.model.SimpleXLink;

/**
 *
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
@XmlRootElement(name = "PortabilityAPI")
public class PortabilityEntry {

    private final String version = "0.1";

    private List<SimpleXLink> links = new LinkedList<>();

    protected PortabilityEntry() {
        super();
    }

    public PortabilityEntry(final List<SimpleXLink> links) {
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

}
