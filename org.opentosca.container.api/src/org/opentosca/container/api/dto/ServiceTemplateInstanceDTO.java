package org.opentosca.container.api.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "ServiceTemplateInstance")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTemplateInstanceDTO extends ResourceSupport {

    private Long id;

    private Date createdAt;

    private String csarId;

    private String serviceTemplateId;

    private ServiceTemplateInstanceState state;


    @XmlAttribute
    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @XmlElement(name = "CreatedAt")
    @ApiModelProperty(name = "created_at")
    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    @XmlElement(name = "CsarId")
    @ApiModelProperty(name = "csar_id")
    public String getCsarId() {
        return this.csarId;
    }

    public void setCsarId(final String csarId) {
        this.csarId = csarId;
    }

    @XmlElement(name = "ServiceTemplateId")
    @ApiModelProperty(name = "service_template_id")
    public String getServiceTemplateId() {
        return this.serviceTemplateId;
    }

    public void setServiceTemplateId(final String serviceTemplateId) {
        this.serviceTemplateId = serviceTemplateId;
    }

    public ServiceTemplateInstanceState getState() {
        return this.state;
    }

    public void setState(final ServiceTemplateInstanceState state) {
        this.state = state;
    }

    public static final class Converter {

        public static ServiceTemplateInstanceDTO convert(final ServiceTemplateInstance object) {
            final ServiceTemplateInstanceDTO dto = new ServiceTemplateInstanceDTO();

            dto.setId(object.getId());
            dto.setCreatedAt(object.getCreatedAt());
            dto.setCsarId(object.getCsarId().toString());
            dto.setServiceTemplateId(object.getTemplateId().toString());
            dto.setState(object.getState());

            return dto;
        }
    }
}
