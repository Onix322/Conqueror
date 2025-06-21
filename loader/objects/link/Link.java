package loader.objects.link;

import loader.objects.Dependency;

import java.net.URI;
import java.util.Objects;

public class Link {
    private Dependency dependency;
    private URI uri;
    private LinkExtension extension;

    public Link(Dependency dependency, URI uri, LinkExtension extension) {
        this.dependency = dependency;
        this.uri = uri;
        this.extension = extension;
    }

    public LinkExtension getExtension() {
        return extension;
    }

    public void setExtension(LinkExtension extension) {
        this.extension = extension;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }


    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Link link = (Link) object;
        return Objects.equals(getDependency(), link.getDependency()) && Objects.equals(getUri(), link.getUri()) && getExtension() == link.getExtension();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDependency(), getUri(), getExtension());
    }

    @Override
    public String toString() {
        return "Link{" +
                "dependency=" + dependency +
                ", uri=" + uri +
                ", extension=" + extension +
                '}';
    }
}
