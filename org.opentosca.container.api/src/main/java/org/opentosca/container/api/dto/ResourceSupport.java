package org.opentosca.container.api.dto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Resources")
public class ResourceSupport {

  @ApiModelProperty(hidden = true)
  @JsonSerialize(using = LinksSerializer.class)
  private final List<Link> links = new ArrayList<>();


  public ResourceSupport() {

  }

  /**
   * Returns all {@link Link}s contained in this resource.
   */
  @XmlElement(name = "Link")
  @XmlElementWrapper(name = "Links")
  @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
  @JsonProperty("_links")
  public List<Link> getLinks() {
    return this.links;
  }

  /**
   * Adds the given link to the resource.
   */
  public void add(final Link link) {
    Objects.requireNonNull(link, "Link must not be null!");
    this.links.add(link);
  }

  /**
   * Adds all given {@link Link}s to the resource.
   */
  public void add(final Iterable<Link> links) {
    Objects.requireNonNull(links, "Given links must not be null!");
    for (final Link candidate : links) {
      this.add(candidate);
    }
  }

  /**
   * Adds all given {@link Link}s to the resource.
   *
   * @param links must not be {@literal null}.
   */
  public void add(final Link... links) {
    Objects.requireNonNull(links, "Given links must not be null!");
    this.add(Arrays.asList(links));
  }

  /**
   * Returns whether the resource contains {@link Link}s at all.
   */
  public boolean hasLinks() {
    return !this.links.isEmpty();
  }

  /**
   * Returns whether the resource contains a {@link Link} with the given rel.
   */
  public boolean hasLink(final String rel) {
    return getLink(rel) != null;
  }

  /**
   * Removes all {@link Link}s added to the resource so far.
   */
  public void removeLinks() {
    this.links.clear();
  }

  /**
   * Returns the link with the given rel.
   *
   *  @return the link with the given rel or {@literal null} if none found.
   */
  public Link getLink(final String rel) {
    for (final Link link : this.links) {
      if (link.getRel().equals(rel)) {
        return link;
      }
    }
    return null;
  }

  /**
   * Returns all {@link Link}s with the given relation type.
   *
   * @return the links in a {@link List}
   */
  public List<Link> getLinks(final String rel) {
    final List<Link> relatedLinks = new ArrayList<>();
    for (final Link link : this.links) {
      if (link.getRel().equals(rel)) {
        relatedLinks.add(link);
      }
    }
    return relatedLinks;
  }

  @Override
  public String toString() {
    return String.format("links: %s", this.links.toString());
  }

  public static class LinksSerializer extends JsonSerializer<List<Link>> {

    @Override
    public void serialize(final List<Link> links, final JsonGenerator json,
                          final SerializerProvider provider) throws IOException, JsonProcessingException {
      if (links.isEmpty()) {
        return;
      }
      final LinkSerializer delegate = new LinkSerializer();
      json.writeStartObject();
      for (final Link link : links) {
        delegate.serialize(link, json, provider);
      }
      json.writeEndObject();
    }
  }

  public static class LinkSerializer extends JsonSerializer<Link> {

    @Override
    public void serialize(final Link link, final JsonGenerator json,
                          final SerializerProvider provider) throws IOException, JsonProcessingException {
      if (link.getUri() == null || link.getRel() == null || link.getRel().isEmpty()) {
        return;
      }
      json.writeObjectFieldStart(link.getRel());
      json.writeStringField("href", link.getUri().toString());
      if (link.getTitle() != null && !link.getTitle().isEmpty()) {
        json.writeStringField(Link.TITLE, link.getTitle());
      }
      if (link.getType() != null && !link.getType().isEmpty()) {
        json.writeStringField(Link.TYPE, link.getType());
      }
      json.writeEndObject();
    }
  }
}
