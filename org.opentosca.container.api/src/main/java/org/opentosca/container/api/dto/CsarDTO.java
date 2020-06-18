package org.opentosca.container.api.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.winery.model.selfservice.Application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Csar")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsarDTO extends ResourceSupport {

    @JsonProperty
    @XmlAttribute
    private String id;

    @JsonProperty
    @XmlElement(name = "Name")
    private String name;

    @JsonProperty
    @ApiModelProperty(name = "display_name")
    @XmlElement(name = "DisplayName")
    private String displayName;

    @JsonProperty
    @XmlElement(name = "Version")
    private String version;

    @JsonProperty
    @XmlElement(name = "Author")
    @XmlElementWrapper(name = "Authors")
    private List<String> authors;

    @JsonProperty
    @XmlElement(name = "Description")
    private String description;

    @JsonProperty
    @ApiModelProperty(name = "icon_url")
    @XmlElement(name = "IconUrl")
    private String iconUrl;

    @JsonProperty
    @ApiModelProperty(name = "image_url")
    @XmlElement(name = "ImageUrl")
    private String imageUrl;

    public CsarDTO() {
    }

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

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(final String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getAuthors() {
        return this.authors;
    }

    public void setAuthors(final List<String> authors) {
        this.authors = authors;
    }

    public void addAuthors(final String... authors) {
        if (this.authors == null) {
            this.authors = new ArrayList<>();
        }
        this.authors.addAll(Arrays.asList(authors));
    }

    public static final class Converter {

        public static CsarDTO convert(final Application object) {
            final CsarDTO csar = new CsarDTO();

            csar.setName(object.getCsarName());
            csar.setDisplayName(object.getDisplayName());
            csar.setDescription(object.getDescription());
            csar.setAuthors(object.getAuthors());
            csar.setVersion(object.getVersion());
            csar.setIconUrl(object.getIconUrl());
            csar.setImageUrl(object.getImageUrl());

            return csar;
        }
    }
}
