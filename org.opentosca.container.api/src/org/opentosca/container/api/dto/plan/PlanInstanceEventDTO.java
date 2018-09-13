package org.opentosca.container.api.dto.plan;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.next.model.PlanInstanceEvent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "PlanInstanceEvent")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanInstanceEventDTO {

    @XmlElement(name = "Timestamp")
    private Date timestamp;

    @XmlElement(name = "Status")
    private String status;

    @XmlElement(name = "Type")
    private String type;

    @XmlElement(name = "Message")
    private String message;

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public static final class Converter {

        public static PlanInstanceEventDTO convert(final PlanInstanceEvent object) {
            final PlanInstanceEventDTO dto = new PlanInstanceEventDTO();

            dto.setMessage(object.getMessage());
            dto.setStatus(object.getStatus());
            dto.setTimestamp(object.getTimestamp());
            dto.setType(object.getType());

            return dto;
        }

    }
}
