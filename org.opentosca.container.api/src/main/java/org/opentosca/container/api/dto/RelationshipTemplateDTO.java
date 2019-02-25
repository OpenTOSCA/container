package org.opentosca.container.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "RelationshipTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationshipTemplateDTO extends ResourceSupport {

    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "RelationshipType")
    private String relationshipType;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @ApiModelProperty(name = "relationship_type")
    public String getRelationshipType() {
        return this.relationshipType;
    }

    public void setRelationshipType(final String relationshipType) {
        this.relationshipType = relationshipType;
    }
    
    public static RelationshipTemplateDTO fromToscaObject(TRelationshipTemplate toscaObject) {
        RelationshipTemplateDTO dto = new RelationshipTemplateDTO();
        dto.id = toscaObject.getId();
        dto.name = toscaObject.getName();
        dto.relationshipType = toscaObject.getType().toString();
        return dto;
    }
}
