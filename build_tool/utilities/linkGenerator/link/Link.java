package build_tool.utilities.linkGenerator.link;


import build_tool.utilities.depsReader.supportedTagsClasses.artifact.Artifact;

import java.net.URI;
import java.util.Objects;

/**
 * Represents a link associated with an artifact, a URI, and an optional link extension.
 * Provides methods for accessing and modifying these properties.
 */
public class Link  {
    private Artifact artifact;
    private URI uri;
    private LinkExtension linkExtension;

    public Link(Artifact artifact, URI uri, LinkExtension linkExtension) {
        this.artifact = artifact;
        this.uri = uri;
        this.linkExtension = linkExtension;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public Link setArtifact(Artifact artifact) {
        this.artifact = artifact;
        return this;
    }

    public URI getUri() {
        return uri;
    }

    public Link setUri(URI uri) {
        this.uri = uri;
        return this;
    }

    public LinkExtension getLinkExtension() {
        return linkExtension;
    }

    public Link setLinkExtension(LinkExtension linkExtension) {
        this.linkExtension = linkExtension;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Link link = (Link) object;
        return Objects.equals(getArtifact(), link.getArtifact()) && Objects.equals(getUri(), link.getUri()) && getLinkExtension() == link.getLinkExtension();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getArtifact(), getUri(), getLinkExtension());
    }

    @Override
    public String toString() {
        return "Link{" +
                "artifact=" + artifact +
                ", uri=" + uri +
                ", linkExtension=" + linkExtension +
                '}';
    }
}
