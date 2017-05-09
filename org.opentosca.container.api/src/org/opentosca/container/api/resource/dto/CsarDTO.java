package org.opentosca.container.api.resource.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "csar")
public class CsarDTO extends ResourceSupport {
	
	private String id;

	private String name;

	private String displayName;

	private String version;

	private List<String> authors;
	
	private String description;

	private String iconUrl;
	
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
	
	@XmlElement(name = "author")
	@XmlElementWrapper(name = "authors")
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
	
	public void setCsarName(final String name) {
		this.name = name;
	}
}
