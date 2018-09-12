package org.opentosca.container.api.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.winery.model.selfservice.Application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Csar")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsarDTO extends ResourceSupport {

    private String id;

    private String name;

    private String displayName;

    private String version;

    private List<String> authors;

    private String description;

    private String iconUrl;

    private String imageUrl;


    public CsarDTO() {}

    @XmlAttribute
    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @XmlElement(name = "Name")
    public String getName() {
        return this.name;
    }

    @JsonSetter
    public void setName(final String name) {
        this.name = name;
    }

    @ApiModelProperty(name = "display_name")
    @XmlElement(name = "DisplayName")
    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    @XmlElement(name = "Version")
    public String getVersion() {
        return this.version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    @XmlElement(name = "Description")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @ApiModelProperty(name = "icon_url")
    @XmlElement(name = "IconUrl")
    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(final String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @ApiModelProperty(name = "image_url")
    @XmlElement(name = "ImageUrl")
    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @XmlElement(name = "Author")
    @XmlElementWrapper(name = "Authors")
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
