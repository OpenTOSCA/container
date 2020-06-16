package org.opentosca.container.api.dto.situations;

import java.util.Collection;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Maps;
import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.next.model.SituationsMonitor;

@XmlRootElement(name = "SituationsMonitor")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SituationsMonitorDTO extends ResourceSupport {

    @XmlAttribute(name = "id")
    private Long id;

    @XmlElementWrapper(name = "NodeIds2SituationIds")
    private Map<String, SituationIdsWrapper> nodeId2situationIds;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Map<String, SituationIdsWrapper> getNodeId2SituationIds() {
        return this.nodeId2situationIds;
    }

    public void setNodeId2SituationIds(final Map<String, SituationIdsWrapper> nodeId2situationIds) {
        this.nodeId2situationIds = nodeId2situationIds;
    }

    public static final class Converter {
        public static SituationsMonitorDTO convert(final SituationsMonitor object) {
            final SituationsMonitorDTO dto = new SituationsMonitorDTO();
            dto.setId(object.getId());

            Map<String, SituationIdsWrapper> wrap = Maps.newHashMap();

            for (String key : object.getNode2Situations().keySet()) {
                SituationIdsWrapper w = new SituationIdsWrapper();
                w.setSituationId(object.getNode2Situations().get(key));
                wrap.put(key, w);
            }

            dto.setNodeId2SituationIds(wrap);

            return dto;
        }
    }

    public static class SituationIdsWrapper {

        @XmlElementWrapper(name = "SituationIdsList")
        @XmlElement(name = "situationId")
        private Collection<Long> situationIds;

        public void setSituationId(Collection<Long> situationId) {
            this.situationIds = situationId;
        }

        public Collection<Long> getSituationId() {
            return this.situationIds;
        }
    }
}
